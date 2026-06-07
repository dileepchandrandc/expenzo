# Expenzo - CRUD Operations Implementation Plan

## Overview

This plan outlines all the missing CRUD operations needed for **Payment Channels**, **Transactions**, and **Budget Settings** across both the backend (Spring Boot / Java) and frontend (Vue 3 / TypeScript).

---

## 1. Payment Channels CRUD

### Backend

#### 1.1 Exception Classes
- **`BankAccountNotFoundException.java`** - RuntimeException
- **`CreditCardNotFoundException.java`** - RuntimeException
- **`DebitCardNotFoundException.java`** - RuntimeException

#### 1.2 Request DTOs
Create these request DTOs for create/update operations:

| File | Fields |
|------|--------|
| `AddBankAccountRequest.java` | `bankId: Integer`, `nickName: String` |
| `AddCreditCardRequest.java` | `bankAccountId: Integer`, `limit: BigDecimal`, `currentOutStanding: BigDecimal`, `billingDate: int` |
| `AddDebitCardRequest.java` | `bankAccountId: Integer` |

#### 1.3 Service Layer Changes

| Service | Methods to Add |
|---------|---------------|
| **BankAccountService** | `add(userId, request)` - Create bank account with ID generation, `update(id, userId, request)`, `delete(id, userId)` |
| **CreditCardService** | `add(userId, request)`, `update(id, userId, request)`, `delete(id, userId)` |
| **DebitCardService** | `add(userId, request)`, `update(id, userId, request)`, `delete(id, userId)` |
| **BankService** | `addBank(name)` - If bank creation is needed, otherwise no change |

#### 1.4 Controller Layer Changes

| Controller | Endpoints to Add |
|------------|-----------------|
| **BankAccountController** | `POST /` - Create bank account, `PUT /{id}` - Update, `DELETE /{id}` - Delete |
| **CreditCardController** | `POST /` - Create credit card, `PUT /{id}` - Update, `DELETE /{id}` - Delete |
| **DebitCardController** | `POST /` - Create debit card, `PUT /{id}` - Update, `DELETE /{id}` - Delete |

### Frontend

#### 1.5 API Layer (`src/api/index.ts`)
Add API functions:
- `createBankAccount(data)` → `POST /bank-account`
- `updateBankAccount(id, data)` → `PUT /bank-account/{id}`
- `deleteBankAccount(id)` → `DELETE /bank-account/{id}`
- `createCreditCard(data)` → `POST /credit-card`
- `updateCreditCard(id, data)` → `PUT /credit-card/{id}`
- `deleteCreditCard(id)` → `DELETE /credit-card/{id}`
- `createDebitCard(data)` → `POST /debit-card`
- `updateDebitCard(id, data)` → `PUT /debit-card/{id}`
- `deleteDebitCard(id)` → `DELETE /debit-card/{id}`
- `getBanks()` → `GET /bank/list` (if not already present)

#### 1.6 Models (`src/models/index.ts`)
Add request/response types:
- `AddBankAccountRequest` interface
- `AddCreditCardRequest` interface
- `AddDebitCardRequest` interface

#### 1.7 UI Components

| Component | Changes |
|-----------|---------|
| **PaymentChannelPage.vue** | Wire up "Add Bank Account" button to open a form modal; wire up "Add Credit/Debit Card" button similarly |
| **BankAccountCard.vue** | Wire edit/delete buttons to actual API calls and emit events to parent |
| **BankingCardComponent.vue** | Wire edit/delete buttons to actual API calls and emit events to parent |
| **AddBankAccountModal.vue** (new) | Form with bank selector (from `GET /bank/list`), nickname field |
| **AddCreditDebitCardModal.vue** (new) | Form with card type selector, bank account selector, limit/outstanding/billing fields |
| **EditBankAccountModal.vue** (new) | Pre-filled edit form |
| **EditCardModal.vue** (new) | Pre-filled edit form |

---

## 2. Transactions CRUD

### Backend (Existing: POST /, PUT /{id})

#### 2.1 Missing Endpoints

| Controller | Endpoints to Add |
|------------|-----------------|
| **TransactionController** | `GET /{id}` - Get single transaction by ID, `DELETE /{id}` - Delete transaction |
| **TransactionController** | `GET /` or `GET /list` - List all transactions for user (optional, may be served by expense endpoints) |

