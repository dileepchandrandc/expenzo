## Plan: Bank & Bank Account Backend APIs

**TL;DR** — Generate full CRUD + list APIs for `bank-account` with soft-delete, plus a read-only `GET /bank/list` endpoint. Response models embed `BankResponse` as a nested object inside `BankAccountResponse`. User context is extracted from the JWT auth context (SecurityContext). Follows existing codebase patterns: Lombok POJOs, Jakarta validation, `EntityNotFoundException`, manual DTO mapping.

---

## Phase 1: Foundation — Bank Entity & Bank List (no dependencies)

**Steps**
1. **Create `AccountType` enum** at `com.expenzo.services.bankaccount.enums.AccountType` — values: `SAVINGS`, `CURRENT`. Placed in an `enums/` sub-package so it can be shared cleanly between `repository/model/` entities and `request/` DTOs without cross-package coupling.

2. **Create `Bank` entity** at `com.expenzo.services.bankaccount.repository.model.Bank` — JPA `@Entity` mapped to `bank` table with fields: `id` (UUID PK), `name`, `shortName` (column `short_name`). Use Lombok `@Getter`/`@Setter`/`@NoArgsConstructor`/`@AllArgsConstructor`/`@Builder` pattern identical to `ExpenzoUser`.

3. **Create `BankRepository`** at `com.expenzo.services.bankaccount.repository.BankRepository` — extends `JpaRepository<Bank, String>`, annotated `@Repository`. Add `findAllByOrderByNameAsc()` for sorted bank list.

4. **Create `BankResponse` DTO** at `com.expenzo.services.bankaccount.dto.BankResponse` — fields: `id`, `name`, `shortName`. `@Getter`/`@Setter`.

5. **Create `BankService`** at `com.expenzo.services.bankaccount.service.BankService` — `@Service`, single method `list()` that calls `bankRepository.findAllByOrderByNameAsc()`, maps each `Bank` → `BankResponse` via private `toBankResponse()`.

6. **Create `BankController`** at `com.expenzo.services.bankaccount.controller.BankController` — `@RestController`, `@RequestMapping("/bank")`, single endpoint `GET /list` → returns `List<BankResponse>`.

---

## Phase 2: BankAccount CRUD (depends on Phase 1)

**Steps**
7. **Create `BankAccount` entity** at `com.expenzo.services.bankaccount.repository.model.BankAccount` — JPA `@Entity` mapped to `bank_account` table. Fields:
   - `id` (UUID PK, `@GeneratedValue(GenerationType.UUID)`)
   - `userId` (column `user_id`)
   - `bank` — `@ManyToOne(fetch = FetchType.LAZY)` + `@JoinColumn(name = "bank_id", nullable = false)` to `Bank`; the single source of truth for the issuing bank
   - `accountType` (column `account_type`, `@Enumerated(EnumType.STRING)`, `@Column(columnDefinition = "account_type_enum")`)
   - `accountNumber` (column `account_number`, `unique = true`)
   - `nickName` (column `nick_name`)
   - `createdAt`, `updatedAt` (columns `created_at`, `updated_at`, `TIMESTAMPTZ`)
   - `isActive` (column `is_active`, boolean, default `TRUE`)
   - `deletedAt` (column `deleted_at`, `TIMESTAMPTZ`, nullable)

   *Decision:* There is NO separate `bankId` field — the `bank` object relationship (`@ManyToOne` → `Bank`) is the single source of truth and maps the `bank_id` FK column directly. The service fetches the `Bank` entity from the DB and sets it on the `BankAccount` during create/update.

8. **Create `BankAccountRepository`** at `com.expenzo.services.bankaccount.repository.BankAccountRepository` — extends `JpaRepository<BankAccount, String>`. Custom query methods:
   - `findByIdAndUserIdAndDeletedAtIsNull(String id, String userId)` — for get-by-id (ownership check + soft-delete filter)
   - `findByUserIdAndDeletedAtIsNull(String userId, Pageable pageable)` — for paginated list (ownership check + soft-delete filter)

