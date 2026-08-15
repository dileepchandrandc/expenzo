<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { X } from '@lucide/vue';
import {
  listBanksApi,
  updateBankAccountApi,
  type Bank,
  type BankAccount,
  type BankAccountType,
} from '../api/bank';

const props = defineProps<{
  account: BankAccount;
}>();

const emit = defineEmits<{
  close: [];
  saved: [];
}>();

const banks = ref<Bank[]>([]);
const loadingBanks = ref(false);
const submitting = ref(false);
const error = ref('');

const bankId = ref(props.account.bank.id);
const accountType = ref<BankAccountType>(props.account.accountType);
const accountNumber = ref(props.account.accountNumber);
const nickName = ref(props.account.nickName || '');

onMounted(async () => {
  loadingBanks.value = true;
  try {
    const response = await listBanksApi();
    banks.value = response.data;
  } catch {
    error.value = 'Failed to load the bank list.';
  } finally {
    loadingBanks.value = false;
  }
});

const handleSubmit = async () => {
  error.value = '';

  if (!bankId.value) {
    error.value = 'Please select a bank';
    return;
  }
  if (!accountNumber.value.trim()) {
    error.value = 'Account number is required';
    return;
  }

  submitting.value = true;
  try {
    await updateBankAccountApi(props.account.id, {
      bankId: bankId.value,
      accountType: accountType.value,
      accountNumber: accountNumber.value.trim(),
      nickName: nickName.value.trim() || undefined,
    });
    emit('saved');
  } catch (err: unknown) {
    if (err && typeof err === 'object' && 'response' in err) {
      const axiosErr = err as { response?: { data?: { error?: string } | Record<string, string> } };
      const data = axiosErr.response?.data;
      if (typeof data === 'object' && data !== null && !('error' in data)) {
        error.value = Object.entries(data).map(([k, v]) => `${k}: ${v}`).join(', ');
      } else {
        error.value = (data as { error?: string })?.error || 'Failed to update bank account';
      }
    } else {
      error.value = 'Failed to update bank account. Please try again.';
    }
  } finally {
    submitting.value = false;
  }
};
</script>

<template>
  <div class="modal-overlay" @click.self="emit('close')">
    <div class="modal-card">
      <div class="modal-header">
        <div class="modal-title">Edit Bank Account</div>
        <button class="modal-close" @click="emit('close')"><X :size="18" /></button>
      </div>

      <p v-if="error" class="modal-error">{{ error }}</p>

      <form @submit.prevent="handleSubmit" class="modal-form">
        <div class="form-group">
          <label for="bank">Bank</label>
          <select id="bank" v-model="bankId" :disabled="loadingBanks">
            <option v-if="loadingBanks" value="">Loading banks...</option>
            <option v-for="bank in banks" :key="bank.id" :value="bank.id">
              {{ bank.name }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="accountType">Account Type</label>
          <select id="accountType" v-model="accountType">
            <option value="SAVINGS">Savings</option>
            <option value="CURRENT">Current</option>
          </select>
        </div>

        <div class="form-group">
          <label for="accountNumber">Account Number</label>
          <input
            id="accountNumber"
            v-model="accountNumber"
            type="text"
            placeholder="Enter account number"
          />
        </div>

        <div class="form-group">
          <label for="nickName">Nickname (optional)</label>
          <input
            id="nickName"
            v-model="nickName"
            type="text"
            placeholder="e.g. Salary Account"
          />
        </div>

        <div class="modal-actions">
          <button type="button" class="btn-cancel" @click="emit('close')">Cancel</button>
          <button type="submit" class="btn-submit" :disabled="submitting">
            {{ submitting ? 'Saving...' : 'Save Changes' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 16px;
}

.modal-card {
  background: #ffffff;
  border-radius: 12px;
  padding: 24px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.modal-title {
  font-size: 18px;
  font-weight: 700;
  color: #000000;
}

.modal-close {
  background: transparent;
  border: 0;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
}

.modal-error {
  color: #ef4444;
  font-size: 13px;
  margin-bottom: 12px;
}

.modal-form {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #000000;
}

.form-group select,
.form-group input {
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 15px;
  color: #000000;
  background: #ffffff;
}

.form-group select:focus,
.form-group input:focus {
  outline: none;
  border-color: rgb(79, 70, 229);
  box-shadow: 0 0 0 2px rgba(79, 70, 229, 0.15);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 8px;
}

.btn-cancel {
  padding: 10px 16px;
  background: transparent;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  color: #374151;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.btn-submit {
  padding: 10px 16px;
  background-color: rgb(79, 70, 229);
  border: 0;
  border-radius: 8px;
  color: #ffffff;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
