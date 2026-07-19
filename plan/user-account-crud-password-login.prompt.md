# Plan: User Account CRUD, Password, and Login

Complete the user account lifecycle (create, read, update, delete), add password hashing with validation, implement change-password, and wire login to real credential verification — replacing all in-memory stubs with JPA-backed logic.

**Current gaps**: `UserAccountService` uses a HashMap (not DB), `ExpenzoUserDetailsService` returns hardcoded credentials, login skips password verification, and the entity lacks a password field and auto-generated UUID.

---

## Phase 1: Entity & Repository Layer

### Step 1: Update `ExpenzoUser` entity (`repository/model/ExpenzoUser.java`)
- Add `password` field (String), annotated with `@Column(nullable = false)`
- Add `@GeneratedValue(strategy = GenerationType.UUID)` on `id` for auto UUID generation
- Add `@Column(unique = true, nullable = false)` on `email`
- Add `@Column(nullable = false)` on `firstName`
- Add `@CreationTimestamp` / `@UpdateTimestamp` on createdAt/updatedAt (optional but recommended)

### Step 2: Add query methods to `ExpenzoUserRepository`
- `Optional<ExpenzoUser> findByEmail(String email)` — for login lookup and uniqueness check
- `boolean existsByEmail(String email)` — for duplicate email validation

---

## Phase 2: Password Infrastructure

### Step 3: Expose `PasswordEncoder` bean in `SecurityConfig`
- Add `@Bean PasswordEncoder passwordEncoder()` returning `BCryptPasswordEncoder`
- This is the standard approach, not reversible "basic encryption" — BCrypt is a one-way hash, which is the industry standard for passwords

### Step 4: Add password validation utility or method
- Regex: `^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$` (1 lower, 1 upper, 1 digit, min 8 chars)
- Can be a static method in `UserAccountService` or a separate `PasswordValidator` class

---

## Phase 3: Request/Response DTOs

### Step 5: Introduce new request DTOs in `auth/request/`
- `CreateUserRequest` — email, password, firstName, lastName, countryCode, mobileNumber (email + firstName + password mandatory, validated via `@NotBlank`/`@Valid`)
- `UpdateUserRequest` — firstName, lastName, countryCode, mobileNumber (no id, no email, no password — only updatable fields)
- `ChangePasswordRequest` — oldPassword, newPassword (both mandatory)

### Step 6: Update `UserAccount` DTO to include all read fields (userId, email, firstName, lastName, countryCode, mobile) — used for responses

---

## Phase 4: Service Layer Implementation

### Step 7: Rewrite `UserAccountService`
- Inject `ExpenzoUserRepository` and `PasswordEncoder`
- `create(CreateUserRequest)` — validate password, check email uniqueness, encode password, map to entity, save, return `UserAccount` DTO
- `update(userId, UpdateUserRequest)` — find by id or throw, update only allowed fields, save, return DTO
- `get(userId)` — find by id or throw `EntityNotFoundException`, return DTO
- `delete(userId)` — delete by id
- `changePassword(userId, ChangePasswordRequest)` — find user, verify old password matches, validate new password, encode and save new password

### Step 8: Rewrite `ExpenzoUserDetailsService`
- Inject `ExpenzoUserRepository`
- `loadUserByUsername(email)` — query DB by email, return Spring Security `User` with email, hashed password, and empty authorities

### Step 9: Rewrite `UserAuthService.login()`
- Inject `PasswordEncoder` and `ExpenzoUserRepository` (or delegate to `AuthenticationManager`)
- Look up user by email, verify password with `passwordEncoder.matches()`, throw `BadCredentialsException` on mismatch
- Generate JWT tokens only on successful verification

---

## Phase 5: Controller Layer Updates

### Step 10: Update `UserAccountController`
- Change `POST /user` to accept `@Valid @RequestBody CreateUserRequest`, delegate to updated service
- Change `PUT /user/{userId}` to accept `@Valid @RequestBody UpdateUserRequest`
- Add `PUT /user/{userId}/change-password` endpoint accepting `@Valid @RequestBody ChangePasswordRequest`

### Step 11: Update `SecurityConfig`
- Add `/user` (POST only) to `permitAll()` for public account creation
- Add `PasswordEncoder` bean (from Step 3)
- Optionally add `AuthenticationManager` bean for cleaner login

