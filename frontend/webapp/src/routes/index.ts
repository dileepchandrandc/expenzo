import { createRouter, createWebHistory } from 'vue-router'

import DashboardPage from '../components/pages/DashboardPage.vue'
import ExpensePage from '../components/pages/ExpensePage.vue'
import ProfilePage from '../components/pages/ProfilePage.vue'
import PaymentChannelPage from '../components/pages/PaymentChannelPage.vue'
import BudgetPage from '../components/pages/BudgetPage.vue'

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
  },
  {
    path: '/payment-channel',
    name: 'Payment Channel',
    component: PaymentChannelPage
  },
  {
    path: '/budget',
    name: 'Budget',
    component: BudgetPage
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router