9. **Create request DTOs** in `com.expenzo.services.bankaccount.request/`:
   - `CreateBankAccountRequest` — `@NotBlank bankId`, `@NotNull AccountType accountType`, `@NotBlank @Size(max=50) accountNumber`, `@Size(max=50) nickName`. `@Getter`/`@Setter`.
   - `UpdateBankAccountRequest` — all fields optional: `bankId`, `accountType`, `accountNumber`, `nickName`. Only non-null fields are applied in the service. `@Getter`/`@Setter`.

10. **Create response DTOs** in `com.expenzo.services.bankaccount.dto/`:
    - `BankAccountResponse` — fields: `id`, `userId`, `bank` (type: `BankResponse`, populated from the `bank` relationship), `accountType`, `accountNumber`, `nickName`, `createdAt`, `updatedAt`, `isActive`. `@Getter`/`@Setter`.

11. **Create `BankAccountService`** at `com.expenzo.services.bankaccount.service.BankAccountService`:
    - Inject `BankAccountRepository`, `BankRepository`
    - **`getCurrentUserId()`** — private helper extracting `SecurityContextHolder.getContext().getAuthentication().getName()`
    - **`create(CreateBankAccountRequest)`** — fetches the `Bank` entity via `bankRepository.findById()` (throws if missing), builds `BankAccount` with `.bank(bank)` and userId from auth context, saves, returns `BankAccountResponse` with bank populated
    - **`update(String id, UpdateBankAccountRequest)`** — finds by id+userId+not-deleted, selectively updates non-null fields (`bankId`, `accountType`, `accountNumber`, `nickName`); when `bankId` is provided, fetches the `Bank` entity and calls `setBank(bank)`; sets `updatedAt = now()`, saves, returns response
    - **`get(String id)`** — finds by id+userId+not-deleted or throws `EntityNotFoundException`, returns response
    - **`delete(String id)`** — finds by id+userId+not-deleted, sets `deletedAt = now()` and `isActive = false`, saves (soft delete)
    - **`list(int page, int size)`** — calls `findByUserIdAndDeletedAtIsNull` with `PageRequest`, maps to `Page<BankAccountResponse>`, then wraps content into a generic `com.expenzo.services.common.PaginatedResponse<BankAccountResponse>` (content + hasNext + size)
    - **`toBankAccountResponse(BankAccount)`** — private mapper using `BankResponse` from the entity's bank relationship

12. **Create `BankAccountController`** at `com.expenzo.services.bankaccount.controller.BankAccountController`:
    - `@RestController`, `@RequestMapping("/bank-account")`
    - `POST /` → `@ResponseStatus(CREATED)`, `@Valid @RequestBody CreateBankAccountRequest` → `BankAccountResponse`
    - `PUT /{id}` → `@PathVariable String id`, `@Valid @RequestBody UpdateBankAccountRequest` → `BankAccountResponse`
    - `DELETE /{id}` → `@PathVariable String id`, `@ResponseStatus(NO_CONTENT)`, returns void
    - `GET /{id}` → `@PathVariable String id` → `BankAccountResponse`
    - `GET /list` → `@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size` → `PaginatedResponse<BankAccountResponse>`

---

## Phase 3: Verification

**Steps**
13. **Verify compilation** — run `./gradlew compileJava` from the backend root to ensure no compilation errors.

14. **Verify existing tests still pass** — run `./gradlew test` to ensure no regressions.

15. **Manual verification** — after starting the app, use Swagger UI at `/expenzo-services/swagger-ui/index.html` to test:
    - `GET /bank/list` — returns banks from DB
    - `POST /bank-account` — create with valid bankId
    - `GET /bank-account/{id}` — returns with nested bank object
    - `PUT /bank-account/{id}` — partial update works
    - `DELETE /bank-account/{id}` — soft-deletes (row remains with deleted_at set)
    - `GET /bank-account/list?page=0&size=10` — paginated list, excludes deleted