---

## Phase 6: Validation & Error Handling

### Step 12: Add validation annotations to request DTOs
- `@NotBlank`, `@Email`, `@Size` on fields as appropriate
- Custom password validator annotation (or manual check in service)

### Step 13: Add global exception handler (`@ControllerAdvice`)
- Handle `MethodArgumentNotValidException` → 400 with field errors
- Handle `EntityNotFoundException` → 404
- Handle `BadCredentialsException` → 401
- Handle `DataIntegrityViolationException` (duplicate email) → 409

---

## Relevant Files

- `backend/expenzo-services/src/main/java/com/expenzo/services/auth/repository/model/ExpenzoUser.java` — add password field, UUID generation, column constraints
- `backend/expenzo-services/src/main/java/com/expenzo/services/auth/repository/ExpenzoUserRepository.java` — add findByEmail, existsByEmail
- `backend/expenzo-services/src/main/java/com/expenzo/services/auth/config/SecurityConfig.java` — add PasswordEncoder bean, expose /user POST
- `backend/expenzo-services/src/main/java/com/expenzo/services/auth/service/UserAccountService.java` — full rewrite with JPA
- `backend/expenzo-services/src/main/java/com/expenzo/services/auth/service/UserAuthService.java` — add real credential verification
- `backend/expenzo-services/src/main/java/com/expenzo/services/auth/service/ExpenzoUserDetailsService.java` — load from DB
- `backend/expenzo-services/src/main/java/com/expenzo/services/auth/controller/UserAccountController.java` — update endpoints, add change-password
- `backend/expenzo-services/src/main/java/com/expenzo/services/auth/dto/UserAccount.java` — may need small adjustments
- `backend/expenzo-services/src/main/java/com/expenzo/services/auth/request/` — new files: CreateUserRequest, UpdateUserRequest, ChangePasswordRequest

---

## Verification

1. **Build**: `./gradlew build` — compiles without errors
2. **Unit tests**: Write/run tests for password validation (valid/invalid passwords), service layer (mock repository), and controller layer (MockMvc)
3. **Manual API tests** (via curl or Postman):
   - `POST /expenzo-services/user` with valid payload → 200 + user DTO (no password in response)
   - `POST /expenzo-services/user` with duplicate email → 409
   - `POST /expenzo-services/user` with weak password → 400
   - `GET /expenzo-services/user/{id}` → 200 + user DTO
   - `PUT /expenzo-services/user/{id}` with field updates → 200 + updated DTO
   - `DELETE /expenzo-services/user/{id}` → 204
   - `POST /expenzo-services/auth/login` with valid credentials → 200 + JWT tokens
   - `POST /expenzo-services/auth/login` with wrong password → 401
   - `PUT /expenzo-services/user/{id}/change-password` with correct old password → 200
   - `PUT /expenzo-services/user/{id}/change-password` with wrong old password → 400
4. **DB verification**: Check `expenzo_user` table — password column is hashed (BCrypt, 60-char string starting with `$2a$`)

---

## Decisions

- **BCrypt over reversible encryption**: Passwords must be hashed, not encrypted. BCrypt via Spring Security's `PasswordEncoder` is the standard. The term "basic encryption" is interpreted as BCrypt hashing — the correct approach for password storage.
- **Email as login identifier**: The existing `UserLoginRequest` uses email. The `ExpenzoUserDetailsService.loadUserByUsername` will use email as the username.
- **No email update in account update**: Email is the unique login identifier; changing it has security implications. Excluded from the updatable fields unless explicitly requested.
- **No forgot-password**: Explicitly excluded per the requirements.

## Further Considerations

1. **Self-only access**: Currently, any authenticated user can GET/PUT/DELETE any user by ID. Consider extracting the authenticated user from `SecurityContextHolder` and restricting operations to the caller's own account. Not requested but worth noting for a follow-up.
2. **Password in create vs. separate endpoint**: The plan includes password in the create request for simplicity. An alternative is a separate activation flow — but this is out of scope.
3. **`ExpenzoUser.id` type**: The entity currently uses `String` for id, which works with UUID generation. No migration needed — JPA will auto-generate UUID strings.
