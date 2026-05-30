<script setup lang="ts">
import { Chart, type ChartConfiguration } from 'chart.js/auto';
import { onMounted, ref, type Ref } from 'vue';
import type { BudgetSummary, YearMonth, BudgetResponse } from '../../models';
import { getBudgetSummary, getMyBudget } from '../../api';
import { getCurrentYearAndMonth } from '../../utils';
import BudgetSettingsModal from '../BudgetSettingsModal.vue';
import { Plus, Pencil } from 'lucide-vue-next';

const budgetSummary: Ref<BudgetSummary | undefined> = ref();
const existingBudget: Ref<BudgetResponse | null> = ref(null);
const showBudgetModal = ref(false);
const loadKey = ref(0);

const loadData = async () => {
    const current: YearMonth = getCurrentYearAndMonth();
    try {
        budgetSummary.value = await getBudgetSummary(current.year, current.month);
    } catch (err) {
        console.error("Failed to load budget summary:", err);
    }
}

const loadBudgetConfig = async () => {
    existingBudget.value = await getMyBudget();
}

onMounted(async () => {
    await Promise.all([loadData(), loadBudgetConfig()]);
    if (budgetSummary.value) {
        renderCharts();
    }
});

// Re-render charts when data changes
const renderCharts = () => {
    if (!budgetSummary.value) return;
    const spentVsLimitBarChart : HTMLCanvasElement = document.querySelector(".spend-vs-limit-bar-chart") as HTMLCanvasElement
    const categoryPieChart : HTMLCanvasElement = document.querySelector(".budget-category-pie-chart") as HTMLCanvasElement
    if (!spentVsLimitBarChart || !categoryPieChart) return;

    const spentVsLimitData = {
        labels: budgetSummary.value.utilizations.map(cu => cu.categoryName),
        datasets: [
            {
                label: 'Limit',
                data: budgetSummary.value.utilizations.map(cu => cu.spendLimit)
            },
            {
                label: 'Spent',
                data: budgetSummary.value.utilizations.map(cu => cu.totalSpent)
            }
        ]
    }
    const barchartConfig: ChartConfiguration<"bar"> = {
        type: 'bar',
        data: spentVsLimitData,
        options: {
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    }
    new Chart(spentVsLimitBarChart, barchartConfig);
    const categoryPieChartData = {
        labels: [...budgetSummary.value.utilizations.filter(cu => cu.partOfBudget).map(cu => cu.categoryName), 'Uncategorized', 'Untracked'],
        datasets: [
            {
                data: [
                    ...budgetSummary.value.utilizations.filter(cu => cu.partOfBudget && cu.categoryId > 0).map(cu => cu.totalSpent),
                    budgetSummary.value.utilizations.filter(cu => !cu.partOfBudget && cu.categoryId > 0).map(cu => cu.totalSpent).reduce((a, b) => a + b, 0),
                    budgetSummary.value.utilizations.filter(cu => !cu.partOfBudget && cu.categoryId == 0).map(cu => cu.totalSpent).reduce((a, b) => a + b, 0)
                ]
            }
        ]
    }
    const categoryPieChartConfig: ChartConfiguration<"doughnut"> = {
        type: "doughnut",
        data: categoryPieChartData
    }
    new Chart(categoryPieChart, categoryPieChartConfig);
};

const handleBudgetSaved = async () => {
    await loadBudgetConfig();
    await loadData();
    loadKey.value++;
}

const handleBudgetDeleted = async () => {
    existingBudget.value = null;
    await loadData();
    loadKey.value++;
}
</script>
<template>
    <BudgetSettingsModal v-if="showBudgetModal" :existing-budget="existingBudget" @close="showBudgetModal = false" @saved="handleBudgetSaved" @deleted="handleBudgetDeleted"/>
    <div class="budget-page container-fluid">
        <div class="d-flex justify-content-between align-items-center">
            <div class="page-title">Monthly Budget</div>
            <button v-if="existingBudget" class="btn btn-outline-primary d-flex align-items-center gap-1" @click="showBudgetModal = true">
                <Pencil :size="18"/> View / Edit Budget
            </button>
            <button v-else class="btn btn-primary d-flex align-items-center gap-1" @click="showBudgetModal = true">
                <Plus :size="18"/> Create Budget
            </button>
        </div>
        <div v-if="budgetSummary" class="d-flex gap-2 mt-2">
            <div class="details-card d-flex flex-column">
                <div class="details-card-title">Budget Limit</div>
                <div class="details-card-value">₹{{ budgetSummary.budgetLimit.toLocaleString() }}</div>
            </div>
            <div class="details-card d-flex flex-column">
                <div class="details-card-title">Total Spend</div>
                <div class="details-card-value">₹{{ budgetSummary.totalSpent.toLocaleString() }}</div>
                <div class="details-card-bottom">{{ budgetSummary.budgetUsage.toFixed(1) }}% utilized</div>
            </div>
            <div class="details-card d-flex flex-column">
                <div class="details-card-title">Remaining</div>
                <div class="details-card-value">₹{{ (budgetSummary.budgetLimit - budgetSummary.totalSpent).toLocaleString() }}</div>
                <div class="details-card-bottom">{{ (100 - budgetSummary.budgetUsage).toFixed(1) }}% left</div>
            </div>
            <div class="details-card d-flex flex-column">
                <div class="details-card-title">Categories</div>
                <div class="details-card-value">{{ budgetSummary.utilizations.length }}</div>
                <div class="details-card-bottom">{{ budgetSummary.utilizations.filter(c => c.partOfBudget).length }} tracked</div>
            </div>
        </div>
        <div class="container mt-3" :key="loadKey">
            <div class="row">
                <div class="col"><canvas class="spend-vs-limit-bar-chart"></canvas></div>
                <div class="col-auto"><canvas class="budget-category-pie-chart"></canvas></div>
            </div>
        </div>
    </div>
</template>
<style scoped>
.budget-category-pie-chart {
    width: 400px;
}
.budget-page{
    padding: 1rem;
}
.page-title{
    font-size: 1.5rem;
    font-weight: 300;
}
.details-card {
    padding: 1rem;
    border-radius: 10px;
    border: 1px solid grey;
    min-width: 200px;
    background-color: white;
}
.details-card-title {
    color: grey;
    font-size: small;
}
.details-card-value {
    color: black;
    font-size: larger;
    font-weight: 700;
}
.details-card-bottom {
    color: grey;
    font-size: small;
}
</style>