#### 2.2 Service Layer

| Service | Methods to Add |
|---------|---------------|
| **TransactionService** | `getTransaction(userId, id)` - Get single transaction with DTO conversion, `deleteTransaction(userId, id)` - Delete with ownership check |
| **TransactionService** | `toTransactionDto(Transaction)` - Convert entity to `TransactionDto` |

#### 2.3 Exception Classes
- No new exceptions needed (can reuse `TransactionNotFoundException`)

### Frontend

#### 2.4 API Layer (`src/api/index.ts`)
- `addTransaction(data)` → `POST /transaction`
- `updateTransaction(id, data)` → `PUT /transaction/{id}`
- `deleteTransaction(id)` → `DELETE /transaction/{id}`
- `getTransaction(id)` → `GET /transaction/{id}`

#### 2.5 Models (`src/models/index.ts`)
- `AddTransactionRequest` interface

#### 2.6 UI Components

| Component | Changes |
|-----------|---------|
| **AddTransactionModal.vue** | Wire up "Add Transaction" button to call `addTransaction()` API; add payment channel source/destination selectors; handle form validation and state reset |
| **ExpenseModal.vue** | Wire "Edit" button to open edit mode; wire "Delete" button to call `deleteTransaction()` with confirmation |
| **ExpenseCard.vue** | Ensure edit/delete buttons propagate events up to trigger appropriate modals/actions |
| **ExpenseListView.vue** | Handle refresh after delete |

---

## 3. Budget Settings CRUD (Excluding Report APIs)

### Backend

#### 3.1 New Entity Classes
Create JPA entities for the `monthly_budget` and `monthly_budget_expense_category` tables (already referenced in SQL queries):

| Entity | Table | Key Fields |
|--------|-------|-----------|
| **MonthlyBudget** | `monthly_budget` | `id`, `userId`, `name`, `spendLimit: BigDecimal`, `month: int`, `year: int` |
| **MonthlyBudgetCategory** | `monthly_budget_expense_category` | `id`, `budget` (ManyToOne → MonthlyBudget), `category` (ManyToOne → ExpenseCategory), `spendLimit: BigDecimal` |

#### 3.2 DTOs

| DTO | Purpose |
|-----|---------|
| `CreateBudgetRequest.java` | `name`, `spendLimit`, `year`, `month`, `categoryLimits: List<CategoryLimit>` |
| `BudgetResponse.java` | `id`, `name`, `spendLimit`, `year`, `month`, `categories: List<BudgetCategoryResponse>` |
| `CategoryLimit.java` | `categoryId`, `spendLimit` |
| `BudgetCategoryResponse.java` | `categoryId`, `categoryName`, `spendLimit` |

#### 3.3 Repositories

| Repository | Methods |
|-----------|---------|
| **MonthlyBudgetRepository** (JPA) | `findByUserIdAndYearAndMonth()`, `findByUserIdAndId()` |
| **MonthlyBudgetCategoryRepository** (JPA) | `findByBudgetId()` |

#### 3.4 Service Layer

| Service | Methods to Add |
|---------|---------------|
| **BudgetService** | `createBudget(userId, request)` - Create budget with category allocations |
| **BudgetService** | `updateBudget(userId, budgetId, request)` - Update budget details |
| **BudgetService** | `deleteBudget(userId, budgetId)` - Delete budget and its category mappings |
| **BudgetService** | `getBudget(userId, budgetId)` - Get single budget details |

#### 3.5 Controller Endpoints

| Controller | Endpoints to Add |
|------------|-----------------|
| **BudgetController** | `POST /` - Create new budget |
| **BudgetController** | `PUT /{budgetId}` - Update existing budget |
| **BudgetController** | `DELETE /{budgetId}` - Delete budget |

*Note: The budget report/fetch APIs (`GET /summary/year/{year}/month/{month}`) already exist and should NOT be modified.*

### Frontend

#### 3.6 API Layer (`src/api/index.ts`)
- `createBudget(data)` → `POST /budget`
- `updateBudget(id, data)` → `PUT /budget/{id}`
- `deleteBudget(id)` → `DELETE /budget/{id}`

