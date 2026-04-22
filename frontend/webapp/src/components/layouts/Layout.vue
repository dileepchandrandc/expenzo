<script setup>
import { Bell, User, LayoutDashboard, BanknoteArrowDown, PiggyBank} from 'lucide-vue-next'
import { ref } from 'vue'
const page = ref("dashboard")
</script>

<template>
  <div>
    <header class="layout-header d-flex justify-content-between align-items-center">
        <div class="d-flex flex-row align-items-center gap-1">
            <div class="app-icon-container"><PiggyBank size="30"/></div>
            <div class="app-title">Expenzo</div>
        </div>
        <div class="d-flex gap-2">
            <button class="action-button"><Bell/></button>
            <button class="action-button"><User/></button>
        </div>
    </header>
    <main>
        <div class="d-flex">
            <div class="side-bar d-flex flex-column gap-2">
                <router-link to="/" class="side-bar-button" :class="{ active: page === 'dashboard' }" @click="page = 'dashboard'">
                    <div class="d-flex gap-2 align-items-center">
                        <div><LayoutDashboard size="20"/></div>
                        <div>Dashboard</div>
                    </div>
                </router-link>
                <router-link to="/expense" class="side-bar-button" :class="{ active: page === 'expense' }" @click="page = 'expense'">
                    <div class="d-flex gap-2 align-items-center">
                        <div><BanknoteArrowDown size="20"/></div>
                        <div>Expense</div>
                    </div>
                </router-link>
                <router-link to="/profile" class="side-bar-button" :class="{ active: page === 'profile' }" @click="page = 'profile'">
                    <div class="d-flex gap-2 align-items-center">
                        <div><User size="20"/></div>
                        <div>Profile</div>
                    </div>
                </router-link>
            </div>
            <div class="container-fluid p-0" style="height: 100ex; overflow-y: auto;background-color: #f8f9fa;">
                <router-view/>
            </div>
        </div>
    </main>
  </div>
</template>

<style scoped>
.layout-header{
    background-color: #f8f9fa;
    padding: 1rem;
    border-bottom: 1px solid #dee2e6;
}

.app-title{
    font-size: 1.5rem;
    font-weight: normal;
    color: rgb(91, 120, 205);
    font-family: 'Poppins', sans-serif;
}

.app-icon-container{
    background-color: rgb(183, 183, 183, 0.2);
    padding: 0.25rem;
    border-radius: 25%;
    color: rgb(0, 0, 0);
}

.action-button{
    background: none;
    border: none;
    cursor: pointer;
}

.side-bar{
    width: 250px;
    background-color: #f8f9fa;
    padding: 1rem;
    border-right: 1px solid #dee2e6;
    height: 100ex;
}

.side-bar-button {
    background: none;
    border: none;
    cursor: pointer;
    padding-block: 0.5rem;
    text-align: left;
    font-weight: normal;
    text-decoration: none;
    color: rgb(91, 91, 91);
    font-size:small;
}

.side-bar-button.active {
    background-color: rgba(98, 98, 98, 0.1);
    border-radius: 10px;
    color: #000;
}
</style>