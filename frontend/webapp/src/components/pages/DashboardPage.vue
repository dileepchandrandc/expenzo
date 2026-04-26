<script setup lang="ts">
import ExpenseByCategory from '../ExpenseByCategory.vue';
import RecentExpenseList from '../RecentExpenseList.vue'
import OverViewCard from '../OverViewCard.vue';
import AddTransactionModal from '../AddTransactionModal.vue';
import { ref, onMounted } from 'vue';
import { getDashboradOverview } from '../../api';
import type { ExpenseOverview } from '../../models';

const showModal = ref(false);
const monthlyOverView = ref<ExpenseOverview>({
    income: 0,
    expense: 0,
    bill: 0,
    averagePerDay: 0
});
onMounted(async () => {
    try {   
        monthlyOverView.value = await getDashboradOverview();
    } catch(err) {
        console.error("API error:", err);
    }
});
</script>

<template>
    <AddTransactionModal v-if="showModal"/>
    <div class="dashboard-page container-fluid">
    <div class="page-title">Dashboard</div>
    <div class="dashbord-section-title">Expense Overview</div>
    <div class="d-flex gap-4 p-0 mb-4 mt-2">
        <OverViewCard :title="'Income'" :amount="monthlyOverView.income" :bottomText="'Total income for the month'" />
        <OverViewCard :title="'Expense'" :amount="monthlyOverView.expense" :bottomText="'Total expense for the month'" />
        <OverViewCard :title="'Bills'" :amount="monthlyOverView.bill" :bottomText="'Total bills for the month'" />
        <OverViewCard :title="'Average Spend Per Day'" :amount="monthlyOverView.averagePerDay" :bottomText="'Average spend per day for the month'" />
    </div>
    <div class="container-fuild">
      <div class="row g-3">
        <div class="col">
            <RecentExpenseList />
        </div>
        <div class="col">
            <ExpenseByCategory />
        </div>
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
</style>