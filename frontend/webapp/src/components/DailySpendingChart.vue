<script setup lang="ts">
import {onMounted, ref, type Ref} from 'vue';
import type { DailySpendingTrend, YearMonth } from '../models';
import { getCurrentYearAndMonth } from '../utils';
import { Chart, type ChartConfiguration } from 'chart.js/auto';
import { getDailySpendingTrend } from '../api';

const expenseTrend: Ref<DailySpendingTrend[]> = ref([]);
onMounted(async () => {
    try {
        const current: YearMonth = getCurrentYearAndMonth();
        expenseTrend.value = await getDailySpendingTrend(current.year, current.month);
        const chartData = {
          labels: expenseTrend.value.map(et => et.day),
          datasets: [
            {
              axis: 'y',
              label: 'Spending trend',
              data: expenseTrend.value.map(et => et.totalAmountSpent)
            }
          ]
        }
        const consfig: ChartConfiguration<"line">  = {
          type: 'line',
          data: chartData,
          options: {
            indexAxis: 'x',
            scales: {
              x: {
                beginAtZero: true
              }
            }
          }
        };
        const ctx : HTMLCanvasElement = document.getElementsByClassName("spend-chart")[0] as HTMLCanvasElement
        new Chart(ctx, consfig);
    } catch (err) {
        console.error("API error:", err);
    }
})
</script>

<template>
    <div class="d-flex flex-column align-items-center">
      <canvas class="spend-chart"></canvas>
      <div>Daily Expense Trend</div>
    </div>
</template>

<style scoped>
.category-graph {
    max-height: 400px;
}
</style>
