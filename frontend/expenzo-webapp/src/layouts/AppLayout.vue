
<script lang="ts" setup>
import SideBar from '../components/SideBar.vue';
import { RouterView } from 'vue-router';

</script>

<template>
    <div class="app-layout">
        <SideBar/>
        <main class="content">
            <RouterView/>
        </main>
    </div>
</template>

<style scoped>
.app-layout {
    display: grid;
    grid-template-columns: 240px 1fr;
    min-height: 100vh;
    background-color: rgb(245, 244, 239);
    width: 100%;
}

.content {
    padding: 10px;
    overflow-x: auto;
}
</style>