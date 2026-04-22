<script setup>
import { X } from 'lucide-vue-next';
import { ref } from 'vue';

const categories = [
    {
        'id': '1',
        'name': 'Food & Drinks'
    },
    {
        'id': '2',
        'name': 'Transport'
    },
    {
        'id': '3',
        'name': 'Entertainment'
    },
    {
        'id': '4',
        'name': 'Bills'
    }
]
const transactionType = ref('expense');
</script>

<template>
    <div class="add-transaction-modal">
        <div class="modal-content">
            <div class="modal-header d-flex justify-content-between align-items-center">
                <div class="modal-title">Add Transaction</div>
                <button class="close-button"><X /></button>
            </div>
            <div class="modal-body d-flex flex-column gap-3 mt-3">
                <div class="d-flex justify-content-between">
                    <label class="transaction-type-selection d-flex align-items-center gap-2">
                        <input type="radio" value="expense" v-model="transactionType"/>Expense
                    </label>
                    <label class="transaction-type-selection d-flex align-items-center gap-2">
                        <input type="radio" value="income" v-model="transactionType"/>Income
                    </label>
                    <label class="transaction-type-selection d-flex align-items-center gap-2">
                        <input type="radio" value="selfTransfer" v-model="transactionType"/>Self Transfer
                    </label>
                </div>
                <input type="text" class="form-control" placeholder="Title" />
                <textarea type="textarea" class="form-control" placeholder="Description" rows="3"></textarea>
                <select class="form-select">
                    <option v-for="category in categories" :value="category.id">{{ category.name }}</option>
                </select>
                <div class="d-flex gap-2">
                    <input type="number" class="form-control" placeholder="Amount" />
                    <input type="date" class="form-control" />
                </div>
                <button class="btn btn-primary">Add Transaction</button>
            </div>
        </div>
    </div>
</template>

<style scoped>
.close-button{
    background: none;
    border: none;
    cursor: pointer;
}
.modal-content{
    background-color: white;
    padding: 1rem;
    border-radius: 10px;
    width: 400px;
}
.add-transaction-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.4);
  display: flex;
  justify-content: center;
  align-items: center;
}
.transaction-type-selection{
    background-color: rgb(210, 210, 210, 0.5);
    padding: 0.25rem;
    border-radius: 7px;
}
</style>
