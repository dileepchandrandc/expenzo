<script setup lang="ts">
import ExpenseCard from '../ExpenseCard.vue';
import type { ExpenseCategory } from '../../models';
import { onMounted, ref, type Ref } from 'vue';
import { getExpenseCategories } from '../../api';

const categories: Ref<ExpenseCategory[]> = ref<ExpenseCategory[]>([]);

onMounted(async () => {
  categories.value = await getExpenseCategories();
  console.log(categories.value)
})

const buckets = [
    {
        'id': '5-2025',
        'name': 'May 2024'
    },
    {
        'id': '6-2025',
        'name': 'June 2024'
    },
    {
        'id': '7-2025',
        'name': 'July 2024'
    }
]
const expenses = {
  'total': 5,
  'totalAmount': {
    'value': 250.00,
    'uniCode': '$'
  },
  'data': [
    {
        'id': '1',
        'title': 'Grocery',
        'amount': 50.00,
        'date': new Date(2024, 5, 1),
        'type': 'Food & Drinks'
    },
    {
        'id': '1',
        'title': 'Grocery',
        'amount': 50.00,
        'date': new Date(2024, 5, 1),
        'type': 'Food & Drinks'
    },
    {
        'id': '1',
        'title': 'Grocery',
        'amount': 50.00,
        'date': new Date(2024, 5, 1),
        'type': 'Food & Drinks'
    },
    {
        'id': '1',
        'title': 'Grocery',
        'amount': 50.15,
        'date': new Date(2024, 5, 1),
        'type': 'Food & Drinks'
    },
    {
        'id': '1',
        'title': 'Grocery',
        'amount': 50.00,
        'date': new Date(2024, 5, 1),
        'type': 'Food & Drinks'
    }
  ]
}
</script>

<template>
  <div class="expense-page">
    <div class="page-title">Expense</div>
    <div class="d-flex filter-bar gap-5">
      <CleanSearchBox hint-text="Search for expenses" class="filter-bar-item" bg-color="transparent"/>
      <div class="filter-bar-item d-flex">
        <select class="filter-dropdown">
          <option v-for="category in categories" :value="category.id">{{ category.name }}</option>
         </select>
      </div>
      <div class="filter-bar-item d-flex">
        <select class="filter-dropdown">
          <option v-for="bucket in buckets" :value="bucket.id">{{ bucket.name }}</option>
        </select>
      </div>
    </div>
    <div class="d-flex justify-content-between mt-4">
      <div>Total Expenses: {{ expenses.total }}</div>
      <div class="d-flex gap-1 align-items-center ml-4">
        <div>Total Amount:</div>
        <div>{{ expenses.totalAmount.uniCode }}{{ expenses.totalAmount.value }}</div>
      </div>
    </div>
    <div class="expense-list d-flex flex-column gap-2 mt-3">
      <ExpenseCard v-for="expense in expenses['data']" :key="expense.id" :title="expense.title" :amount="expense.amount" :date="expense.date" :type="expense.type"/>
    </div>
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
