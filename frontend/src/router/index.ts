import { createRouter, createWebHistory } from 'vue-router'
import { isAuthenticated } from '../services/auth'
import DashboardView from '../views/dashboard/DashboardView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import UserView from '../views/UserView.vue'
import DeviationView from "../views/deviation/DeviationView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
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
      path: '/register',
      name: 'register',
      component: RegisterView,
    },
    {
      path: '/dashboard',
      name: 'dashboard',
      component: DashboardView,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: '/user',
      name: 'user',
      component: UserView,
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: '/deviation',
      name: 'deviation',
      component: DeviationView,
      meta: {
        requiresAuth: true,
      },
    }
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

  if (to.name === 'deviation' && authenticated) {
    return { name: 'deviation' }
  }

  return true
})

export default router
