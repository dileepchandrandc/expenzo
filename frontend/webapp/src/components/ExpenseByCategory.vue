
<script setup>
const expenseByCategory = [
    {
        'category': 'Food & Drinks',
        'amount': {
            'value': 150.00,
            'uniCode': '$'
        }
    },
    {
        'category': 'Transportation',
        'amount': {
            'value': 80.00,
            'uniCode': '$'
        }
    },
    {
        'category': 'Entertainment',
        'amount': {
            'value': 120.00,
            'uniCode': '$'
        }
    },
    {
        'category': 'Health',
        'amount': {
            'value': 60.00,
            'uniCode': '$'
        }
    },
    {
        'category': 'Education',
        'amount': {
            'value': 200.00,
            'uniCode': '$'
        }
    }
]
const max = Math.max(...expenseByCategory.map(e => e.amount.value))
</script>

<template>
    <div class="expense-by-category">
        <div class="title">Expenses by Category</div>
        <div class="d-flex flex-column gap-1">
            <div v-for="item in expenseByCategory" :key="item.category" class="chart-row">
            <div class="label">{{ item.category }}</div>
            <div class="bar-container">
                <div class="bar" :style="{ width: (item.amount.value / max * 100) + '%' }"></div>
            </div>
            <div class="value">{{ item.amount.uniCode }}{{ item.amount.value }}</div>
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

.chart {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.chart-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.label {
  width: 100px;
  font-size: 14px;
}

.bar-container {
  flex: 1;
  background: #eee;
  height: 10px;
  border-radius: 5px;
  overflow: hidden;
}

.bar {
  height: 100%;
  background: #4dabf7;
  border-radius: 5px;
}

.value {
  width: 60px;
  text-align: right;
  text-align: start;
}
</style>
