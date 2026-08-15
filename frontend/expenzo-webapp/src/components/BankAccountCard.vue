<script setup lang="ts">
import { Landmark, Pencil, Trash2 } from '@lucide/vue';
import type { BankAccount } from '../api/bank';

const props = defineProps<{
  account: BankAccount;
}>();

defineEmits<{
  edit: [account: BankAccount];
  delete: [account: BankAccount];
}>();

const maskAccountNumber = (accountNumber: string) => {
  if (!accountNumber) return '';
  if (accountNumber.length <= 4) return accountNumber;
  return '*'.repeat(accountNumber.length - 4) + accountNumber.slice(-4);
};
</script>

<template>
  <div class="payment-channel-item">
    <div class="payment-channel-left">
      <Landmark :size="20" class="payment-channel-icon" />
      <div class="payment-channel-info">
        <div class="payment-channel-name-row">
          <div class="payment-channel-name">
            {{ account.nickName || account.bank.shortName }}
          </div>
          <span class="payment-channel-type">{{ account.accountType }}</span>
        </div>
        <div class="payment-channel-sub">
          {{ account.bank.name }} &middot; {{ maskAccountNumber(account.accountNumber) }}
        </div>
      </div>
    </div>
    <div class="payment-channel-actions">
      <button class="payment-channel-action" title="Edit" @click="$emit('edit', account)">
        <Pencil :size="15" />
      </button>
      <button class="payment-channel-action payment-channel-action-delete" title="Delete" @click="$emit('delete', account)">
        <Trash2 :size="15" />
      </button>
    </div>
  </div>
</template>

<style scoped>
.payment-channel-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    padding: 12px 14px;
}
.payment-channel-left {
    display: flex;
    align-items: center;
    gap: 12px;
    min-width: 0;
}
.payment-channel-icon {
    color: rgb(79, 70, 229);
    flex-shrink: 0;
}
.payment-channel-info {
    display: flex;
    flex-direction: column;
    gap: 2px;
    min-width: 0;
}
.payment-channel-name-row {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: wrap;
}
.payment-channel-name {
    color: black;
    font-size: small;
    font-weight: 600;
}
.payment-channel-type {
    background-color: rgb(79, 70, 229);
    color: #ffffff;
    font-size: 10px;
    font-weight: 600;
    border-radius: 6px;
    padding: 2px 8px;
    text-transform: uppercase;
    letter-spacing: 0.3px;
}
.payment-channel-sub {
    color: grey;
    font-size: smaller;
    word-break: break-word;
}
.payment-channel-actions {
    display: flex;
    align-items: center;
    gap: 6px;
    flex-shrink: 0;
}
.payment-channel-action {
    background: transparent;
    border: 0;
    color: #6b7280;
    cursor: pointer;
    padding: 6px;
    border-radius: 6px;
    display: flex;
    align-items: center;
}
.payment-channel-action:hover {
    background-color: #f3f4f6;
}
.payment-channel-action-delete:hover {
    color: #ef4444;
}
</style>
