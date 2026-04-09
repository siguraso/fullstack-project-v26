import { createRouter, createWebHistory } from 'vue-router'
import { getAuthSession, isAuthenticated } from '../services/auth'
import type { UserRole } from '../services/auth'
import DashboardView from '../views/dashboard/DashboardView.vue'
import LoginView from '../views/LoginView.vue'
import InviteAcceptView from '../views/InviteAcceptView.vue'
import DeviationView from '../views/deviation/DeviationView.vue'
import InspectionsView from '@/views/inspections/InspectionsView.vue'
import ChecklistView from '@/views/checklist/ChecklistView.vue'
import MainLayout from '@/views/MainLayout.vue'
import SettingsView from '@/views/settings/SettingsView.vue'
import ChecklistBuilderView from '@/views/checklist/ChecklistBuilderView.vue'
import TemperatureLogView from '@/views/temperature-logs/TemperatureLogView.vue'
import AlcoholLogView from '@/views/alcohol-logs/AlcoholLogView.vue'
import DocumentsView from '@/views/documents/DocumentsView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
    },
    {
      path: '/invite/accept',
      name: 'invite-accept',
      component: InviteAcceptView,
    },
    {
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
          path: '/checklists',
          name: 'checklist',
          component: ChecklistView,
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
          meta: { allowedRoles: ['ADMIN', 'MANAGER'] },
        },
        {
          path: '/documents',
          name: 'documents',
          component: DocumentsView,
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
        {
          path: '/alcohol-logs',
          name: 'alcohol-logs',
          component: AlcoholLogView,
        },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const authenticated = isAuthenticated()
  const session = getAuthSession()

  if (to.meta.requiresAuth && !authenticated) {
    return { name: 'login' }
  }

  const allowedRoles = to.meta.allowedRoles as UserRole[] | undefined
  if (allowedRoles && (!session?.role || !allowedRoles.includes(session.role))) {
    return { name: 'dashboard' }
  }

  if ((to.name === 'login' || to.name === 'register') && authenticated) {
    return { name: 'dashboard' }
  }

  return true
})

export default router
