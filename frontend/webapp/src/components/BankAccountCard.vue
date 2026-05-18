<script setup lang="ts">
import { Edit3, LandmarkIcon, Trash2 } from 'lucide-vue-next';
import type { BankAccountCardProps } from './props';

const props = defineProps<BankAccountCardProps>();
</script>
<template>
    <div class="bank-account-card d-flex flex-column">
        <div class="d-flex justify-content-between">
            <LandmarkIcon/>
            <div class="bank-account-type-card">{{ props.bankAccount.accountType }} account</div>
        </div>
        <div v-if="props.bankAccount.nickName != undefined" class="bank-account-nickname">{{ props.bankAccount.nickName }}</div>
        <div class="bank-name">{{ props.bankAccount.bank.name }}</div>
        <hr/>
        <div class="d-flex justify-content-between align-items-center">
            <div class="">
                <div class="account-type-header">Account Type</div>
                <div>{{ props.bankAccount.accountType }}</div>
            </div>
            <div class="d-flex gap-2">
                <button class="bank-account-action-button"><Edit3 :size="15"/></button>
                <button class="bank-account-action-button"><Trash2 :size="15"/></button>
            </div>
        </div>
    </div>
</template>
<style scoped>
.bank-account-card {
    padding: 1rem;
    background-image: linear-gradient(to bottom right, rgb(85, 76, 255), rgb(122, 191, 255));
    color: white;
    border-radius: 20px;
    min-width: 500px;
}

.bank-account-nickname{
    font-size: larger;
    font-weight: 700;
}

.bank-account-type-card {
    padding: 5px;
    color: white;
    border-radius: 10px;
    background-color: rgb(255, 255, 255, 0.1);
    font-size: smaller;
}

.bank-account-action-button {
    color: black;
    background-color: white;
    border: 1px solid gray;
    border-radius: 10px;
    padding: 0px 15px;
    justify-content: center;
    align-items: center;
}

.account-type-header {
    color: rgb(187, 187, 187);
}

.bank-name {
    font-size: small;
}
</style>