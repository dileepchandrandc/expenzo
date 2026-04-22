
<script setup>
import { Pencil, Trash } from 'lucide-vue-next';

const props = defineProps({
    title: String,
    amount: {
        value: Number,
        uniCode: String
    },
    date: Date,
    type: String
})
</script>

<template>
    <div class="expense-card d-flex justify-content-between align-items-center">
        <div>
            <div class="expense-title">{{ title }}</div>
            <div class="d-flex gap-2 align-items-center">
                <div class="expense-type">{{ type }}</div>
                <div class="expense-date">{{ date.toLocaleDateString() }}</div>                
            </div>
        </div>
        <div class="d-flex gap-2 align-items-center">
            <div class="expense-amount">{{ amount.uniCode }}{{ amount.value }}</div>
            <button class="action-button"><Pencil size="15"/></button>
            <button class="action-button"><Trash size="15" color="red"/></button>
        </div>
    </div>
</template>


<style scoped>
.expense-card{
    padding: 0.5rem;
    border: 1px solid #e7e7e7;
    border-radius: 10px;
    margin-bottom: 0.5rem;
    width: 100%;
    background-color: white;
}

.expense-type{
    font-size: 0.875rem;
    color: #186ffc;
    background-color: rgba(102, 158, 255, 0.2);
    padding: 0.3rem;
    border-radius: 5px;
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

.action-button{
    background-color: transparent;
    border: none;
    cursor: pointer;
}
</style>