#### 3.7 Models (`src/models/index.ts`)
- `CreateBudgetRequest` interface
- `CategoryLimit` interface
- `BudgetResponse` interface

#### 3.8 UI Components

| Component | Changes |
|-----------|---------|
| **BudgetPage.vue** | Add a "Create Budget" or "Budget Settings" button; show existing budgets list; wire up edit/delete actions |
| **BudgetSettingsModal.vue** (new) | Form to create/edit budget: name, spend limit, month/year selection, category-wise spend limits |

---

## 4. Cross-Cutting Concerns

### 4.1 ID Generation Strategy
- `Bank`, `BankAccount`, `CreditCard`, `DebitCard`, `ExpenseCategory` use plain `@Id` without `@GeneratedValue`
- For new entities (`MonthlyBudget`, `MonthlyBudgetCategory`), use `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- For existing payment channel entities, use `getNextSequence()` or a `@SequenceGenerator` approach if ID auto-generation is needed

### 4.2 Global Exception Handler (Recommended)
- Add `@RestControllerAdvice` class to centralize exception handling
- Return proper HTTP status codes (404 for not found, 400 for validation, etc.)

### 4.3 Frontend Refresh Pattern
- After any create/update/delete operation, refresh the relevant data list
- Show success/error toast notifications

---

## 5. Implementation Order

1. **Backend: Payment Channels CRUD** (entities, DTOs, services, controllers)
2. **Frontend: Payment Channels UI** (modals, API integration, button wiring)
3. **Backend: Transaction DELETE + GET** (service, controller)
4. **Frontend: Transaction UI** (wire up AddTransactionModal, ExpenseModal edit/delete)
5. **Backend: Budget Settings CRUD** (entities, repositories, DTOs, services, controllers)
6. **Frontend: Budget Settings UI** (modal, API integration)

---

## 6. Files to Create (Backend)

| File | Package |
|------|---------|
| `MonthlyBudget.java` | `model/budget/` |
| `MonthlyBudgetCategory.java` | `model/budget/` |
| `MonthlyBudgetRepository.java` | `repository/` |
| `MonthlyBudgetCategoryRepository.java` | `repository/` |
| `AddBankAccountRequest.java` | `dto/payment/` |
| `AddCreditCardRequest.java` | `dto/payment/` |
| `AddDebitCardRequest.java` | `dto/payment/` |
| `CreateBudgetRequest.java` | `dto/budget/` |
| `BudgetResponse.java` | `dto/budget/` |
| `CategoryLimit.java` | `dto/budget/` |
| `BudgetCategoryResponse.java` | `dto/budget/` |
| `BankAccountNotFoundException.java` | `exception/` |
| `CreditCardNotFoundException.java` | `exception/` |
| `DebitCardNotFoundException.java` | `exception/` |

## 7. Files to Create (Frontend)

| File | Location |
|------|----------|
| `AddBankAccountModal.vue` | `src/components/` |
| `AddCreditDebitCardModal.vue` | `src/components/` |
| `BudgetSettingsModal.vue` | `src/components/` |

## 8. Files to Modify

### Backend
- `BankAccountService.java` - Add CRUD methods
- `CreditCardService.java` - Add CRUD methods
- `DebitCardService.java` - Add CRUD methods
- `TransactionService.java` - Add delete + get methods
- `BankAccountController.java` - Add POST/PUT/DELETE endpoints
- `CreditCardController.java` - Add POST/PUT/DELETE endpoints
- `DebitCardController.java` - Add POST/PUT/DELETE endpoints
- `TransactionController.java` - Add DELETE + GET endpoints
- `BudgetService.java` - Add CRUD methods
- `BudgetController.java` - Add POST/PUT/DELETE endpoints

### Frontend
- `src/api/index.ts` - Add all new API functions
- `src/models/index.ts` - Add all new interfaces
- `PaymentChannelPage.vue` - Wire up modals and actions
- `BankAccountCard.vue` - Wire edit/delete
- `BankingCardComponent.vue` - Wire edit/delete
- `AddTransactionModal.vue` - Wire submit button, add payment selectors
- `ExpenseModal.vue` - Wire edit/delete
- `BudgetPage.vue` - Add budget settings button/modal
