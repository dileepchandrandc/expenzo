<script setup lang="ts">
import { onMounted, ref, computed } from 'vue';
import { X } from '@lucide/vue';
import {
  listBanksApi,
  listBankAccountsApi,
  createBankAccountApi,
  updateBankAccountApi,
  type Bank,
  type BankAccount,
  type BankAccountType,
} from '../api/bank';
import {
  createCreditCardApi,
  updateCreditCardApi,
  createDebitCardApi,
  updateDebitCardApi,
  type CreditCard,
  type DebitCard,
} from '../api/card';

// ── Props / Emits ─────────────────────────────────────────────────────────────

const props = defineProps<{
  mode: 'create' | 'edit';
  account?: BankAccount;
  creditCard?: CreditCard;
  debitCard?: DebitCard;
}>();

const emit = defineEmits<{
  close: [];
  saved: [];
}>();

type ChannelType = 'BANK_ACCOUNT' | 'DEBIT_CARD' | 'CREDIT_CARD';

// ── Shared state ──────────────────────────────────────────────────────────────

const submitting = ref(false);
const error = ref('');

const banks = ref<Bank[]>([]);
const bankAccounts = ref<BankAccount[]>([]);
const loadingBanks = ref(false);
const loadingAccounts = ref(false);

// ── Type selector ─────────────────────────────────────────────────────────────

const channelTypes: ChannelType[] = ['BANK_ACCOUNT', 'DEBIT_CARD', 'CREDIT_CARD'];

const channelTypeLabels: Record<ChannelType, string> = {
  BANK_ACCOUNT: 'Bank Account',
  DEBIT_CARD: 'Debit Card',
  CREDIT_CARD: 'Credit Card',
};

const activeType = ref<ChannelType>(props.account ? 'BANK_ACCOUNT'
  : props.creditCard ? 'CREDIT_CARD'
  : props.debitCard ? 'DEBIT_CARD'
  : 'BANK_ACCOUNT');

const isEdit = computed(() => props.mode === 'edit');

const sliderIndex = computed(() => channelTypes.indexOf(activeType.value));

const sliderPosition = computed(() => `translateX(${sliderIndex.value * 100}%)`);

// Field helper for the dropdown label "{bank-account-name} - {bank name}"
const getBankAccountLabel = (account: BankAccount) =>
  `${account.nickName || account.bank.shortName} - ${account.bank.name}`;

// ── Bank Account fields ───────────────────────────────────────────────────────

const bankId = ref(props.account?.bank.id ?? '');
const accountType = ref<BankAccountType>(props.account?.accountType ?? 'SAVINGS');
const accountNumber = ref(props.account?.accountNumber ?? '');
const accountNickName = ref(props.account?.nickName ?? '');

// ── Card fields (shared) ──────────────────────────────────────────────────────

const cardBankAccountId = ref(props.debitCard?.bankAccountId
  ?? props.creditCard?.bankAccountId
  ?? '');
const cardNumber = ref(props.creditCard?.cardNumber
  ?? props.debitCard?.cardNumber
  ?? '');
const expiry = ref(formatExpiryForInput(props.creditCard?.validTo ?? props.debitCard?.validTo ?? ''));
const cardNickName = ref(props.creditCard?.nickName
  ?? props.debitCard?.nickName
  ?? '');

// Credit-card-only fields
const creditLimit = ref(props.creditCard?.creditLimit?.toString() ?? '');
const billingDate = ref(props.creditCard?.billingDate?.toString() ?? '');

function formatExpiryForInput(isoDate: string): string {
  if (!isoDate) return '';
  return isoDate.slice(0, 7); // YYYY-MM
}

// Load reference data once. Bank account dropdown is only needed for card types,
// but we load it lazily when the user switches to a card type to avoid unnecessary requests.
onMounted(async () => {
  loadingBanks.value = true;
  try {
    const response = await listBanksApi();
    banks.value = response.data;
    if (banks.value.length > 0) {
      bankId.value = bankId.value || banks.value[0].id;
    }
  } catch {
    error.value = 'Failed to load the bank list.';
  } finally {
    loadingBanks.value = false;
  }

  if (activeType.value !== 'BANK_ACCOUNT') {
    await loadBankAccounts();
  }
});

const loadBankAccounts = async () => {
  loadingAccounts.value = true;
  try {
    const response = await listBankAccountsApi();
    bankAccounts.value = response.data.content;
  } catch {
    bankAccounts.value = [];
  } finally {
    loadingAccounts.value = false;
  }
};

