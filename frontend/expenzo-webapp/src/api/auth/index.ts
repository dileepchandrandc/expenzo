import apiClient from '../client';

// ── Types ──────────────────────────────────────────────────────────────────────

export interface UserLoginRequest {
  email: string;
  password: string;
}

export interface UserToken {
  accessToken: string;
  refreshToken: string;
}

// ── API calls ──────────────────────────────────────────────────────────────────

export function loginApi(payload: UserLoginRequest) {
  return apiClient.post<UserToken>('/auth/login', payload);
}

export function refreshTokenApi(refreshToken: string) {
  return apiClient.put<{ accessToken: string }>('/auth/refresh', { refreshToken });
}
