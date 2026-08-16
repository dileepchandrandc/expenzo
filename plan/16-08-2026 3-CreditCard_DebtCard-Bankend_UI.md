# Plan: Credit Card / Debit Card — Backend & UI

**Date:** 16-08-2026
**Scope:** Add a new Spring Boot module `expenzo-payment-card-service` for CRUD on the `credit_card` and `debit_card` tables with **separate controllers** (`/credit-card`, `/debit-card`), wire it into `expenzo-app`, and extend the Vue frontend with a generic payment-channel modal (sliding type selector) plus **Debit Cards** and **Credit Cards** sections on the Profile page.

## Goal

Let users manage credit and debit cards exactly like they manage bank accounts today:

- Full CRUD (create, read, update, soft delete, list) for both card types.
- Debit card: bank account is **mandatory**.
- Credit card: bank account is **optional**; when the user wants to attach one, show available accounts from `GET /bank-account/list` in a dropdown labeled `{bank-account-name} - {bank name}`.
- List cards below bank accounts on the Profile page in two sections: **Debit Cards** and **Credit Cards**.
- Use the `CreditCard` icon for both card types.
- Show expiry date on both card types; show the credit limit only for credit cards.
- Reuse the bank-account modal, generalized so the user can slide between **Bank Account / Debit Card / Credit Card** when adding, and the type is locked when editing.

## Confirmed Decisions

- **Dates:** keep the schema as-is (`valid_from` and `valid_to` are `NOT NULL`). The form collects only the expiry date (`valid_to`); the backend sets `valid_from = LocalDate.now()` on create.
- **Billing date:** the credit-card form includes a billing-day field (1–28), matching the `billing_date` `NOT NULL` column.
- **Card row display:** show only card number / expiry / limit — do **not** show the linked bank account on the card row.
- **Bank account linkage:** required for debit cards, optional for credit cards. Dropdown label = `{bank-account-name} - {bank name}`, where `bank-account-name = nickName || bank.shortName`.
- **No DB schema changes.** `db-schema/credit_card.sql` and `db-schema/debit_card.sql` remain the source of truth (tables must be applied to the Postgres `expenzo_v2` schema).

---

## Backend

### Phase 1 — Module scaffold (`expenzo-payment-card-service`)

1. `backend/expenzo-services/settings.gradle`: add `include 'expenzo-payment-card-service'`.
2. Create `backend/expenzo-services/expenzo-payment-card-service/build.gradle`:
   - Copy `expenzo-bank-account-service/build.gradle` (java-library, Boot plugin `apply false`, BOM import, starters: data-jpa / web / security / validation, Lombok, H2 test).
   - Add `implementation project(':expenzo-bank-account-service')` to reuse `PaginatedResponse` and `BankAccountRepository` for ownership validation.
3. `backend/expenzo-services/expenzo-app/build.gradle`: add `implementation project(':expenzo-payment-card-service')`.

Component scanning needs no change: `@SpringBootApplication` lives at `com.expenzo.services`, so the new `com.expenzo.services.paymentcard.*` package is picked up automatically.

### Phase 2 — Domain (package `com.expenzo.services.paymentcard`)

#### Entities (`repository/model/`)

Mirror `BankAccount` conventions (UUID String id, soft-delete fields, timestamps set in the service layer).

**`CreditCard`** — `@Table(name = "credit_card")`
- `id` — `String`, `@Id @GeneratedValue(strategy = GenerationType.UUID)`
- `userId` — `@Column(name = "user_id", nullable = false)`
- `bankAccountId` — `@Column(name = "bank_account_id")` (nullable)
- `cardNumber` — `@Column(name = "card_number", nullable = false, unique = true)`
- `validFrom` — `@Column(name = "valid_from", nullable = false)` `LocalDate`
- `validTo` — `@Column(name = "valid_to", nullable = false)` `LocalDate`
- `creditLimit` — `@Column(name = "credit_limit", nullable = false)` `BigDecimal`
- `billingDate` — `@Column(name = "billing_date", nullable = false)` `Integer`
- `nickName` — `@Column(name = "nick_name")`
- `createdAt`, `updatedAt` — `OffsetDateTime`
- `isActive` — `boolean`
- `deletedAt` — `OffsetDateTime`

**`DebitCard`** — `@Table(name = "debit_card")`
- Same as `CreditCard` minus `creditLimit` / `billingDate`.
- `bankAccountId` is `nullable = false`.

Lombok: `@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder`.

#### Repositories (`repository/`)

- `CreditCardRepository extends JpaRepository<CreditCard, String>`
- `DebitCardRepository extends JpaRepository<DebitCard, String>`

Both with:
- `Optional<T> findByIdAndUserIdAndDeletedAtIsNull(String id, String userId)`
- `Page<T> findByUserIdAndDeletedAtIsNull(String userId, Pageable pageable)`

#### Request DTOs (`request/`)

Validation follows the existing Jakarta Bean Validation style.