const onTypeSwitch = async (type: ChannelType) => {
  if (isEdit.value) return; // type is locked when editing
  if (activeType.value !== type) {
    activeType.value = type;
    error.value = '';
  }
  if (type !== 'BANK_ACCOUNT' && bankAccounts.value.length === 0) {
    await loadBankAccounts();
  }
};

// ── Validation & Submit ───────────────────────────────────────────────────────

const validate = (): string => {
  if (activeType.value === 'BANK_ACCOUNT') {
    if (!bankId.value) return 'Please select a bank';
    if (!accountNumber.value.trim()) return 'Account number is required';
  } else if (activeType.value === 'DEBIT_CARD') {
    if (!cardBankAccountId.value) return 'Please select a bank account';
    if (!cardNumber.value.trim()) return 'Card number is required';
    if (!expiry.value.trim()) return 'Expiry is required';
  } else {
    if (!cardNumber.value.trim()) return 'Card number is required';
    if (!expiry.value.trim()) return 'Expiry is required';
    if (!creditLimit.value || Number(creditLimit.value) <= 0) return 'Credit limit must be greater than 0';
    const billing = Number(billingDate.value);
    if (!billingDate.value || billing < 1 || billing > 28) return 'Billing date must be between 1 and 28';
  }
  return '';
};

const handleSubmit = async () => {
  error.value = '';

  const validationError = validate();
  if (validationError) {
    error.value = validationError;
    return;
  }

  submitting.value = true;
  try {
    if (activeType.value === 'BANK_ACCOUNT') {
      const payload = {
        bankId: bankId.value,
        accountType: accountType.value,
        accountNumber: accountNumber.value.trim(),
        nickName: accountNickName.value.trim() || undefined,
      };
      if (isEdit.value && props.account) {
        await updateBankAccountApi(props.account.id, payload);
      } else {
        await createBankAccountApi(payload);
      }
    } else if (activeType.value === 'DEBIT_CARD') {
      const cardPayload = {
        bankAccountId: cardBankAccountId.value,
        cardNumber: cardNumber.value.trim(),
        validTo: expiry.value,
        nickName: cardNickName.value.trim() || undefined,
      };
      if (isEdit.value && props.debitCard) {
        await updateDebitCardApi(props.debitCard.id, cardPayload);
      } else {
        await createDebitCardApi(cardPayload);
      }
    } else {
      const cardPayload = {
        bankAccountId: cardBankAccountId.value.trim() || undefined,
        cardNumber: cardNumber.value.trim(),
        validTo: expiry.value,
        creditLimit: Number(creditLimit.value),
        billingDate: Number(billingDate.value),
        nickName: cardNickName.value.trim() || undefined,
      };
      if (isEdit.value && props.creditCard) {
        await updateCreditCardApi(props.creditCard.id, cardPayload);
      } else {
        await createCreditCardApi(cardPayload);
      }
    }
    emit('saved');
  } catch (err: unknown) {
    error.value = parseError(err, 'Failed to save payment channel');
  } finally {
    submitting.value = false;
  }
};

function parseError(err: unknown, fallback: string): string {
  if (err && typeof err === 'object' && 'response' in err) {
    const axiosErr = err as { response?: { data?: { error?: string } | Record<string, string> } };
    const data = axiosErr.response?.data;
    if (typeof data === 'object' && data !== null && !('error' in data)) {
      return Object.entries(data).map(([k, v]) => `${k}: ${v}`).join(', ');
    }
    return (data as { error?: string })?.error || fallback;
  }
  return `${fallback}. Please try again.`;
}

const modalTitle = computed(() => {
  if (isEdit.value) {
    return `Edit ${channelTypeLabels[activeType.value]}`;
  }
  return 'Add Payment Channel';
});

const submitLabel = computed(() => {
  if (submitting.value) return 'Saving...';
  return isEdit.value ? 'Save Changes' : 'Add';
});
</script>

