import apiClient from '../client';

// ── Types ──────────────────────────────────────────────────────────────────────

export interface CreateUserRequest {
  email: string;
  password: string;
  firstName: string;
  lastName?: string;
  countryCode?: string;
  mobileNumber?: string;
}

export interface UpdateUserRequest {
  firstName?: string;
  lastName?: string;
  countryCode?: string;
  mobileNumber?: string;
}

export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

export interface UserAccount {
  userId: string;
  email: string;
  firstName: string;
  lastName?: string;
  countryCode?: string;
  mobile?: string;
}

// ── API calls ──────────────────────────────────────────────────────────────────

export function createUserApi(payload: CreateUserRequest) {
  return apiClient.post('/user', payload);
}

export function getUserApi(userId: string) {
  return apiClient.get<UserAccount>(`/user/${userId}`);
}

export function updateUserApi(userId: string, payload: UpdateUserRequest) {
  return apiClient.put<UserAccount>(`/user/${userId}`, payload);
}

export function deleteUserApi(userId: string) {
  return apiClient.delete(`/user/${userId}`);
}

export function changePasswordApi(userId: string, payload: ChangePasswordRequest) {
  return apiClient.put(`/user/${userId}/change-password`, payload);
}
