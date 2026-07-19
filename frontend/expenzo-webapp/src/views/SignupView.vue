<script setup lang="ts">
import { ref } from 'vue';
import AppLogo from '../components/AppLogo.vue';
import { useAuth } from '../composables/useAuth';

const { signup } = useAuth();

const email = ref('');
const password = ref('');
const firstName = ref('');
const lastName = ref('');
const countryCode = ref('');
const mobileNumber = ref('');
const error = ref('');
const loading = ref(false);

const handleSignup = async () => {
  error.value = '';

  if (!email.value || !password.value || !firstName.value) {
    error.value = 'Email, password, and first name are required';
    return;
  }

  loading.value = true;
  try {
    await signup({
      email: email.value,
      password: password.value,
      firstName: firstName.value,
      lastName: lastName.value || undefined,
      countryCode: countryCode.value || undefined,
      mobileNumber: mobileNumber.value || undefined,
    });

    alert('Account created and signed in successfully!');
  } catch (err: unknown) {
    if (err && typeof err === 'object' && 'response' in err) {
      const axiosErr = err as {
        response?: { data?: { error?: string } | Record<string, string>; status?: number };
      };
      const data = axiosErr.response?.data;
      if (typeof data === 'object' && data !== null && !('error' in data)) {
        // Validation errors: key-value map of field → message
        error.value = Object.entries(data).map(([k, v]) => `${k}: ${v}`).join(', ');
      } else {
        error.value = (data as { error?: string })?.error || 'Signup failed';
      }
    } else {
      error.value = 'Signup failed. Please try again.';
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
      <h2 class="auth-title">Create your account</h2>

      <form @submit.prevent="handleSignup" class="auth-form">
        <div class="form-group">
          <label for="email">Email *</label>
          <input
            id="email"
            v-model="email"
            type="email"
            placeholder="you@example.com"
            required
          />
        </div>

        <div class="form-group">
          <label for="password">Password *</label>
          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="At least 8 characters, upper, lower & digit"
            required
          />
        </div>

        <div class="form-row">
          <div class="form-group">
            <label for="firstName">First Name *</label>
            <input
              id="firstName"
              v-model="firstName"
              type="text"
              placeholder="First Name"
              required
            />
          </div>
          <div class="form-group">
            <label for="lastName">Last Name</label>
            <input
              id="lastName"
              v-model="lastName"
              type="text"
              placeholder="Last Name"
            />
          </div>
        </div>

        <div class="form-row">
          <div class="form-group form-group-small">
            <label for="countryCode">Code</label>
            <input
              id="countryCode"
              v-model="countryCode"
              type="text"
              placeholder="+1"
            />
          </div>
          <div class="form-group form-group-large">
            <label for="mobileNumber">Mobile</label>
            <input
              id="mobileNumber"
              v-model="mobileNumber"
              type="text"
              placeholder="1234567890"
            />
          </div>
        </div>

        <p v-if="error" class="error-message">{{ error }}</p>

        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? 'Creating account...' : 'Sign up' }}
        </button>
      </form>

      <p class="auth-footer">
        Already have an account?
        <router-link to="/login">Sign in</router-link>
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
  max-width: 420px;
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
  gap: 14px;
}

.form-row {
  display: flex;
  gap: 12px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
  min-width: 0;
}

.form-group-small {
  max-width: 80px;
}

.form-group-large {
  flex: 1;
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
