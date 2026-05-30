<script setup lang="ts">
import { X, Trash2, TriangleAlert } from 'lucide-vue-next';
import { ref, computed, onMounted } from 'vue';
import { getExpenseCategories, createBudget, updateMyBudget, deleteMyBudget } from '../api';
import type { ExpenseCategory, BudgetResponse } from '../models';

const props = defineProps<{
  existingBudget?: BudgetResponse | null;
  onClose: VoidFunction;
}>();
const emit = defineEmits<{
  saved: []
  deleted: []
}>();

const categories = ref<ExpenseCategory[]>([]);
const name = ref('');
const spendLimit = ref<number>(0);
const categoryLimits = ref<{ categoryId: number; spendLimit: number }[]>([]);
const isEdit = ref(false);

const categoryTotal = computed(() =>
  categoryLimits.value
    .filter(cl => cl.categoryId > 0 && cl.spendLimit > 0)
    .reduce((sum, cl) => sum + cl.spendLimit, 0)
);

const limitExceeded = computed(() =>
  categoryLimits.value.some(cl => cl.categoryId > 0) && categoryTotal.value > spendLimit.value
);

const closeModal = () => {
  props.onClose();
}

const addCategoryLimit = () => {
  categoryLimits.value.push({ categoryId: 0, spendLimit: 0 });
}

const removeCategoryLimit = (index: number) => {
  categoryLimits.value.splice(index, 1);
}

const submitBudget = async () => {
  if (limitExceeded.value) return;
  try {
    const payload = {
      name: name.value,
      spendLimit: spendLimit.value,
      categoryLimits: categoryLimits.value.filter(cl => cl.categoryId > 0 && cl.spendLimit > 0)
    };
    if (isEdit.value) {
      await updateMyBudget(payload);
    } else {
      await createBudget(payload);
    }
    emit('saved');
    closeModal();
  } catch (err) {
    console.error("Failed to save budget:", err);
  }
}

const handleDelete = async () => {
  if (confirm('Are you sure you want to delete this budget?')) {
    try {
      await deleteMyBudget();
      emit('deleted');
      closeModal();
    } catch (err) {
      console.error("Failed to delete budget:", err);
    }
  }
}

onMounted(async () => {
  try {
    categories.value = await getExpenseCategories();
  } catch (err) {
    console.error("Failed to load categories:", err);
  }
  // Pre-fill if editing
  if (props.existingBudget) {
    isEdit.value = true;
    name.value = props.existingBudget.name;
    spendLimit.value = props.existingBudget.spendLimit;
    categoryLimits.value = props.existingBudget.categories.map(c => ({
      categoryId: c.categoryId,
      spendLimit: c.spendLimit
    }));
  }
});
</script>

<template>
  <div class="modal-background">
    <div class="modal-content">
      <div class="d-flex justify-content-between align-items-center">
        <div class="modal-title">{{ isEdit ? 'Edit Budget' : 'Create Budget' }}</div>
        <button class="close-button" @click="closeModal"><X /></button>
      </div>
      <hr/>
      <div class="d-flex flex-column gap-3">
        <input type="text" class="form-control" placeholder="Budget Name" v-model="name" />
        <input type="number" class="form-control" placeholder="Total Spend Limit" v-model="spendLimit" />
        <hr/>
        <div class="d-flex justify-content-between align-items-center">
          <div class="section-label">Category Limits</div>
          <button class="btn btn-sm btn-outline-primary" @click="addCategoryLimit">+ Add Category</button>
        </div>
        <div v-for="(cl, index) in categoryLimits" :key="index" class="d-flex gap-2 align-items-center">
          <select class="form-select" v-model="cl.categoryId">
            <option value="0" disabled>Select Category</option>
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
          </select>
          <input type="number" class="form-control" placeholder="Spend Limit" v-model="cl.spendLimit" />
          <button class="btn btn-sm btn-outline-danger" @click="removeCategoryLimit(index)">✕</button>
        </div>
        <div v-if="categoryLimits.some(cl => cl.categoryId > 0)" class="d-flex justify-content-between text-muted small">
          <span>Total category limits: ₹{{ categoryTotal.toLocaleString() }}</span>
          <span>Budget limit: ₹{{ spendLimit.toLocaleString() }}</span>
        </div>
        <div v-if="limitExceeded" class="alert alert-danger d-flex align-items-center gap-2 p-2 m-0 small">
          <TriangleAlert :size="16"/>
          <span>Category limits total (₹{{ categoryTotal.toLocaleString() }}) exceeds the budget limit (₹{{ spendLimit.toLocaleString() }})</span>
          <button class="btn btn-sm btn-outline-danger ms-auto" @click="spendLimit = categoryTotal">Match limit</button>
        </div>
        <div class="d-flex gap-2 justify-content-between">
          <button v-if="isEdit" class="btn btn-outline-danger d-flex align-items-center gap-1" @click="handleDelete">
            <Trash2 :size="16"/> Delete Budget
          </button>
          <button class="btn btn-primary" @click="submitBudget" :disabled="!name || !spendLimit || limitExceeded">
            {{ isEdit ? 'Update Budget' : 'Create Budget' }}
          </button>
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
  width: 550px;
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
.section-label {
  font-weight: 600;
  color: #555;
}
</style>
