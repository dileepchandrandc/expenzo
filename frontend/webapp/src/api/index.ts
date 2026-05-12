
import axios from "axios";
import type { ExpenseBucket, ExpenseByCategory, ExpenseCategory, ExpenseCategoryResponse, MonthlyExpenseOverview, ExpenseResponse } from "../models";

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
