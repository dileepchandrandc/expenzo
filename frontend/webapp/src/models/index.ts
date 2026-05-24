
export interface YearMonth {
    year: number;
    month: number;
}

export interface ExpenseCategoryResponse {
    data: ExpenseCategory[];
    hasNext: boolean
}

export interface ExpenseResponse {
    data: Expense[];
    hasNext: boolean;
}

export interface ExpenseCategory {
    id: number;
    name: string;
}

export interface ExpenseByCategory {
    category: ExpenseCategory;
    amount: number;
}

export interface MonthlyExpenseOverview {
    income: number;
    expense: number;
    bill: number;
    avgSpentPerDay: number;
}

export interface Expense {
    id: number;
    amount: number;
    spentOn: string;
    title: string;
    description: string;
    category?: ExpenseCategory;
    paymentSource: PaymentChannelDetails;
}

export interface ExpenseBucket {
    year: number;
    month: number;
    name: string;
}

export interface PaymentChannelDetails {
    channelType: string;
    channelId: number;
    channelName: string;
    bankName: string;
    bankAccountName: string;
}

export interface Bank {
    id: number;
    name: string;
}

export interface BankAccount {
    id: number;
    bank: Bank;
    nickName?: string;
    accountType: string;
}

export interface BankingCard {
    id: number;
    bankAccount: BankAccount;
    nickName?: string;
    type: string;
}

export interface DailySpendingTrend {
    day: number;
    totalAmountSpent: number;
    expenses: Expense[];
}

export interface BudgetSummary {
    budgetId: number;
    budgetName: string;
    budgetLimit: number;
    totalSpent: number;
    utilizations: BudgetCategory[];
    untrackedSpent: number;
    uncategorizedSpent: number;
    budgetUsage: number;
    untrackedUsage: number;
    uncategorizedUsage: number;
}

export interface BudgetCategory {
    categoryId: number;
    categoryName: string;
    spendLimit: number;
    totalSpent: number;
    budgetUsage: number;
    partOfBudget: boolean;
}
