<script setup lang="ts">
import { X } from 'lucide-vue-next';
import { ref, type Ref } from 'vue';
import { uploadExpenses } from '../api';

const emit = defineEmits<{
  close: []
}>();
const closeModal = () => {
  emit('close');
}
const checkForDuplicates = ref(true);
const duplicateStrategy = ref("SAME_DAY");
const expenseFile: Ref<File | null> = ref(null);

const onFileUpload = (event: Event) => {
    const inputElement = event.target as HTMLInputElement;
    if (inputElement.files?.length) {
        expenseFile.value = inputElement.files[0];
        console.log("file uploaded")
    }
}

const uploadFile = () => {
    if (expenseFile.value != null) {
        uploadExpenses(expenseFile.value, checkForDuplicates.value, duplicateStrategy.value);
    }
}
</script>
<template>
    <div class="modal-background">
    <div class="modal-content">
      <div class="d-flex justify-content-between align-items-center">
        <div class="modal-title">Import Transactions</div>
        <button class="close-button" @click="closeModal"><X /></button>
      </div>
      <hr/>
      <div class="d-flex flex-column gap-3">
        <div>
            <input type="file" accept=".csv" @change="onFileUpload"/>
        </div>
       <div class="d-flex flex-column">
            <label><input type="checkbox" v-model="checkForDuplicates"/> Exclude Duplicate Transactions</label>
            <div class="d-flex gap-2">
                <label><input :disabled="!checkForDuplicates" type="radio" v-model="duplicateStrategy" value="SAME_DAY"> Same Day</label>
                <label><input :disabled="!checkForDuplicates" type="radio" v-model="duplicateStrategy" value="ONE_DAY_DIFF"> 1 Day difference</label>
            </div>
        </div>
        <div class="d-flex justify-content-end">
            <button class="upload-button" @click="uploadFile">Upload</button>
        </div>
      </div>
    </div>
  </div>
</template>
<style scoped>
.close-button {
  background: none;
  border: none;
  cursor: pointer;
}
.modal-content {
  background-color: white;
  padding: 1rem;
  border-radius: 10px;
  width: 400px;
}
.modal-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}
.modal-title {
  font-size: 1.25rem;
  font-weight: 600;
}
.upload-button {
  padding: 0.25rem;
  border: 1px solid grey;
  border-radius: 10px;
}
</style>