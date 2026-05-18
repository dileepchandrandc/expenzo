<script setup lang="ts">
import { Plus } from 'lucide-vue-next';
import type { BankAccount, BankingCard } from '../../models';
import BankAccountCard from '../BankAccountCard.vue';
import BankingCardComponent from '../BankingCardComponent.vue';
import { onMounted, type Ref, ref} from 'vue';
import { getCreditCards, getDebitCards, getBankAccounts } from '../../api';

const bankAccounts: Ref<BankAccount[]> = ref([]);
const bankingCards: Ref<BankingCard[]> = ref([])

onMounted(async () => {
    bankAccounts.value = await getBankAccounts();
    const creditCards = await getCreditCards();
    const debitCards = await getDebitCards();
    creditCards.forEach(cc => cc.type = 'Credit');
    debitCards.forEach(dc => dc.type = 'Debit');
    bankingCards.value = [
        ...creditCards,
        ...debitCards
    ]
});



</script>
<template>
    <div class="payment-channel-page">
        <div>
            <div class="section-title">Bank Acccounts</div>
            <div class="grid">
                <BankAccountCard v-for="bankAccount in bankAccounts" :bank-account="bankAccount"/>
                <button class="add-button">
                    <Plus/>
                    <div>Add Bank Account</div>
                </button>
            </div>
        </div>
        <div class="">
            <div class="section-title">Credit Cards & Debit Cards</div>
            <div class="grid">
                <BankingCardComponent v-for="bankingCard in bankingCards" :banking-card="bankingCard"/>
                <button class="add-button">
                    <Plus/>
                    <div>Add Credit / Debit Card</div>
                </button>
            </div>
        </div>
    </div>
</template>
<style scoped>
.section-title {
    padding-bottom: 10px;
    font-size: large;
}
.add-button {
    border: 1px dashed grey;
    border-radius: 20px;
}

.payment-channel-page{
    padding: 1rem;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 1rem;
}
</style>