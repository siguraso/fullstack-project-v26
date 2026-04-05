import { createRouter, createWebHistory } from 'vue-router'
import { isAuthenticated } from '../services/auth'
import DashboardView from '../views/dashboard/DashboardView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import DeviationView from '../views/deviation/DeviationView.vue'
import TemperatureView from '../views/temperature/TemperatureView.vue'
import TasksView from '@/views/tasks/TasksView.vue'
import LogsView from '@/views/logs/LogsView.vue'
import InspectionsView from '@/views/inspections/InspectionsView.vue'
import ChecklistView from '@/views/checklist/ChecklistView.vue'
import MainLayout from '@/views/MainLayout.vue'
import SettingsView from '@/views/settings/SettingsView.vue'
import ChecklistBuilderView from '@/views/checklist/ChecklistBuilderView.vue'
import TemperatureLogView from '@/views/temperature-logs/TemperatureLogView.vue'

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
      path: '/',
      path: '/',
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
          path: '/temperature',
          name: 'temperature',
          component: TemperatureView,
        },
        {
          path: '/temperature',
          name: 'temperature',
          component: TemperatureView,
        },
        {
          path: '/checklists',
          name: 'checklist',
          component: ChecklistView,
        },
        {
          path: '/tasks',
          name: 'tasks',
          component: TasksView,
        },
        {
          path: '/logs',
          name: 'logs',
          component: LogsView,
        },
        {
          path: '/inspections',
          name: 'inspections',
          component: InspectionsView,
        },
        {
          path: '/settings',
          name: 'settings',
          component: SettingsView,
        },
        {
          path: '/checklist-builder',
          component: ChecklistBuilderView,
        },
        {
          path: '/temperature-logs',
          name: 'temperature-logs',
          component: TemperatureLogView,
        },
      ],
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
