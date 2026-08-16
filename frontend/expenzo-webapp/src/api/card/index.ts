import apiClient from '../client';

// ── Types ──────────────────────────────────────────────────────────────────────

export interface CreditCard {
  id: string;
  userId: string;
  bankAccountId?: string;
  cardNumber: string;
  validFrom: string;
  validTo: string;
  creditLimit: number;
  billingDate: number;
  nickName?: string;
  createdAt: string;
  updatedAt: string;
  active: boolean;
}

export interface DebitCard {
  id: string;
  userId: string;
  bankAccountId: string;
  cardNumber: string;
  validFrom: string;
  validTo: string;
  nickName?: string;
  createdAt: string;
  updatedAt: string;
  active: boolean;
}

export interface CreateCreditCardRequest {
  bankAccountId?: string;
  cardNumber: string;
  validTo: string; // YYYY-MM
  creditLimit: number;
  billingDate: number;
  nickName?: string;
}

export interface UpdateCreditCardRequest {
  bankAccountId?: string;
  cardNumber?: string;
  validTo?: string; // YYYY-MM
  creditLimit?: number;
  billingDate?: number;
  nickName?: string;
}

export interface CreateDebitCardRequest {
  bankAccountId: string;
  cardNumber: string;
  validTo: string; // YYYY-MM
  nickName?: string;
}

export interface UpdateDebitCardRequest {
  bankAccountId?: string;
  cardNumber?: string;
  validTo?: string; // YYYY-MM
  nickName?: string;
}

// ── API calls ──────────────────────────────────────────────────────────────────

// Credit cards
export function listCreditCardsApi(page = 0, size = 20) {
  return apiClient.get<PaginatedResponse<CreditCard>>('/credit-card/list', {
    params: { page, size },
  });
}

export function createCreditCardApi(payload: CreateCreditCardRequest) {
  return apiClient.post<CreditCard>('/credit-card', payload);
}

export function updateCreditCardApi(id: string, payload: UpdateCreditCardRequest) {
  return apiClient.put<CreditCard>(`/credit-card/${id}`, payload);
}

export function deleteCreditCardApi(id: string) {
  return apiClient.delete(`/credit-card/${id}`);
}

// Debit cards
export function listDebitCardsApi(page = 0, size = 20) {
  return apiClient.get<PaginatedResponse<DebitCard>>('/debit-card/list', {
    params: { page, size },
  });
}

export function createDebitCardApi(payload: CreateDebitCardRequest) {
  return apiClient.post<DebitCard>('/debit-card', payload);
}

export function updateDebitCardApi(id: string, payload: UpdateDebitCardRequest) {
  return apiClient.put<DebitCard>(`/debit-card/${id}`, payload);
}

export function deleteDebitCardApi(id: string) {
  return apiClient.delete(`/debit-card/${id}`);
}

export interface PaginatedResponse<T> {
  content: T[];
  hasNext: boolean;
  size: number;
}
