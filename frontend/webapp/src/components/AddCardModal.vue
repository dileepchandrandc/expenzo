<script setup lang="ts">
import { X } from 'lucide-vue-next';
import { ref, onMounted } from 'vue';
import { getBankAccounts, createCreditCard, createDebitCard } from '../api';
import type { BankAccount } from '../models';

const emit = defineEmits<{
  close: []
  saved: []
}>();

const cardType = ref<'credit' | 'debit'>('credit');
const bankAccounts = ref<BankAccount[]>([]);
const selectedBankAccountId = ref<number | null>(null);
const creditLimit = ref<number>(0);
const outstanding = ref<number>(0);
const billingDate = ref<number>(1);

onMounted(async () => {
  try {
    bankAccounts.value = await getBankAccounts();
  } catch (err) {
    console.error("Failed to load bank accounts:", err);
  }
});

const closeModal = () => {
  emit('close');
}

const save = async () => {
  if (selectedBankAccountId.value == null) return;
  try {
    if (cardType.value === 'credit') {
      await createCreditCard({
        bankAccountId: selectedBankAccountId.value,
        limit: creditLimit.value,
        currentOutStanding: outstanding.value,
        billingDate: billingDate.value
      });
    } else {
      await createDebitCard({
        bankAccountId: selectedBankAccountId.value
      });
    }
    emit('saved');
    emit('close');
  } catch (err) {
    console.error("Failed to create card:", err);
  }
}
</script>

<template>
  <div class="modal-background">
    <div class="modal-content">
      <div class="d-flex justify-content-between align-items-center">
        <div class="modal-title">Add Credit / Debit Card</div>
        <button class="close-button" @click="closeModal"><X /></button>
      </div>
      <hr/>
      <div class="d-flex flex-column gap-3">
        <div class="d-flex gap-3">
          <label class="card-type-selection">
            <input type="radio" value="credit" v-model="cardType" /> Credit Card
          </label>
          <label class="card-type-selection">
            <input type="radio" value="debit" v-model="cardType" /> Debit Card
          </label>
        </div>
        <select class="form-select" v-model="selectedBankAccountId">
          <option :value="null" disabled>Select Bank Account</option>
          <option v-for="ba in bankAccounts" :key="ba.id" :value="ba.id">
            {{ ba.bank.name }} - {{ ba.nickName || 'Account' }}
          </option>
        </select>
        <template v-if="cardType === 'credit'">
          <input type="number" class="form-control" placeholder="Credit Limit" v-model="creditLimit" />
          <input type="number" class="form-control" placeholder="Current Outstanding" v-model="outstanding" />
          <div class="d-flex align-items-center gap-2">
            <label>Billing Date:</label>
            <input type="number" class="form-control" min="1" max="31" v-model="billingDate" />
          </div>
        </template>
        <button class="btn btn-primary" @click="save" :disabled="selectedBankAccountId == null">Save</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.close-button {
  background: none;
  border: none;
  cursor: pointer;
}
.modal-content {
  background-color: white;
  padding: 1rem;
  border-radius: 10px;
  width: 450px;
}
.modal-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.modal-title {
  font-size: 1.25rem;
  font-weight: 600;
}
.card-type-selection {
  background-color: rgba(210, 210, 210, 0.5);
  padding: 0.25rem 0.75rem;
  border-radius: 7px;
  cursor: pointer;
}
</style>
