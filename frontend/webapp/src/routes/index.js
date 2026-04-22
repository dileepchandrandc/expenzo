import { createRouter, createWebHistory } from 'vue-router'

import DashboardPage from '../components/pages/DashboardPage.vue'
import ExpensePage from '../components/pages/ExpensePage.vue'
import ProfilePage from '../components/pages/ProfilePage.vue'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: DashboardPage
  },
  {
    path: '/expense',
    name: 'Expense',
    component: ExpensePage
  },
  {
    path: '/profile',
    name: 'Profile',
    component: ProfilePage
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router