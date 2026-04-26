
<script setup lang="ts">
import type { OverViewCardProps } from './props';

const overview = defineProps<OverViewCardProps>()
</script>

<template>
    <div class="overview-card d-flex flex-column gap-3">
        <div class="overview-title">{{ overview.title }}</div>
        <div class="d-flex flex-column">
            <div class="overview-value">₹{{ overview.amount }}</div>
            <div class="overview-bottom-text">{{ overview.bottomText }}</div>
        </div>
    </div>
</template>

<style scoped>
.overview-card{
    padding: 0.5rem;
    border: 1px solid #ccc;
    border-radius: 10px;
    width: 100%;
    background-color: white;
}

.overview-title{
    font-size: 0.8rem;
    color: #666;
}
.overview-value{
    font-size: 1.5rem;
    font-weight: 500;
}

.overview-bottom-text{
    font-size: 0.6rem;
    color: #999;
}
</style>
