<script setup lang="ts">
import DailySpendingChart from './DailySpendingChart.vue';
import ExpenseByCategory from './ExpenseByCategory.vue';

</script>
<template>
    <div class="recent-expense-list">
        <div class="title">Expense Trend</div>
        <div class="container">
            <div class="row g-3 align-items-end">
                <div class="col-md-6"><DailySpendingChart /></div>
                <div class="col-md-6"><ExpenseByCategory /></div>
            </div>
        </div>
    </div>
</template>
<style scoped>
.recent-expense-list{
    background-color: white;
    padding: 1rem;
    border-radius: 10px;
}

.title{
    font-size: 1.25rem;
    font-weight: 500;
    margin-bottom: 0.5rem;
}
</style>