<script setup lang="ts">
import {ref, onMounted} from 'vue';
import { getExpenseGroupedByCategory } from '../api';
import { CleanHorizondalBar } from './clean/components';
import type { ExpenseByCategory } from '../models';

const expenseByCategory = ref<ExpenseByCategory[]>([])
const max = ref(0)
onMounted(async () => {
    try {
        expenseByCategory.value = await getExpenseGroupedByCategory()
        max.value = Math.max(...expenseByCategory.value.map(e => e.amount))
    } catch (err) {
        console.error("API error:", err);
    }
})
</script>

<template>
    <div class="expense-by-category">
        <div class="title">Expenses by Category</div>
        <div class="d-flex flex-column gap-1">
            <div v-for="item in expenseByCategory" :key="item.categoryId" class="chart-row">
                <div class="label">{{ item.categoryName }}</div>
                <CleanHorizondalBar :value="item.amount" :max-value="max"/>
                <div class="value">₹{{ item.amount }}</div>
            </div>
        </div>
    </div>

</template>

<style scoped>
.expense-by-category{
    background-color: white;
    padding: 1rem;
    border-radius: 10px;
}

.title{
    font-size: 1.25rem;
    font-weight: 500;
    margin-bottom: 0.5rem;
}
.chart-row {
  display: flex;
  align-items: center;
  gap: 5px;
}
.label {
  flex: 0 1 150px;
  min-width: 80px;
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.bar {
  flex: 1;
}
.value {
  flex: 0 0 auto;
  min-width: 60px;
  text-align: right;
  white-space: nowrap;
}
</style>
