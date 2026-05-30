<script setup lang="ts">
import { CreditCard, Edit3, Trash2 } from 'lucide-vue-next';
import type { BankingCardComponentProps } from './props';
import type { BankingCard } from '../models';

const porp = defineProps<BankingCardComponentProps>();
const emit = defineEmits<{
  delete: [card: BankingCard]
}>();
</script>
<template>
    <div class="banking-card">
        <div class="d-flex justify-content-between">
            <div><CreditCard/></div>
            <div class="card-type">{{ porp.bankingCard.type }} card</div>
        </div>
        <div>
            <div class="card-nickname">{{ porp.bankingCard.nickName }}</div>
            <div class="card-bank-name">{{ porp.bankingCard.bankAccount.bank.name }}</div>
        </div>
        <hr/>
        <div class="d-flex justify-content-between align-items-center">
            <div class="d-flex flex-column justify-content-start">
                <div class="card-number-header">Card Number</div>
                <div>.... 9876</div>
            </div>
            <div class="d-flex gap-2">
                <button class="banking-card-action-button"><Edit3 :size="15"/></button>
                <button class="banking-card-action-button" @click="emit('delete', porp.bankingCard)"><Trash2 :size="15"/></button>
            </div>
        </div>
    </div>
</template>
<style scoped>
.card-bank-name {
    font-size: small;
}
.card-nickname{
    font-size: larger;
    font-weight: 700;
}
.card-type {
    padding: 5px;
    color: white;
    border-radius: 10px;
    background-color: rgb(255, 255, 255, 0.1);
    font-size: smaller;
}

.banking-card {
    padding: 1rem;
    background-image: linear-gradient(to bottom right, rgb(255, 76, 225), rgb(255, 147, 174));
    color: white;
    border-radius: 20px;
    min-width: 500px;
}

.banking-card-action-button {
    color: black;
    background-color: white;
    border: 1px solid gray;
    border-radius: 10px;
    padding: 0px 15px;
    justify-content: center;
    align-items: center;
}

.card-number-header {
    color: rgb(187, 187, 187);;
}
</style>