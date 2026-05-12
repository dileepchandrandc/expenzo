<script setup lang="ts">
import { onMounted, ref, watch, type Ref } from 'vue';
import type { Expense } from '../models';
import type { ExpenseListViewProps } from './props';
import { getExpenses } from '../api';
import ExpenseCard from './ExpenseCard.vue';

const expenses: Ref<Expense[]> = ref<Expense[]>([]);
const props = defineProps<ExpenseListViewProps>();
const hasNext: Ref<boolean> = ref(false);
const pageSize: number = 10;
const page: Ref<number> = ref(0);

const fetchExpenses = async () => {
    if (props.year != undefined && props.month != undefined) {
        const response = await getExpenses(props.year, props.month, props.categoryId, page.value, pageSize);
        expenses.value = response.data
        hasNext.value = response.hasNext
    }
}
const nextPage = () => {
    page.value++;
}
onMounted(async () => {
    fetchExpenses()
});

watch(
  () => [props.year, props.month, props.categoryId],
  async () => {
    if (props.year != undefined && props.month != undefined) {
        const response = await getExpenses(props.year, props.month, props.categoryId, page.value, pageSize);
        expenses.value = response.data
        hasNext.value = response.hasNext
    }
  }
);

watch(
    () => [page.value],
    async () => {
        if (hasNext.value && props.year != undefined && props.month != undefined) {
            console.log("Fetching more expenses")
            const response = await getExpenses(props.year, props.month, props.categoryId, page.value, pageSize);
            hasNext.value = response.hasNext
            expenses.value = [
                ...expenses.value,
                ...response.data
            ]
        }
    }
)
</script>
<template>
    <div class="expense-list d-flex flex-column gap-2 mt-3">
        <div v-for="expense in expenses" :key="expense.id" @click="() => {
            selectExpense(expense);
        }">
            <ExpenseCard :title="expense.title" :amount="expense.amount" :date="expense.spentOn" :category="expense.category"/>
        </div>
    </div>
    <div class="load-more-container">
        <button @click="nextPage" class="button-load-more" :disabled="!hasNext">Load More</button>
    </div>
</template>
<style scoped>
.load-more-container {
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 1rem;
    width: 100%;
}

.button-load-more {
    background-color: rgb(91, 120, 205);
    color: white;
    border: none;
    border-radius: 6px;
    padding: 0.25rem 1rem;
    cursor: pointer;
    font-size: 0.9rem;
}

.button-load-more:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}
</style>