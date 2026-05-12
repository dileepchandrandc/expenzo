
<script setup lang="ts">
import { onMounted, ref } from 'vue';
import type { Expense, YearMonth } from '../models';
import { getExpenses } from '../api';
import { getCurrentYearAndMonth, getFormattedDate } from '../utils';

const recentExpenses = ref<Expense[]>([]);
onMounted(async() => {
    const current: YearMonth = getCurrentYearAndMonth();
    const res = await getExpenses(current.year, current.month, undefined, 0, 5);
    recentExpenses.value = res.data;
});
</script>

<template>
    <div class="recent-expense-list">
        <div class="title">Recent Expenses</div>
        <div class="d-flex flex-column gap-1">
            <div v-for="expense in recentExpenses" :key="expense.id" class="expense d-flex justify-content-between align-items-center">
                <div>
                    <div class="expense-title">{{ expense.title }}</div>
                    <div class="d-flex gap-2">
                        <div class="expense-date">{{ getFormattedDate(expense.spentOn) }}</div>
                        <div class="expense-type">{{ expense.category?.name }}</div>
                    </div>
                </div>
                <div>
                    <div class="expense-amount">{{ expense.amount }}₹</div>
                </div>
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
.expense{
    padding: 0.5rem;
    margin-bottom: 0.5rem;
    width: 100%;
    border-bottom: #666 1px solid;
}

.expense:last-child{
    border-bottom: none;
}

.expense-type{
    font-size: 0.875rem;
    color: #666;
}

.expense-title{
    font-size: 1rem;
    font-weight: 500;
}

.expense-date{
    font-size: 0.875rem;
    color: #666;
}

.expense-amount{
    font-size: 1rem;
    font-weight: 500;
}
</style>
