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

export function getUserApi() {
  return apiClient.get<UserAccount>('/user');
}

export function updateUserApi(payload: UpdateUserRequest) {
  return apiClient.put<UserAccount>('/user', payload);
}

export function deleteUserApi() {
  return apiClient.delete('/user');
}

export function changePasswordApi(payload: ChangePasswordRequest) {
  return apiClient.put('/user/change-password', payload);
}
