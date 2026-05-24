<script setup lang="ts">
import {ref, onMounted} from 'vue';
import { getExpenseGroupedByCategory } from '../api';
import type { ExpenseByCategory, YearMonth } from '../models';
import { getCurrentYearAndMonth } from '../utils';
import { Chart, type ChartConfiguration } from 'chart.js/auto';

const expenseByCategory = ref<ExpenseByCategory[]>([])
onMounted(async () => {
    try {
        const current: YearMonth = getCurrentYearAndMonth();
        expenseByCategory.value = await getExpenseGroupedByCategory(current.year, current.month)
        const ctx : HTMLCanvasElement = document.getElementsByClassName("category-graph")[0] as HTMLCanvasElement
        const data = {
            labels: expenseByCategory.value.map(ec => ec.category.name),
            datasets: [
                {
                    data: expenseByCategory.value.map(ec => ec.amount),
                    hoverOffset: 4
                }
            ]
        }
        const chartConfig: ChartConfiguration<"doughnut"> = {
            type: "doughnut",
            data: data
        }
        new Chart(ctx, chartConfig);
    } catch (err) {
        console.error("API error:", err);
    }
})
</script>

<template>
    <div class="d-flex flex-column align-items-center">
      <canvas class="category-graph"></canvas>
      <div>Expense By Category</div>
    </div>
</template>

<style scoped>
.category-graph {
    max-height: 400px;
}
</style>
