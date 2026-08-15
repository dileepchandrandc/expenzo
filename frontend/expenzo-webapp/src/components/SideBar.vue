<script setup lang="ts">
import { RouterLink } from 'vue-router';
import { sideBarItems } from '../utils/constants.ts';

</script>

<template>
    <div class="side-bar">
        <div class="logo-container">
            <div class="logo-circle">Ex</div>
            <span class="logo-text">Expenzo</span>
        </div>
        <nav class="menu">
            <RouterLink v-for="item in sideBarItems" :key="item.key" :to="item.to">
                <div class="side-bar-item">
                    <span><component :is="item.icon" :size="20" /></span>
                    <span>{{ item.text }}</span>
                </div>
            </RouterLink>
        </nav>
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
  color: #ffffff;
}
.menu {
  flex: 1;
  padding: 20px 5px;
}
.side-bar {
    padding: 10px;
    background-color: black;
}

.side-bar-item {
    color: white;
    padding: 5px 10px;
    margin-bottom: 5px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    gap: 10px;
}

.menu a.router-link-active .side-bar-item {
    background-color: rgb(79, 70, 229);
}

.menu a {
  text-decoration: none;
}
</style>