- `CreateCreditCardRequest`: `bankAccountId` (optional), `cardNumber` `@NotBlank @Size(max = 19)`, `validTo` `@NotBlank @Pattern(regexp = "\\d{4}-\\d{2}")` (YYYY-MM), `creditLimit` `@NotNull @DecimalMin("0.01")`, `billingDate` `@NotNull @Min(1) @Max(28)`, `nickName` `@Size(max = 50)`.
- `UpdateCreditCardRequest`: same fields, all optional.
- `CreateDebitCardRequest`: `bankAccountId` `@NotBlank`, `cardNumber` `@NotBlank @Size(max = 19)`, `validTo` `@NotBlank @Pattern(regexp = "\\d{4}-\\d{2}")`, `nickName` `@Size(max = 50)`.
- `UpdateDebitCardRequest`: same fields, all optional.

#### Response DTOs (`dto/`)

- `CreditCardResponse`: `id`, `userId`, `bankAccountId`, `cardNumber`, `validFrom`, `validTo`, `creditLimit`, `billingDate`, `nickName`, `createdAt`, `updatedAt`, `active`.
- `DebitCardResponse`: same minus `creditLimit` / `billingDate`.

Plain `@Getter @Setter` POJOs.

#### Services (`service/`)

Mirror `BankAccountService` exactly:

**`CreditCardService`** and **`DebitCardService`** each expose:
- `create(request)` — `@Transactional`
- `update(id, request)` — `@Transactional`
- `get(id)` — `@Transactional(readOnly = true)`
- `delete(id)` — `@Transactional` (soft delete: set `deletedAt` + `isActive=false` + `updatedAt`)
- `list(page, size)` — `@Transactional(readOnly = true)`, returns `PaginatedResponse<T>`

Shared behaviors:
- `getCurrentUserId()` from `SecurityContextHolder.getContext().getAuthentication().getName()`.
- Ownership via `findByIdAndUserIdAndDeletedAtIsNull`.
- Bank-account ownership validation: when `bankAccountId` is provided (always for debit, optional for credit), resolve it with `BankAccountRepository.findByIdAndUserIdAndDeletedAtIsNull(...)`, else throw `EntityNotFoundException`.
- On create: `validFrom = LocalDate.now()`; parse `validTo` with `YearMonth.parse(...).atEndOfMonth()`; validate `validTo.isAfter(validFrom)`, else `IllegalArgumentException`.
- Normalize `cardNumber` by removing whitespace.
- Manual `toXxxResponse(...)` mapping (no MapStruct).

#### Controllers (`controller/`)

Separate controllers as requested:

**`CreditCardController`** — `@RequestMapping("/credit-card")`
- `POST /credit-card` → 201, `CreditCardResponse`
- `PUT /credit-card/{id}` → `CreditCardResponse`
- `DELETE /credit-card/{id}` → 204
- `GET /credit-card/{id}` → `CreditCardResponse`
- `GET /credit-card/list?page=0&size=20` → `PaginatedResponse<CreditCardResponse>`

**`DebitCardController`** — `@RequestMapping("/debit-card")`
- Same shape with `DebitCardResponse`.

Controllers use `@Valid @RequestBody`; errors flow through the existing `GlobalExceptionHandler` in `expenzo-app`.

### Phase 3 — Backend tests (mirror `expenzo-bank-account-service`)

1. Test bootstrap `src/test/java/com/expenzo/services/PaymentCardServiceTestApplication.java` (`@SpringBootConfiguration @EnableAutoConfiguration`).
2. `src/test/resources/application.yaml` — H2 in PostgreSQL mode, `ddl-auto: none`, `sql.init.mode: always`.
3. `src/test/resources/schema.sql` — create `expenzo_user`, `bank`, `bank_account`, `credit_card`, `debit_card` with **all** entity columns (including `deleted_at`).
4. Test classes:
   - Repository `@DataJpaTest`: `CreditCardRepositoryTest`, `DebitCardRepositoryTest`.
   - Service (Mockito): `CreditCardServiceTest`, `DebitCardServiceTest`.
   - Controller (`@WebMvcTest` + `@AutoConfigureMockMvc(addFilters=false)` + `@MockBean JwtFilterChain`): `CreditCardControllerTest`, `DebitCardControllerTest`.

---

## Frontend

### Phase 4 — API module

Create `src/api/card/index.ts` following `src/api/bank/index.ts`:

- Types: `CreditCard`, `DebitCard`, `CreateCreditCardRequest`, `UpdateCreditCardRequest`, `CreateDebitCardRequest`, `UpdateDebitCardRequest`.
- Functions:
  - `listCreditCardsApi(page = 0, size = 20)`
  - `createCreditCardApi(payload)`
  - `updateCreditCardApi(id, payload)`
  - `deleteCreditCardApi(id)`
  - `listDebitCardsApi(page = 0, size = 20)`
  - `createDebitCardApi(payload)`
  - `updateDebitCardApi(id, payload)`
  - `deleteDebitCardApi(id)`
- Endpoints: `/credit-card`, `/credit-card/{id}`, `/credit-card/list`, and `/debit-card` equivalents.
- `validTo` is sent as `"YYYY-MM"`; the response returns `validTo` as an ISO date string (last day of month).