---

## Relevant Files

### New files to create (all under `src/main/java/com/expenzo/services/bankaccount/`):
- `enums/AccountType.java` — Java enum (shared across entity & request packages)
- `repository/model/Bank.java` — JPA entity
- `repository/model/BankAccount.java` — JPA entity with `@ManyToOne` to Bank
- `repository/BankRepository.java` — JPA repository
- `repository/BankAccountRepository.java` — JPA repository with custom queries
- `dto/BankResponse.java` — Bank response DTO
- `dto/BankAccountResponse.java` — BankAccount response DTO with nested BankResponse
- `request/CreateBankAccountRequest.java` — create request with Jakarta validation
- `request/UpdateBankAccountRequest.java` — update request (all optional)
- `service/BankService.java` — list banks
- `service/BankAccountService.java` — CRUD + soft-delete + paginated list, userId from auth
- `controller/BankController.java` — GET /bank/list
- `controller/BankAccountController.java` — full CRUD + list

### New file (generic, outside bankaccount package):
- `common/PaginatedResponse.java` — generic `PaginatedResponse<T>` with `content` (List<T>), `hasNext` (boolean), `size` (int); reused by list endpoints

### Existing files to reference (patterns):
- `auth/repository/model/ExpenzoUser.java` — entity pattern (Lombok, JPA, UUID PK)
- `auth/repository/ExpenzoUserRepository.java` — repository pattern
- `auth/service/UserAccountService.java` — service pattern (manual mapping, exception handling)
- `auth/controller/UserAccountController.java` — controller pattern
- `auth/config/GlobalExceptionHandler.java` — already handles `EntityNotFoundException`, `DataIntegrityViolationException`, etc.

### Existing files that may need modification:
- `auth/config/SecurityConfig.java` — no changes needed; `anyRequest().authenticated()` already covers `/bank-account/**` and `/bank/**` endpoints
- `build.gradle` — no changes needed; all required dependencies already present

---

## Decisions

- **userId from JWT**: Extracted via `SecurityContextHolder.getContext().getAuthentication().getName()` — the existing JWT filter stores userId as the principal name.
- **Soft delete**: DELETE sets `deleted_at = now()` + `is_active = false`. List/get queries filter `WHERE deleted_at IS NULL`.
- **Bank relationship in response**: `BankAccount` has a single `@ManyToOne` to `Bank` (lazy) that maps the `bank_id` FK. The service eagerly loads it (via `@EntityGraph("bank")` on the repository find methods). The service fetches the `Bank` entity from the DB and sets it on the `BankAccount` during create/update.
- **Editable fields on update**: `bank_id`, `account_type`, `account_number`, `nick_name` — only non-null fields in the request are applied.
- **Bank is read-only**: No create/update/delete for banks — they're system-managed reference data.
- **Paginated list**: Service uses Spring `Page`/`Pageable` internally, but the controller/service return a generic `com.expenzo.services.common.PaginatedResponse<T>` with `content` (List<T>), `hasNext` (boolean), and `size` (int) — NOT Spring's full `Page` object (no `totalElements`, `totalPages`, etc. leaked to the client).
- **account_type PostgreSQL enum**: Mapped as a native PostgreSQL enum via `@Enumerated(EnumType.STRING)` + `@JdbcTypeCode(SqlTypes.NAMED_ENUM)` with `columnDefinition = "account_type_enum"`. `NAMED_ENUM` binds values as PostgreSQL's native enum type (not plain varchar) so queries/comparisons against the `account_type_enum` column work correctly.

## Explicitly Excluded

- Bank CRUD (create/update/delete) — banks are system-managed reference data
- Frontend changes — this plan covers backend only
- Debit card / Credit card APIs — separate from this plan
- Unit tests for the new endpoints — can be added in a follow-up
- Encryption of `account_number` — schema comment notes "store encrypted at rest" but encryption is out of scope for this plan
