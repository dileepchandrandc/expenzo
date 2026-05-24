<script setup lang="ts">
import { computed } from 'vue';
import { hexToRgba } from '../utils';

const props = defineProps({
  minBarWidth: Number,
  progress: Number,
  color: String
});

const barWidth = computed(
  () => `${props.minBarWidth}px`
);

const progressWidth = computed(() => {
  if (props.progress == undefined) return '0px';
  if (props.progress > 100) {
    return '100%'
  }
  return `${props.progress}%`
});

const bgColor = computed(() => {
  return hexToRgba(props.color??'', 0.1);
})
const progressColor = computed(() => {
  return hexToRgba(props.color??'');
})
</script>

<template>
  <div class="usage-bar">
    <div class="inner-bar"></div>
  </div>
</template>

<style scoped>
.usage-bar {
  width: v-bind(barWidth);
  background-color: v-bind(bgColor);
  height: 5px;
}

.inner-bar {
  width: v-bind(progressWidth);
  background-color: v-bind(progressColor);
  height: 5px;
}
</style>