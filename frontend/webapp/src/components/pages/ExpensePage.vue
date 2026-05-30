<script setup lang="ts">
import type { Expense, ExpenseBucket, ExpenseCategory } from '../../models';
import { onMounted, ref, type Ref } from 'vue';
import { getExpenseCategories, getExpenseBuckets } from '../../api';
import ExpenseListView from '../ExpenseListView.vue';
import ExpenseModal from '../ExpenseModal.vue';

interface PageState {
  bucket?: ExpenseBucket;
  category?: ExpenseCategory;
  page: number;
  size: number;
  hasNext: boolean;
}
interface ExpenseModalState {
  show: boolean;
  expense?: Expense;
}

const categories: Ref<ExpenseCategory[]> = ref<ExpenseCategory[]>([]);
const buckets: Ref<ExpenseBucket[]> = ref<ExpenseBucket[]>([]);
const refreshKey = ref(0);
const pageState: Ref<PageState> = ref({
  page: 0,
  size: 10,
  hasNext: true
});
const showExpenseModal: Ref<ExpenseModalState> = ref({
  show: false
});

onMounted(async () => {
  categories.value = await getExpenseCategories();
  buckets.value = await getExpenseBuckets();
  pageState.value.bucket = buckets.value[0] //Setting the firt bucket as initial bucket
  console.log("Month from parent = " + pageState.value.bucket.month)
})

const selectExpense = (expense: Expense) => {
  showExpenseModal.value.expense = expense;
  showExpenseModal.value.show = true;
}

const closeExpenseModal = () => {
  showExpenseModal.value.expense = undefined;
  showExpenseModal.value.show = false;
}

const handleExpenseDeleted = () => {
  refreshKey.value++;
  closeExpenseModal();
}
</script>

<template>
  <div class="expense-page">
    <div class="page-title">Expense</div>
    <div class="d-flex filter-bar gap-5">
      <!-- <CleanSearchBox hint-text="Search for expenses" class="filter-bar-item" bg-color="transparent"/> -->
      <div class="filter-bar-item d-flex">
        <select class="filter-dropdown" v-model="pageState.category">
          <option :value="undefined">All Categories</option>
          <option v-for="category in categories" :value="category" :key="category.id">{{ category.name }}</option>
         </select>
      </div>
      <div class="filter-bar-item d-flex">
        <select class="filter-dropdown" v-model="pageState.bucket">
          <option v-for="b in buckets" :value="b" >{{ b.name }}</option>
        </select>
      </div>
    </div>
    <div class="d-flex justify-content-between mt-4">
      <div>Total Expenses: 10</div>
      <div class="d-flex gap-1 align-items-center ml-4">
        <div>Total Amount:</div>
        <div>₹100</div>
      </div>
    </div>
    <ExpenseListView v-if="pageState.bucket != undefined" :key="refreshKey" :year="pageState.bucket?.year" :month="pageState.bucket?.month" :category-id="pageState.category?.id" :select-expense="selectExpense"/>
    <ExpenseModal v-if="showExpenseModal.show && showExpenseModal.expense != undefined" :expense="showExpenseModal.expense" @close="closeExpenseModal" @deleted="handleExpenseDeleted"/>
  </div>
</template>

<style scoped>
.expense-page{
    padding: 1rem;
}

.page-title{
    font-size: 1.5rem;
    font-weight: 300;
}

.filter-bar-item{
    padding: 0.3rem;
    border-radius: 4px;
    background-color: rgb(235, 235, 235);
    width: 100%;
}

.filter-bar {
  background-color: white;
  padding: 1rem;
  border-radius: 10px;
  color: rgb(192, 192, 192);
}

.filter-dropdown {
    background-color: transparent;
    border: none;
    outline: none;
    width: 100%;
}

.search-box{
    border: none;
    outline: none;
    background-color: transparent;
    width: 100%;
}
</style>
