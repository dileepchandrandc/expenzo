
import axios from "axios";
import type { ExpenseByCategory, ExpenseCategory, ExpenseCategoryResponse, ExpenseOverview } from "../models";

const api = axios.create({
  baseURL: "http://localhost:8080/core-services",
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

export const getDashboradOverview = async() : Promise<ExpenseOverview> => {
  const res = await api.get<ExpenseOverview>('/dashboard/overview');
  return res.data;
}

export const getExpenseGroupedByCategory = async() : Promise<ExpenseByCategory[]> => {
  const res = await api.get<ExpenseByCategory[]>('/dashboard/group/category');
  return res.data;
}
