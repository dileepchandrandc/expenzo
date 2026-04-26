
export interface ExpenseCategoryResponse {
    data: ExpenseCategory[];
    hasNext: boolean
}

export interface ExpenseCategory {
    id: string;
    name: string;
}

export interface ExpenseByCategory {
    categoryId: number;
    categoryName: string;
    amount: number
}

export interface ExpenseOverview {
    income: number;
    expense: number;
    bill: number;
    averagePerDay: number;
}