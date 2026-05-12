<script setup lang="ts">
import type { CleanModalProps } from '../props';
import { X } from 'lucide-vue-next';

const props: CleanModalProps = withDefaults(defineProps<CleanModalProps>(), {
    minWidth: 500
});
</script>
<template>
    <div class="clean-modal-background">
        <div class="clean-modal-content">
            <div class="clean-modal-header">
                <div v-if="props.title != undefined" class="modal-title">{{ props.title }}</div>
                <slot name="header" v-if="props.title == undefined"></slot>
                <button class="close-button" @click="props.onClose"><X /></button>
            </div>
            <hr class="clean-section-divider">
            <div class="clean-modal-body">
                <slot></slot>
            </div>
        </div>
    </div>
</template>
<style scoped>
.clean-section-divider {
  border: none;
  border-top: 1px solid #737373;
  margin: 1rem 0;
}

.clean-modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.close-button{
    background: none;
    border: none;
    cursor: pointer;
}
.clean-modal-background {
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

.clean-modal-content{
    background-color: white;
    border-radius: 10px;
    width: v-bind(minWidth + 'px');
    padding-top: 1rem;
    padding-bottom: 1rem;
}

.clean-modal-header {
    padding-left: 1rem;
    padding-right: 1rem;
}

.clean-modal-body {
    padding-left: 1rem;
    padding-right: 1rem;
}
</style>