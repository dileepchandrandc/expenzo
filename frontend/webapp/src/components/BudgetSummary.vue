<script setup lang="ts">
import { onMounted, ref, type Ref } from 'vue';
import type { BudgetSummary, YearMonth } from '../models';
import { getBudgetSummary } from '../api';
import { getCurrentYearAndMonth } from '../utils';
import { Chart, type ChartConfiguration } from 'chart.js';
import ProgresBar from './ProgresBar.vue';

const budgetSummary: Ref<BudgetSummary> = ref({
  budgetName: "",
  budgetId: -1,
  budgetLimit: -1,
  totalSpent: -1,
  utilizations: [],
  untrackedSpent: -1,
  uncategorizedSpent: -1,
  budgetUsage: -1,
  untrackedUsage: -1,
  uncategorizedUsage: -1
});

onMounted(async () => {
  const current: YearMonth = getCurrentYearAndMonth();
  budgetSummary.value = await getBudgetSummary(current.year, current.month);
  const data = {
    labels: [
      'Used',
      'Remaning'
    ],
    datasets: [{
      data: [budgetSummary.value.totalSpent, budgetSummary.value.budgetLimit - budgetSummary.value.totalSpent],
      backgroundColor: [
        'rgb(141, 0, 0)',
        'rgb(0, 141, 30)'
      ],
      hoverOffset: 4
    }]
  };
  const config: ChartConfiguration<"doughnut"> = {
    type: 'doughnut',
    data: data,
    options: {
      plugins: {
        legend: {
          display: false
        }
      }
    }
  };
  const ctx : HTMLCanvasElement = document.getElementsByClassName("budget-usage-chart")[0] as HTMLCanvasElement
  new Chart(ctx, config);
});

const getProgressBarColor = (usage: number) => {
  if (usage < 70) return "#32a852";
  if (usage < 90) return "#a85232";
  return "#a83232";
}
</script>
<template>
    <div class="budget-summary">
        <div class="title">Budget Summary</div>
        <div class="d-flex flex-column gap-2">
          <div class="d-flex gap-2">
            <div class="budget-utilization-card">
              <div class="budget-utilization-title">Budget Usage</div>
              <div class="budget-utilization-value">{{ budgetSummary.budgetUsage.toFixed(2) }}%</div>
              <div>₹{{ budgetSummary.totalSpent }}</div>
            </div>
            <div class="budget-utilization-card">
              <div class="budget-utilization-title">Untracked</div>
              <div class="budget-utilization-value">{{ budgetSummary.untrackedUsage.toFixed(2) }}%</div>
              <div>₹{{ budgetSummary.untrackedSpent }}</div>
            </div>
            <div class="budget-utilization-card">
              <div class="budget-utilization-title">Uncategorized</div>
              <div class="budget-utilization-value">{{ budgetSummary.uncategorizedUsage.toFixed(2) }}%</div>
              <div>₹{{ budgetSummary.uncategorizedSpent }}</div>
            </div>
          </div>
          <div class="d-flex align-items-center gap-3">
            <div>
              <canvas class="budget-usage-chart"></canvas>
              <div class="d-flex flex-column align-items-center justify-content-center">
                <div class="remaining-amount">{{ budgetSummary.budgetLimit - budgetSummary.totalSpent }}</div>
                <div class="remaining-text">Remaining</div>
              </div>
            </div>
            <div>
              <div v-for="categorySummary in budgetSummary.utilizations" class="d-flex flex-column gap-1 category-budget-summary">
                <div v-if="categorySummary.partOfBudget">
                  <div class="d-flex justify-content-between align-items-center">
                    <div class="category-name">{{ categorySummary.categoryName }}</div>
                    <div class="budget-usage">{{ categorySummary.budgetUsage }}%</div>
                  </div>
                  <ProgresBar :progress="categorySummary.budgetUsage" :min-bar-width="300" :color="getProgressBarColor(categorySummary.budgetUsage)"/>
                  <div class="d-flex justify-content-between align-items-center">
                    <div class="budget-used-amount">{{ categorySummary.totalSpent }} / {{ categorySummary.spendLimit }}</div>
                    <div class="over-spending-card" v-if="categorySummary.budgetUsage > 100">Over</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
    </div>
</template>
<style scoped>
.budget-utilization-card {
  padding: 1rem;
  background-color: beige;
  border-radius: 10px;
}
.budget-utilization-title {
  font-size: small;
  color: gray;
}
.budget-utilization-value {
  font-size: large;
  font-weight: 700;
}
.category-budget-summary {
  padding: 2px 0px;
}
.over-spending-card {
  background-color: red;
  border-radius: 5px;
  padding: 2px;
  font-size: xx-small;
}
.category-name {
  font-size: medium;
}
.budget-usage {
  font-size: small;
}
.budget-used-amount {
  font-size: smaller;
  color: grey;
}
.remaining-amount {
  color: green;
  font-size: large;
}
.remaining-text {
  color: grey;
  font-size: x-small;
}
.budget-usage-chart {
  height: 200px;
}
.budget-summary{
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