<script setup lang="ts">
import { ref } from 'vue';
import AppLogo from '../components/AppLogo.vue';
import { useAuth } from '../composables/useAuth';

const { login } = useAuth();

const email = ref('');
const password = ref('');
const error = ref('');
const loading = ref(false);

const handleLogin = async () => {
  error.value = '';

  if (!email.value || !password.value) {
    error.value = 'Email and password are required';
    return;
  }

  loading.value = true;
  try {
    const result = await login(email.value, password.value);
    // Store userId from JWT — we can extract it from the token
    const tokenPayload = JSON.parse(atob(result.accessToken.split('.')[1]));
    localStorage.setItem('userId', tokenPayload.sub);

    // For now show success — no dashboard yet
    alert('Login successful!');
  } catch (err: unknown) {
    if (err && typeof err === 'object' && 'response' in err) {
      const axiosErr = err as { response?: { data?: { error?: string } } };
      error.value = axiosErr.response?.data?.error || 'Login failed';
    } else {
      error.value = 'Login failed. Please try again.';
    }
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="auth-page">
    <div class="auth-card">
      <AppLogo />
      <h2 class="auth-title">Sign in to your account</h2>

      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label for="email">Email</label>
          <input
            id="email"
            v-model="email"
            type="email"
            placeholder="you@example.com"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">Password</label>
          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="Enter your password"
            required
          />
        </div>

        <p v-if="error" class="error-message">{{ error }}</p>

        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? 'Signing in...' : 'Sign in' }}
        </button>
      </form>

      <p class="auth-footer">
        Don't have an account?
        <router-link to="/signup">Sign up</router-link>
      </p>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  width: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}

.auth-card {
  background: var(--white);
  border: 1px solid var(--gray-200);
  border-radius: 12px;
  padding: 40px 32px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
}

.auth-title {
  font-size: 20px;
  font-weight: 600;
  text-align: center;
  margin-top: 20px;
  margin-bottom: 24px;
  color: var(--black);
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: var(--black);
}

.form-group input {
  padding: 10px 12px;
  border: 1px solid var(--gray-300);
  border-radius: 8px;
  font-size: 15px;
  outline: none;
  transition: border-color 0.2s;
  color: var(--black);
  background: var(--white);
}

.form-group input:focus {
  border-color: rgb(79, 70, 229);
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.15);
}

.form-group input::placeholder {
  color: var(--gray-500);
}

.error-message {
  color: var(--red-500);
  font-size: 14px;
  text-align: center;
}

.btn-primary {
  padding: 12px;
  background-color: rgb(79, 70, 229);
  color: var(--white);
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-primary:hover:not(:disabled) {
  background-color: rgb(67, 56, 202);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.auth-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: var(--gray-500);
}

.auth-footer a {
  color: rgb(79, 70, 229);
  font-weight: 500;
  text-decoration: none;
}

.auth-footer a:hover {
  text-decoration: underline;
}
</style>
