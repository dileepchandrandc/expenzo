import { createRouter, createWebHistory } from 'vue-router';
import LoginView from '../views/LoginView.vue';
import SignupView from '../views/SignupView.vue';
import AppLayout from '../layouts/AppLayout.vue';
import DashboardView from '../views/DashboardView.vue';
import ProfileView from '../views/ProfileView.vue';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login',
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/signup',
      name: 'signup',
      component: SignupView,
    },
    {
      path: '/',
      name: '',
      component: AppLayout,
      children: [
        {
          path: "/dashboard",
          name: 'dashboard',
          component: DashboardView
        },
        {
          path: "/profile",
          name: 'profile',
          component: ProfileView
        }
      ]
    }
  ],
});

export default router;