### Phase 5 — Generic payment-channel modal

Create `src/components/PaymentChannelModal.vue` to replace the two bank-account modals:

- Props:
  - `mode: 'create' | 'edit'`
  - `account?: BankAccount` (edit — bank account)
  - `debitCard?: DebitCard` (edit — debit card)
  - `creditCard?: CreditCard` (edit — credit card)
- Emits: `close`, `saved`.

**Sliding type selector**
- Three segments: **Bank Account** | **Debit Card** | **Credit Card**.
- Rounded grey track with an indigo thumb that slides under the active segment (`transform: translateX(...)` based on active index).
- In create mode the user switches freely; in edit mode the active type is locked (buttons disabled, type derived from which prop is set).

**Per-type form fields**
- Bank Account: bank dropdown, account type (SAVINGS/CURRENT), account number, nickname.
- Debit Card: bank-account dropdown (required), card number, expiry (`type="month"`), nickname.
- Credit Card: bank-account dropdown (optional, with a "None" option), card number, expiry (`type="month"`), credit limit (number), billing day (number, 1–28), nickname.

**Bank-account dropdown** (card types): loaded from `listBankAccountsApi()`, option label = `${nickName || bank.shortName} - ${bank.name}`.

Submit dispatches to the matching API and reuses the existing error parsing (`err.response.data` field map vs `error` string).

Delete `AddBankAccountModal.vue` and `EditBankAccountModal.vue`.

### Phase 6 — Card rows + Profile sections

**`src/components/PaymentCardRow.vue`**
- Props: `card: CreditCard | DebitCard`, `kind: 'CREDIT' | 'DEBIT'`.
- Emits: `edit`, `delete`.
- Renders: `CreditCard` icon, title `card.nickName || (kind === 'CREDIT' ? 'Credit Card' : 'Debit Card')`, badge `CREDIT` / `DEBIT`, sub-line `•••• {last4} · Expires {MM/YY}`, and for credit ` · Limit ₹{formatted}`.
- Reuse the last-4 masking helper and the `BankAccountCard` CSS classes.

**`src/views/ProfileView.vue`**
- Add state: `debitCards`, `creditCards`, `showAddModal`, and an `editing` object (type + item).
- Load all three lists on mount; add load/delete/saved/updated handlers for both card types.
- The "Add" button opens `PaymentChannelModal` in create mode; edit buttons open it in edit mode with the correct item.
- Render sections in order: **Bank Accounts** (existing) → **Debit Cards** → **Credit Cards**.

---

## UI Look (Cards)

**Card row** (mirrors `BankAccountCard`):
- White row, 1px `#e5e7eb` border, 8px radius, 12/14px padding.
- Left: `CreditCard` icon (indigo, 20px).
- Name row: bold black small title + indigo badge `DEBIT` / `CREDIT`.
- Sub-line (grey, smaller): `•••• 1234 · Expires 08/29`; credit cards append ` · Limit ₹2,00,000`.
- Right: pencil + trash buttons (trash hover red).

**Add/Edit modal:**
- Title: "Add Payment Channel" (create) / "Edit {Type}" (edit).
- Top: rounded segmented control with 3 labels and an indigo sliding thumb; disabled/locked in edit mode.
- Form below changes per selected type. Card-only fields: expiry as a month picker, credit limit (₹ number), billing day (1–28).

---

## Relevant Files

- `backend/expenzo-services/settings.gradle`, `backend/expenzo-services/expenzo-app/build.gradle` — module wiring.
- New `backend/expenzo-services/expenzo-payment-card-service/**` — module (entity / repo / request / dto / service / controller + tests).
- `backend/expenzo-services/expenzo-bank-account-service/src/main/java/com/expenzo/services/bankaccount/service/BankAccountService.java` — template to mirror.
- `frontend/expenzo-webapp/src/api/bank/index.ts` — API module template.
- `frontend/expenzo-webapp/src/components/{AddBankAccountModal,EditBankAccountModal,BankAccountCard}.vue` — templates / being replaced.
- `frontend/expenzo-webapp/src/views/ProfileView.vue` — new sections + modal wiring.

## Verification

1. `./gradlew test` from `backend/expenzo-services` — existing + new card tests pass.
2. Run the app; exercise `/credit-card` and `/debit-card` CRUD via Swagger (`/expenzo-services/swagger-ui`) or curl (JWT required).
3. Confirm `credit_card` and `debit_card` tables exist in Postgres `expenzo_v2` (apply `db-schema/credit_card.sql` + `db-schema/debit_card.sql` if not already applied).
4. `npm run build` (vue-tsc type-check) in `frontend/expenzo-webapp`.
5. Manual:
   - Sign in → Profile → Add → slide between Bank Account / Debit Card / Credit Card.
   - Create each type; verify debit requires a bank account, credit's bank account is optional with `{name} - {bank}` labels.
   - Verify the three sections list correctly; edit locks the type; delete works; card rows show masked number, expiry, and credit limit (credit only).
