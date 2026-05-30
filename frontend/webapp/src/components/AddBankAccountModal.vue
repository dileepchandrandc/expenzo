<script setup lang="ts">
import { X } from 'lucide-vue-next';
import { ref, onMounted } from 'vue';
import { getBanks, createBankAccount } from '../api';
import type { Bank } from '../models';

const emit = defineEmits<{
  close: []
  saved: []
}>();

const banks = ref<Bank[]>([]);
const selectedBankId = ref<number | null>(null);
const nickName = ref('');

onMounted(async () => {
  try {
    banks.value = await getBanks();
  } catch (err) {
    console.error("Failed to load banks:", err);
  }
});

const closeModal = () => {
  emit('close');
}

const save = async () => {
  if (selectedBankId.value == null) return;
  try {
    await createBankAccount({
      bankId: selectedBankId.value,
      nickName: nickName.value
    });
    emit('saved');
    emit('close');
  } catch (err) {
    console.error("Failed to create bank account:", err);
  }
}
</script>

<template>
  <div class="modal-background">
    <div class="modal-content">
      <div class="d-flex justify-content-between align-items-center">
        <div class="modal-title">Add Bank Account</div>
        <button class="close-button" @click="closeModal"><X /></button>
      </div>
      <hr/>
      <div class="d-flex flex-column gap-3">
        <select class="form-select" v-model="selectedBankId">
          <option :value="null" disabled>Select Bank</option>
          <option v-for="bank in banks" :key="bank.id" :value="bank.id">{{ bank.name }}</option>
        </select>
        <input type="text" class="form-control" placeholder="Nickname (optional)" v-model="nickName" />
        <button class="btn btn-primary" @click="save" :disabled="selectedBankId == null">Save</button>
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
</style>
