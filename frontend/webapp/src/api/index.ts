
import axios from "axios";
import type { 
  ExpenseBucket, ExpenseByCategory, ExpenseCategory, ExpenseCategoryResponse, 
  MonthlyExpenseOverview, ExpenseResponse, BankingCard, BankAccount, 
  DailySpendingTrend, BudgetSummary, AddBankAccountRequest, AddCreditCardRequest, 
  AddDebitCardRequest, AddTransactionRequest, CreateBudgetRequest, BudgetResponse, Bank 
} from "../models";

const api = axios.create({
  baseURL: "http://localhost:8080/expenzo-services",
  timeout: 10000,
});

api.interceptors.request.use((config) => {
  config.headers['user-id'] = 1;
  return config;
});

export const getExpenseCategories = async (): Promise<ExpenseCategory[]> => {
  const res = await api.get<ExpenseCategoryResponse>('/expense-category/list');
  return res.data.data;
};

export const getMonthlyOverview = async(year: number, month: number) : Promise<MonthlyExpenseOverview> => {
  const res = await api.get<MonthlyExpenseOverview>(`expense/overview/year/${year}/month/${month}`);
  return res.data;
}

export const getExpenseGroupedByCategory = async(year: number, month: number) : Promise<ExpenseByCategory[]> => {
  const res = await api.get<ExpenseByCategory[]>(`/expense/group/category/year/${year}/month/${month}`);
  return res.data;
}

export const getExpenses = async(year: number, month: number, categoryId: number | undefined, page: number, size: number) => {
  const res = await api.get<ExpenseResponse>(`/expense/year/${year}/month/${month}?page=${page}&size=${size}${categoryId != undefined ? `&category=${categoryId}`: ''}`)
  return res.data;
}

export const getExpenseBuckets = async() => {
  const res = await api.get<ExpenseBucket[]>("/expense/bucket");
  return res.data;
}

export const getBankAccounts = async() => {
  const res = await api.get<BankAccount[]>("/bank-account/list");
  return res.data;
}

export const getCreditCards = async() => {
  const res = await api.get<BankingCard[]>("/credit-card/list"); 
  return res.data;
}

export const getDebitCards = async() => {
  const res = await api.get<BankingCard[]>("/debit-card/list");
  return res.data;
}

export const getDailySpendingTrend = async(year: number, month: number) => {
  const res = await api.get<DailySpendingTrend[]>(`expense/spend-trend/daily/year/${year}/month/${month}`);
  return res.data;
}

export const getBudgetSummary = async(year: number, month: number) => {
  const res = await api.get<BudgetSummary>(`budget/summary/year/${year}/month/${month}`);
  return res.data;
}

// ========== Payment Channel CRUD APIs ==========

export const getBanks = async (): Promise<Bank[]> => {
  const res = await api.get<Bank[]>("/bank/list");
  return res.data;
}

export const createBankAccount = async (data: AddBankAccountRequest): Promise<BankAccount> => {
  const res = await api.post<BankAccount>("/bank-account", data);
  return res.data;
}

export const updateBankAccount = async (id: number, data: AddBankAccountRequest): Promise<BankAccount> => {
  const res = await api.put<BankAccount>(`/bank-account/${id}`, data);
  return res.data;
}

export const deleteBankAccount = async (id: number): Promise<void> => {
  await api.delete(`/bank-account/${id}`);
}

export const createCreditCard = async (data: AddCreditCardRequest): Promise<BankingCard> => {
  const res = await api.post<BankingCard>("/credit-card", data);
  return res.data;
}

export const updateCreditCard = async (id: number, data: AddCreditCardRequest): Promise<BankingCard> => {
  const res = await api.put<BankingCard>(`/credit-card/${id}`, data);
  return res.data;
}

export const deleteCreditCard = async (id: number): Promise<void> => {
  await api.delete(`/credit-card/${id}`);
}

export const createDebitCard = async (data: AddDebitCardRequest): Promise<BankingCard> => {
  const res = await api.post<BankingCard>("/debit-card", data);
  return res.data;
}

export const updateDebitCard = async (id: number, data: AddDebitCardRequest): Promise<BankingCard> => {
  const res = await api.put<BankingCard>(`/debit-card/${id}`, data);
  return res.data;
}

export const deleteDebitCard = async (id: number): Promise<void> => {
  await api.delete(`/debit-card/${id}`);
}

// ========== Transaction CRUD APIs ==========

export const addTransaction = async (data: AddTransactionRequest): Promise<void> => {
  await api.post("/transaction", data);
}

export const updateTransaction = async (id: number, data: AddTransactionRequest): Promise<void> => {
  await api.put(`/transaction/${id}`, data);
}

export const deleteTransaction = async (id: number): Promise<void> => {
  await api.delete(`/transaction/${id}`);
}

// ========== Budget CRUD APIs ==========

export const createBudget = async (data: CreateBudgetRequest): Promise<BudgetResponse> => {
  const res = await api.post<BudgetResponse>("/budget", data);
  return res.data;
}

export const getMyBudget = async (): Promise<BudgetResponse | null> => {
  try {
    const res = await api.get<BudgetResponse>("/budget/my");
    return res.data;
  } catch {
    return null;
  }
}

export const updateMyBudget = async (data: CreateBudgetRequest): Promise<BudgetResponse> => {
  const res = await api.put<BudgetResponse>("/budget/my", data);
  return res.data;
}

export const deleteMyBudget = async (): Promise<void> => {
  await api.delete("/budget/my");
}

export const uploadExpenses = async(file: File, excludeDuplicates: boolean, duplicationStrategy: string) => {
  const form: FormData = new FormData();
  form.append('file', file);
  form.append('removeDuplicates', String(excludeDuplicates));
  form.append('duplicateMatchingStrategy', duplicationStrategy);
  const res = await api.post("/expense/upload-file/CREDIT_CARD/2", form);
  console.log(res.data)
  return res.data;
}
