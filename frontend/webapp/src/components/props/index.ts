import type { BankAccount, BankingCard, Expense, ExpenseCategory } from "../../models";

export interface OverViewCardProps {
    title: string,
    amount: number,
    bottomText: string,
}

export interface ExpenseCardProps {
    title: string,
    amount: number,
    date: string,
    category?: ExpenseCategory
}

export interface ExpenseListViewProps {
    categoryId?: number,
    year?: number,
    month?: number,
    selectExpense: Function
}

export interface ExpenseModalProps {
    expense: Expense,
    onClose: VoidFunction
}

export interface AddTransactionModalProps {
    onClose: VoidFunction
}

export interface BankAccountCardProps {
    bankAccount: BankAccount;
}

export interface BankingCardComponentProps {
    bankingCard: BankingCard;
}