<script setup lang="ts">
import { getFormattedDate, getPaymentChannelLabel } from '../utils';
import CleanModal from './clean/components/CleanModal.vue';
import type { ExpenseModalProps } from './props';
import { Edit, Trash, TriangleAlert } from 'lucide-vue-next';
import { deleteTransaction } from '../api';

const props: ExpenseModalProps = defineProps<ExpenseModalProps>();
const emit = defineEmits<{
  deleted: []
}>();

const handleDelete = async () => {
  if (confirm('Are you sure you want to delete this transaction?')) {
    try {
      await deleteTransaction(props.expense.id);
      emit('deleted');
      props.onClose();
    } catch (err) {
      console.error("Failed to delete transaction:", err);
    }
  }
}
</script>
<template>
    <CleanModal v-on:close="props.onClose">
        <template #header>
            <div class="d-flex flex-column">
                <div class="expense-modal-header">Expense Details</div>
                <div class="d-flex expense-modal-title">
                    <div>{{ props.expense.title }}</div>
                    <div>{{ getFormattedDate(props.expense.spentOn) }}</div>
                </div>
            </div>
        </template>
        <div class="d-flex flex-column gap-3">
            <div class="amount-container">
                <div class="d-flex flex-column">
                    <div class="amount-title-text">Amount paid</div>
                    <div class="amount-text">₹{{ props.expense.amount }}</div>
                    <div class="payment-channel-text">Paid via {{ getPaymentChannelLabel(props.expense.paymentSource.channelType) }}</div>
                </div>
            </div>
            <div class="details-container d-flex flex-column">
                <div class="d-flex justify-content-between border-bottom details-row">
                    <div>Title</div>
                    <div>{{ expense.title }}</div>
                </div>
                <div class="d-flex justify-content-between border-bottom details-row">
                    <div>Date</div>
                    <div>{{ getFormattedDate(expense.spentOn) }}</div>
                </div>
                <div class="d-flex justify-content-between border-bottom details-row">
                    <div>Category</div>
                    <div v-if="expense.category != undefined" class="expense-type">{{ expense.category?.name }}</div>
                    <div v-if="expense.category == undefined" class="missing-category"><TriangleAlert color="red"/> Missing category</div>
                </div>
                <div class="d-flex justify-content-between details-row">
                    <div>Payment method</div>
                    <div>{{ expense.paymentSource.bankName }} {{ getPaymentChannelLabel(expense.paymentSource.channelType) }}</div>
                </div>
            </div>
            <div class="options-container">
                <button class="edit-button"><Edit :size="15"/> Edit</button>
                <button class="delete-button" @click="handleDelete"><Trash :size="15"/> Delete</button>
            </div>
        </div>
    </CleanModal>
</template>
<style scoped>
.edit-button {
    border: 1px solid grey;
    border-radius: 10px;
    padding: 0.25rem 0.75rem;
    font-size: small;
    color: rgb(124, 124, 255);
}

.delete-button {
    border: 1px solid grey;
    border-radius: 10px;
    padding: 0.25rem 0.75rem;
    font-size: small;
    color: rgb(255, 96, 96);
}
.missing-category{
    display: flex;
    align-items: center;
    gap: 5px;
}
.details-container{
    border: 1px solid grey;
    border-radius: 10px;
}
.details-row {
    padding: 0.5rem
}
.border-bottom {
    border-bottom: 1px solid grey;
}
.options-container {
    display: flex;
    justify-content: end;
    gap: 10px;
}

.expense-modal-header {
    font-size: larger;
    font-weight: 700;
}

.expense-modal-title {
    font-size: medium;
    gap: 10px;
}

.amount-title-text{
    font-size: small;
}

.amount-text{
    font-size: large;
    font-weight: 600;
}

.payment-channel-text{
    font-size: small;
}

.amount-container{
    border-radius: 10px;
    border: 1px solid rgb(122, 122, 122);
    padding: 10px;
    background-color: rgb(227, 235, 171);
}

.expense-type{
    font-size: 0.875rem;
    color: #186ffc;
    background-color: rgba(102, 158, 255, 0.2);
    padding: 0.3rem;
    border-radius: 5px;
}
</style>