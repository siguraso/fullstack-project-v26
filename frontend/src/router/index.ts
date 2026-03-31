import { createRouter, createWebHistory } from 'vue-router'
import { isAuthenticated } from '../services/auth'
import DashboardView from '../views/dashboard/DashboardView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DeviationView from "../views/deviation/DeviationView.vue";
import ChecklistView from '@/views/checklist/ChecklistView.vue';
import MainLayout from '@/views/MainLayout.vue';
import UserPage from '@/views/user/UserPage.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: "/",
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        {
          path: '/dashboard',
          name: 'dashboard',
          component: DashboardView,
        },
        {
          path: '/deviation',
          name: 'deviation',
          component: DeviationView,
        },
        {
          path: '/checklists',
          name: 'checklist',
          component: ChecklistView,
        },
        {
          path: '/user',
          name: 'user',
          component: UserPage,
        },
      ]
    },
  ],
})

router.beforeEach((to) => {
  const authenticated = isAuthenticated()

  if (to.meta.requiresAuth && !authenticated) {
    return { name: 'login' }
  }

  if ((to.name === 'login' || to.name === 'register') && authenticated) {
    return { name: 'dashboard' }
  }
  
  return true
})

export default router