<template>
  <div class="modal-overlay" @click.self="emit('close')">
    <div class="modal-card">
      <div class="modal-header">
        <div class="modal-title">{{ modalTitle }}</div>
        <button class="modal-close" @click="emit('close')"><X :size="18" /></button>
      </div>

      <!-- Sliding type selector (hidden in edit mode) -->
      <div v-if="!isEdit" class="type-selector" role="tablist">
        <div class="type-selector-thumb" :style="{ transform: sliderPosition }"></div>
        <button
          v-for="type in channelTypes"
          :key="type"
          class="type-selector-option"
          :class="{ active: activeType === type }"
          type="button"
          @click="onTypeSwitch(type)"
        >
          {{ channelTypeLabels[type] }}
        </button>
      </div>

      <div v-else class="type-locked">
        {{ channelTypeLabels[activeType] }}
      </div>

      <p v-if="error" class="modal-error">{{ error }}</p>

      <form @submit.prevent="handleSubmit" class="modal-form">
        <!-- BANK ACCOUNT -->
        <template v-if="activeType === 'BANK_ACCOUNT'">
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
            <label for="accountNickName">Nickname (optional)</label>
            <input
              id="accountNickName"
              v-model="accountNickName"
              type="text"
              placeholder="e.g. Salary Account"
            />
          </div>
        </template>

        <!-- DEBIT CARD -->
        <template v-else-if="activeType === 'DEBIT_CARD'">
          <div class="form-group">
            <label for="bankAccountId">Bank Account</label>
            <select id="bankAccountId" v-model="cardBankAccountId" :disabled="loadingAccounts">
              <option v-if="loadingAccounts" value="">Loading accounts...</option>
              <option v-for="account in bankAccounts" :key="account.id" :value="account.id">
                {{ getBankAccountLabel(account) }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="cardNumber">Card Number</label>
            <input
              id="cardNumber"
              v-model="cardNumber"
              type="text"
              placeholder="Enter card number"
            />
          </div>

          <div class="form-group">
            <label for="expiry">Expiry</label>
            <input id="expiry" v-model="expiry" type="month" :max="'2099-12'" />
          </div>

          <div class="form-group">
            <label for="cardNickName">Nickname (optional)</label>
            <input
              id="cardNickName"
              v-model="cardNickName"
              type="text"
              placeholder="e.g. My Debit Card"
            />
          </div>
        </template>

        <!-- CREDIT CARD -->
        <template v-else>
          <div class="form-group">
            <label for="bankAccountId">Bank Account (optional)</label>
            <select id="bankAccountId" v-model="cardBankAccountId" :disabled="loadingAccounts">
              <option value="">None</option>
              <option v-if="loadingAccounts" value="">Loading accounts...</option>
              <option v-for="account in bankAccounts" :key="account.id" :value="account.id">
                {{ getBankAccountLabel(account) }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="cardNumber">Card Number</label>
            <input
              id="cardNumber"
              v-model="cardNumber"
              type="text"
              placeholder="Enter card number"
            />
          </div>

          <div class="form-group">
            <label for="expiry">Expiry</label>
            <input id="expiry" v-model="expiry" type="month" :max="'2099-12'" />
          </div>

          <div class="form-group">
            <label for="creditLimit">Credit Limit (₹)</label>
            <input id="creditLimit" v-model="creditLimit" type="number" min="0.01" step="0.01" placeholder="e.g. 50000" />
          </div>

          <div class="form-group">
            <label for="billingDate">Billing Day (1-28)</label>
            <input id="billingDate" v-model="billingDate" type="number" min="1" max="28" placeholder="e.g. 5" />
          </div>

          <div class="form-group">
            <label for="cardNickName">Nickname (optional)</label>
            <input
              id="cardNickName"
              v-model="cardNickName"
              type="text"
              placeholder="e.g. My Credit Card"
            />
          </div>
        </template>

        <div class="modal-actions">
          <button type="button" class="btn-cancel" @click="emit('close')">Cancel</button>
          <button type="submit" class="btn-submit" :disabled="submitting">
            {{ submitLabel }}
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

/* Sliding type selector */
.type-selector {
  position: relative;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  background-color: #f3f4f6;
  border-radius: 10px;
  padding: 4px;
  margin-bottom: 16px;
}

.type-selector-thumb {
  position: absolute;
  top: 4px;
  left: 4px;
  width: calc((100% - 8px) / 3);
  height: calc(100% - 8px);
  background-color: rgb(79, 70, 229);
  border-radius: 8px;
  transition: transform 0.25s ease;
  z-index: 0;
}

.type-selector-option {
  position: relative;
  z-index: 1;
  background: transparent;
  border: 0;
  border-radius: 8px;
  padding: 8px 0;
  font-size: 13px;
  font-weight: 600;
  color: #6b7280;
  cursor: pointer;
  transition: color 0.2s ease;
}

.type-selector-option.active {
  color: #ffffff;
}

/* Locked indicator when editing */
.type-locked {
  background-color: #f3f4f6;
  border-radius: 10px;
  padding: 8px;
  text-align: center;
  font-size: 13px;
  font-weight: 600;
  color: rgb(79, 70, 229);
  margin-bottom: 16px;
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
