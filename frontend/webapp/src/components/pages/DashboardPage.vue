<script setup lang="ts">
import RecentExpenseList from '../RecentExpenseList.vue'
import OverViewCard from '../OverViewCard.vue';
import AddTransactionModal from '../AddTransactionModal.vue';
import { ref, onMounted, type Ref } from 'vue';
import { getMonthlyOverview } from '../../api';
import type { MonthlyExpenseOverview } from '../../models';
import { getCurrentYearAndMonth } from '../../utils';
import { Plus } from 'lucide-vue-next';
import ExpenseTrendCard from '../ExpenseTrendCard.vue';
import BudgetSummary from '../BudgetSummary.vue';

const addtransactionModal = ref(false);
const monthlyOverView: Ref<MonthlyExpenseOverview> = ref<MonthlyExpenseOverview>({
    income: 0,
    expense: 0,
    bill: 0,
    avgSpentPerDay: 0
});

const showAddTransactionModal = () => {
    addtransactionModal.value = true
}

const closeAddTransactionModal = () => {
    addtransactionModal.value = false
}

const refreshData = async () => {
    try {
        const current = getCurrentYearAndMonth();
        monthlyOverView.value = await getMonthlyOverview(current.year, current.month);
    } catch(err) {
        console.error("API error:", err);
    }
}

onMounted(async () => {
    await refreshData();
});
</script>

<template>
    <AddTransactionModal v-if="addtransactionModal" @close="closeAddTransactionModal" @saved="refreshData"/>
    <div class="dashboard-page container-fluid">
        <div class="d-flex justify-content-between">
            <div class="page-title">Dashboard</div>
            <button @click="showAddTransactionModal" class="add-transaction-button"><Plus/> Add Transaction</button>
        </div>
        <div class="dashbord-section-title">Expense Overview</div>
        <div class="d-flex gap-4 p-0 mb-4 mt-2">
            <OverViewCard :title="'Income'" :amount="monthlyOverView.income" :bottomText="'Total income for the month'" />
            <OverViewCard :title="'Expense'" :amount="monthlyOverView.expense" :bottomText="'Total expense for the month'" />
            <!-- <OverViewCard :title="'Bills'" :amount="monthlyOverView.bill" :bottomText="'Total bills for the month'" /> -->
            <OverViewCard :title="'Average Spend Per Day'" :amount="monthlyOverView.avgSpentPerDay" :bottomText="'Average spend per day for the month'" />
        </div>
        <div class="container-fluid">
            <div class="row g-3">
                <div class="col"><RecentExpenseList /></div>
                <div class="col"><BudgetSummary /></div>
            </div>
            <div class="row g-3 mt-1">
                <div class="col"><ExpenseTrendCard/></div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.dashboard-page{
    padding: 1rem;
}

.page-title{
    font-size: 1.5rem;
    font-weight: 300;
}

.dashbord-section-title{
    font-size: 1rem;
    margin-top: 1rem;
}

.add-transaction-button {
    display: flex;
    align-items: center;
    gap: 5px;
    border: 1px solid grey;
    border-radius: 20px;
    padding: 10px;
    color: white;
    background-color: rgb(60, 127, 69);
}
</style>