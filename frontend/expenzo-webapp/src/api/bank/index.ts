import apiClient from '../client';

// ── Types ──────────────────────────────────────────────────────────────────────

export interface Bank {
  id: string;
  name: string;
  shortName: string;
}

export type BankAccountType = 'SAVINGS' | 'CURRENT';

export interface BankAccount {
  id: string;
  userId: string;
  bank: Bank;
  accountType: BankAccountType;
  accountNumber: string;
  nickName?: string;
  createdAt: string;
  updatedAt: string;
  active: boolean;
}

export interface CreateBankAccountRequest {
  bankId: string;
  accountType: BankAccountType;
  accountNumber: string;
  nickName?: string;
}

export interface UpdateBankAccountRequest {
  bankId?: string;
  accountType?: BankAccountType;
  accountNumber?: string;
  nickName?: string;
}

export interface PaginatedResponse<T> {
  content: T[];
  hasNext: boolean;
  size: number;
}

// ── API calls ──────────────────────────────────────────────────────────────────

export function listBanksApi() {
  return apiClient.get<Bank[]>('/bank/list');
}

export function listBankAccountsApi(page = 0, size = 20) {
  return apiClient.get<PaginatedResponse<BankAccount>>('/bank-account/list', {
    params: { page, size },
  });
}

export function createBankAccountApi(payload: CreateBankAccountRequest) {
  return apiClient.post<BankAccount>('/bank-account', payload);
}

export function updateBankAccountApi(id: string, payload: UpdateBankAccountRequest) {
  return apiClient.put<BankAccount>(`/bank-account/${id}`, payload);
}

export function deleteBankAccountApi(id: string) {
  return apiClient.delete(`/bank-account/${id}`);
}
