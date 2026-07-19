<script setup lang="ts">
</script>

<template>
  <div class="logo-container">
    <div class="logo-circle">Ex</div>
    <span class="logo-text">Expenzo</span>
  </div>
</template>

<style scoped>
.logo-container {
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: center;
}

.logo-circle {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background-color: rgb(79, 70, 229);
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
  border: 2px solid rgb(79, 70, 229);
  flex-shrink: 0;
}

.logo-text {
  font-size: 24px;
  font-weight: 700;
  color: #000000;
}
</style>
