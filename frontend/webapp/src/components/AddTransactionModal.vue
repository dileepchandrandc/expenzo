<script setup lang="ts">
import { X } from 'lucide-vue-next';
import { ref, onMounted, watch } from 'vue';
import { getExpenseCategories, getBankAccounts, getCreditCards, getDebitCards, addTransaction } from '../api';
import type { ExpenseCategory, BankAccount, BankingCard } from '../models';
import type { AddTransactionModalProps } from './props';

const props = defineProps<AddTransactionModalProps>();
const emit = defineEmits<{
  saved: []
}>();

const categories = ref<ExpenseCategory[]>([]);
const bankAccounts = ref<BankAccount[]>([]);
const creditCards = ref<BankingCard[]>([]);
const debitCards = ref<BankingCard[]>([]);
const channelTypeMap: Record<string, string> = {
  'CREDIT_CARD': 'Credit Card',
  'DEBIT_CARD': 'Debit Card',
  'BANK_ACCOUNT': 'Bank Account',
  'WALLET': 'Wallet'
};

const transactionType = ref('expense');
const title = ref('');
const description = ref('');
const selectedCategoryId = ref<number | undefined>(undefined);
const amount = ref<number>(0);
const date = ref(new Date().toISOString().split('T')[0]);

// Payment channel - source (for expense) or destination (for income)
const selectedChannelType = ref('');
const selectedChannelId = ref<number | undefined>(undefined);

const closeModal = () => {
    props.onClose();
}

// Reset payment channel when type changes
watch(transactionType, () => {
  selectedChannelType.value = '';
  selectedChannelId.value = undefined;
  if (transactionType.value !== 'expense') {
    selectedCategoryId.value = undefined;
  }
});

const getPaymentChannels = () => {
  const channels: { type: string; id: number; label: string }[] = [];
  bankAccounts.value.forEach(ba => {
    channels.push({ type: 'BANK_ACCOUNT', id: ba.id, label: `${ba.bank.name} - ${ba.nickName || 'Account'}` });
  });
  creditCards.value.forEach(cc => {
    channels.push({ type: 'CREDIT_CARD', id: cc.id, label: `${cc.bankAccount.bank.name} - ${cc.nickName || 'Credit Card'}` });
  });
  debitCards.value.forEach(dc => {
    channels.push({ type: 'DEBIT_CARD', id: dc.id, label: `${dc.bankAccount.bank.name} - ${dc.nickName || 'Debit Card'}` });
  });
  return channels;
}

const submitTransaction = async () => {
  try {
    const typeMap: Record<string, 'EXPENSE' | 'INCOME' | 'SELF_TRANSFER'> = {
      expense: 'EXPENSE',
      income: 'INCOME',
      selfTransfer: 'SELF_TRANSFER'
    };
    const request: any = {
      type: typeMap[transactionType.value],
      amount: amount.value,
      title: title.value,
      description: description.value,
      timestamp: new Date(date.value).toISOString(),
    };

    if (transactionType.value === 'expense') {
      request.sourceType = selectedChannelType.value;
      request.sourceId = selectedChannelId.value;
      if (selectedCategoryId.value != null) {
        request.metaData = { expenseCategoryId: selectedCategoryId.value };
      }
    } else if (transactionType.value === 'income') {
      request.destType = selectedChannelType.value;
      request.destId = selectedChannelId.value;
    } else if (transactionType.value === 'selfTransfer') {
      request.sourceType = selectedChannelType.value;
      request.sourceId = selectedChannelId.value;
      request.destType = selectedChannelType.value;
      request.destId = selectedChannelId.value;
    }

    await addTransaction(request);
    emit('saved');
    closeModal();
  } catch (err) {
    console.error("Failed to add transaction:", err);
  }
}

onMounted(async () => {
    try {
        categories.value = await getExpenseCategories();
        bankAccounts.value = await getBankAccounts();
        const ccs = await getCreditCards();
        const dcs = await getDebitCards();
        ccs.forEach(cc => cc.type = 'Credit');
        dcs.forEach(dc => dc.type = 'Debit');
        creditCards.value = ccs;
        debitCards.value = dcs;
    } catch(err) {
        console.error("API error:", err);
    }
});
</script>

<template>
    <div class="add-transaction-modal">
        <div class="modal-content">
            <div class="modal-header d-flex justify-content-between align-items-center">
                <div class="modal-title">Add Transaction</div>
                <button class="close-button" @click="closeModal"><X /></button>
            </div>
            <div class="modal-body d-flex flex-column gap-3 mt-3">
                <div class="d-flex justify-content-between">
                    <label class="transaction-type-selection d-flex align-items-center gap-2">
                        <input type="radio" value="expense" v-model="transactionType"/>Expense
                    </label>
                    <label class="transaction-type-selection d-flex align-items-center gap-2">
                        <input type="radio" value="income" v-model="transactionType"/>Income
                    </label>
                    <label class="transaction-type-selection d-flex align-items-center gap-2">
                        <input type="radio" value="selfTransfer" v-model="transactionType"/>Self Transfer
                    </label>
                </div>
                <input type="text" class="form-control" placeholder="Title" v-model="title" />
                <textarea class="form-control" placeholder="Description" rows="3" v-model="description"></textarea>
                
                <!-- Category selector (only for expense) -->
                <select v-if="transactionType === 'expense'" class="form-select" v-model="selectedCategoryId">
                    <option :value="undefined">Select Category</option>
                    <option v-for="category in categories" :key="category.id" :value="category.id">{{ category.name }}</option>
                </select>

                <!-- Payment channel selector -->
                <div class="d-flex gap-2">
                    <select class="form-select" v-model="selectedChannelType">
                        <option value="">Select Channel Type</option>
                        <option value="BANK_ACCOUNT">Bank Account</option>
                        <option value="CREDIT_CARD">Credit Card</option>
                        <option value="DEBIT_CARD">Debit Card</option>
                        <option value="WALLET">Wallet</option>
                    </select>
                    <select class="form-select" v-model="selectedChannelId" :disabled="!selectedChannelType">
                        <option :value="undefined">Select {{ channelTypeMap[selectedChannelType] || 'Channel' }}</option>
                        <option v-for="ch in getPaymentChannels().filter(c => c.type === selectedChannelType)" :key="ch.id" :value="ch.id">{{ ch.label }}</option>
                    </select>
                </div>

                <div class="d-flex gap-2">
                    <input type="number" class="form-control" placeholder="Amount" v-model="amount" />
                    <input type="date" class="form-control" v-model="date" />
                </div>
                <button class="btn btn-primary" @click="submitTransaction" :disabled="!title || !amount || !selectedChannelType || !selectedChannelId">Add Transaction</button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.close-button{
    background: none;
    border: none;
    cursor: pointer;
}
.modal-content{
    background-color: white;
    padding: 1rem;
    border-radius: 10px;
    width: 500px;
}
.add-transaction-modal {
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
.transaction-type-selection{
    background-color: rgb(210, 210, 210, 0.5);
    padding: 0.25rem;
    border-radius: 7px;
}
</style>
