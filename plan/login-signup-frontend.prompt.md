# Plan: Login & Signup Pages (Vue + Axios + Auth Hooks)

## Overview
Add login and signup pages to the Vue 3 + TypeScript frontend with Axios for API calls, token refresh interceptors, and auth composables. Design uses `rgb(79, 70, 229)` (indigo), white, and black.

## Steps

1. **Install dependencies** — `npm install vue-router axios`
2. **Create `src/api/config.ts`** — centralized config with `baseURL: http://localhost:8080/expenzo-services`
3. **Create `src/api/client.ts`** — Axios instance using `config.baseURL`, request interceptor (attach Bearer token), response interceptor (auto-refresh on 401, redirect on failure)
4. **Create `src/api/auth/index.ts`** — auth API functions: `loginApi()`, `refreshTokenApi()` + their types
5. **Create `src/api/user/index.ts`** — user API functions: `createUserApi()`, `getUserApi()`, `updateUserApi()`, `deleteUserApi()`, `changePasswordApi()` + their types
6. **Create `src/composables/useAuth.ts`** — reactive composable calling API modules: `login()`, `signup()`, `logout()`
7. **Create `src/components/AppLogo.vue`** — circular "Ex" badge + "Expenzo" name
8. **Create `src/router/index.ts`** — routes: `/login`, `/signup`
9. **Create `src/views/LoginView.vue`** — email + password form
10. **Create `src/views/SignupView.vue`** — signup form with all fields
11. **Update `src/main.ts`** — import router
12. **Update `src/App.vue`** — `<RouterView />`

## API Layer Architecture
```
src/api/
├── config.ts          ← baseURL, shared constants
├── client.ts          ← axios instance + interceptors only
├── auth/
│   └── index.ts       ← loginApi(), refreshTokenApi() + types
└── user/
    └── index.ts       ← createUserApi(), getUserApi(), etc. + types
```

Components never call apiClient directly — they call composables which invoke the API modules.

## Axios Interceptor Flow
```
Request → attach Bearer token
Response 401 → attempt PUT /auth/refresh with refreshToken
  ├── Success → store new tokens, retry original request
  └── Failure → clear tokens, redirect to /login
```

## Design Tokens
| Token | Value |
|---|---|
| `--primary` | `rgb(79, 70, 229)` |
| `--white` | `#ffffff` |
| `--black` | `#000000` |

## Files
- `src/api/config.ts` — new
- `src/api/client.ts` — new (axios instance + interceptors)
- `src/api/auth/index.ts` — new (auth API functions + types)
- `src/api/user/index.ts` — new (user API functions + types)
- `src/composables/useAuth.ts` — new (auth composable)
- `src/style.css` — new
- `src/components/AppLogo.vue` — new
- `src/router/index.ts` — new
- `src/views/LoginView.vue` — new
- `src/views/SignupView.vue` — new
- `src/main.ts` — edit
- `src/App.vue` — edit

## Verification
1. `npm run build` — compiles without errors
2. `npm run dev` → app loads at localhost:5173, redirects to /login
3. Login form visible with logo, link to signup
4. Signup form visible with all fields, link to login
5. Axios interceptor attaches token on authenticated requests
6. Auth API calls go to `http://localhost:8080/expenzo-services/auth/*`
7. User API calls go to `http://localhost:8080/expenzo-services/user/*`
