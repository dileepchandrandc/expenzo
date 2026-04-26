<script setup lang="ts">
import { computed } from 'vue';
import type { CleanHorizndalProps } from '../props';
const prop = withDefaults(defineProps<CleanHorizndalProps>(), {
    color: 'rgb(90, 175, 255)',
    bgColor: 'rgba(190, 190, 190, 0.5)',
    height: 15
})
const width = computed(() => {
    if (!prop.maxValue) return '0%';
    const percent = (prop.value / prop.maxValue) * 100;
    return Math.min(percent, 100) + '%';
});
</script>
<template>
    <div class="parent-bar">
        <div class="child-bar"></div>
    </div>
</template>
<style scoped>
.parent-bar {
    height: v-bind(height + 'px');
    background-color: v-bind(bgColor);
    width: 100%;
}
.child-bar {
    height: v-bind(height + 'px');
    background-color: v-bind(color);
    width: v-bind(width);
}
</style>