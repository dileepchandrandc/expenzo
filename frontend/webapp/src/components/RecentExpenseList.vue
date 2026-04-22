
<script setup>

const recentExpenses = [
    {
        'id': '1',
        'title': 'Grocery',
        'amount': {
            'value': 50.00,
            'uniCode': '$'
        },
        'date': new Date(2024, 5, 1),
        'type': 'Food & Drinks'
    },
    {
        'id': '1',
        'title': 'Grocery',
        'amount': {
            'value': 50.00,
            'uniCode': '$'
        },
        'date': new Date(2024, 5, 1),
        'type': 'Food & Drinks'
    },
    {
        'id': '1',
        'title': 'Grocery',
        'amount': {
            'value': 50.00,
            'uniCode': '$'
        },
        'date': new Date(2024, 5, 1),
        'type': 'Food & Drinks'
    },
    {
        'id': '1',
        'title': 'Grocery',
        'amount': {
            'value': 50.15,
            'uniCode': '$'
        },
        'date': new Date(2024, 5, 1),
        'type': 'Food & Drinks'
    },
    {
        'id': '1',
        'title': 'Grocery',
        'amount': {
            'value': 50.00,
            'uniCode': '$'
        },
        'date': new Date(2024, 5, 1),
        'type': 'Food & Drinks'
    }
]
</script>

<template>
    <div class="recent-expense-list">
        <div class="title">Recent Expenses</div>
        <div class="d-flex flex-column gap-1">
            <div v-for="expense in recentExpenses" :key="expense.id" class="expense d-flex justify-content-between align-items-center">
                <div>
                    <div class="expense-title">{{ expense.title }}</div>
                    <div class="d-flex gap-2">
                        <div class="expense-date">{{ expense.date.toLocaleDateString() }}</div>
                        <div class="expense-type">{{ expense.type }}</div>
                    </div>
                </div>
                <div>
                    <div class="expense-amount">{{ expense.amount.uniCode }}{{ expense.amount.value }}</div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.recent-expense-list{
    background-color: white;
    padding: 1rem;
    border-radius: 10px;
}

.title{
    font-size: 1.25rem;
    font-weight: 500;
    margin-bottom: 0.5rem;
}
.expense{
    padding: 0.5rem;
    margin-bottom: 0.5rem;
    width: 100%;
    border-bottom: #666 1px solid;
}

.expense:last-child{
    border-bottom: none;
}

.expense-type{
    font-size: 0.875rem;
    color: #666;
}

.expense-title{
    font-size: 1rem;
    font-weight: 500;
}

.expense-date{
    font-size: 0.875rem;
    color: #666;
}

.expense-amount{
    font-size: 1rem;
    font-weight: 500;
}
</style>
