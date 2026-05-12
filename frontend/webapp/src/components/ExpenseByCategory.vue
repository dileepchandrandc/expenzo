<script setup lang="ts">
import {ref, onMounted} from 'vue';
import { getExpenseGroupedByCategory } from '../api';
import type { ExpenseByCategory, YearMonth } from '../models';
import { getCurrentYearAndMonth } from '../utils';
import { Chart } from 'chart.js/auto';

const expenseByCategory = ref<ExpenseByCategory[]>([])
const max = ref(0)
onMounted(async () => {
    try {
        const current: YearMonth = getCurrentYearAndMonth();
        expenseByCategory.value = await getExpenseGroupedByCategory(current.year, current.month)
        max.value = Math.max(...expenseByCategory.value.map(e => e.amount))
        const ctx: HTMLCanvasElement = document.getElementsByClassName("category-graph")
        const data = {
            labels: expenseByCategory.value.map(ec => ec.category.name),
            datasets: [
                {
                    data: expenseByCategory.value.map(ec => ec.amount),
                    hoverOffset: 4
                }
            ]
        }
        const chartData = {
            type: "doughnut",
            data: data
        }
        new Chart(ctx, chartData);
    } catch (err) {
        console.error("API error:", err);
    }
})
</script>

<template>
    <div class="expense-by-category">
        <div class="title">Expenses by Category</div>
        <canvas class="category-graph"></canvas>
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
.category-graph {
    max-height: 400px;
}
</style>
