<script setup lang="ts">
import { Plus } from 'lucide-vue-next';
import type { BankAccount, BankingCard } from '../../models';
import BankAccountCard from '../BankAccountCard.vue';
import BankingCardComponent from '../BankingCardComponent.vue';
import AddBankAccountModal from '../AddBankAccountModal.vue';
import AddCardModal from '../AddCardModal.vue';
import { onMounted, type Ref, ref} from 'vue';
import { getCreditCards, getDebitCards, getBankAccounts, deleteBankAccount, deleteCreditCard, deleteDebitCard } from '../../api';

const bankAccounts: Ref<BankAccount[]> = ref([]);
const bankingCards: Ref<BankingCard[]> = ref([]);
const showAddBankAccountModal = ref(false);
const showAddCardModal = ref(false);

const loadData = async () => {
    bankAccounts.value = await getBankAccounts();
    const creditCards = await getCreditCards();
    const debitCards = await getDebitCards();
    creditCards.forEach(cc => cc.type = 'Credit');
    debitCards.forEach(dc => dc.type = 'Debit');
    bankingCards.value = [
        ...creditCards,
        ...debitCards
    ]
};

onMounted(async () => {
    await loadData();
});

const handleDeleteBankAccount = async (id: number) => {
    if (confirm('Are you sure you want to delete this bank account?')) {
        try {
            await deleteBankAccount(id);
            await loadData();
        } catch (err) {
            console.error("Failed to delete bank account:", err);
        }
    }
}

const handleDeleteCard = async (card: BankingCard) => {
    if (confirm('Are you sure you want to delete this card?')) {
        try {
            if (card.type === 'Credit') {
                await deleteCreditCard(card.id);
            } else {
                await deleteDebitCard(card.id);
            }
            await loadData();
        } catch (err) {
            console.error("Failed to delete card:", err);
        }
    }
}
</script>
<template>
    <AddBankAccountModal v-if="showAddBankAccountModal" @close="showAddBankAccountModal = false" @saved="loadData"/>
    <AddCardModal v-if="showAddCardModal" @close="showAddCardModal = false" @saved="loadData"/>
    <div class="payment-channel-page">
        <div>
            <div class="section-title">Bank Acccounts</div>
            <div class="grid">
                <BankAccountCard v-for="bankAccount in bankAccounts" :key="bankAccount.id" :bank-account="bankAccount" @delete="handleDeleteBankAccount"/>
                <button class="add-button" @click="showAddBankAccountModal = true">
                    <Plus/>
                    <div>Add Bank Account</div>
                </button>
            </div>
        </div>
        <div class="">
            <div class="section-title">Credit Cards & Debit Cards</div>
            <div class="grid">
                <BankingCardComponent v-for="bankingCard in bankingCards" :key="bankingCard.id" :banking-card="bankingCard" @delete="handleDeleteCard"/>
                <button class="add-button" @click="showAddCardModal = true">
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
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 2rem;
    background: none;
    cursor: pointer;
    min-width: 500px;
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