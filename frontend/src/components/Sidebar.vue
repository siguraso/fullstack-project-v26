<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  LayoutDashboard,
  ClipboardCheck,
  ListTodo,
  ScrollText,
  SearchCheck,
  TriangleAlert,
  UserRound,
} from '@lucide/vue'
import { clearAuthSession, getAuthSession } from '@/services/auth'

const menuItems: {
  label: string
  icon: any
  route?: string
}[] = [
  { label: 'Dashboard', icon: LayoutDashboard, route: '/dashboard' },
  { label: 'Users', icon: UserRound, route: '/user' },
  { label: 'Checklist', icon: ClipboardCheck, route: '/checklists' },
  { label: 'Deviations', icon: TriangleAlert, route: '/deviation' },
  { label: 'Tasks', icon: ListTodo, route: '/tasks' },
  { label: 'Logs', icon: ScrollText, route: '/logs' },
  { label: 'Inspections', icon: SearchCheck, route: '/inspections' },
]

const router = useRouter()
const route = useRoute()
const session = getAuthSession()
const userLabel = computed(() => session?.email ?? 'Signed in user')

const activeIndex = computed(() => menuItems.findIndex((item) => item.route === route.path))

function navigate(item: any) {
  if (item.route) {
    router.push(item.route)
  }
}

async function logout() {
  clearAuthSession()
  await router.push('/login')
}
</script>

<template>
  <div class="sidebar">
    <h2>Regula</h2>
    <h3>storename</h3>
    <ul class="menu">
      <li v-for="(item, index) in menuItems" :key="item.label + index">
        <button
          :class="index === activeIndex ? 'menu-button' : 'menu-button-inactive'"
          @click="navigate(item)"
        >
          <!--Set to router link?-->
          <span class="menu-button-content">
            <component :is="item.icon" :size="16" aria-hidden="true" />
            {{ item.label }}
          </span>
        </button>
      </li>
    </ul>
    <div class="user-info">
      <p>{{ userLabel }}</p>
      <button type="button" @click="logout">Logout</button>
    </div>
  </div>
</template>

<style scoped>
.sidebar {
  width: var(--sidebar-width, 220px);
  min-width: var(--sidebar-width, 220px);
  height: 100vh;
  position: fixed;
  top: 0;
  left: 0;
  background-color: var(--bg);
  border-right: 1px solid var(--stroke);
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 0;
}

.sidebar ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.sidebar h2 {
  font-size: 24px;
  margin-bottom: 0px;
}

.sidebar h3 {
  font-size: 14px;
  margin-top: 10px;
  margin-bottom: 30px;
}

.menu-button {
  background-color: var(--neutral);
  color: var(--bg);
  border: none;
  font-size: 14px;
}

.menu-button-inactive {
  background-color: transparent;
  color: var(--neutral);
  border: none;
  outline: none;
  font-size: 14px;
}

.menu-button-inactive:hover {
  background-color: var(--stroke);
}

.menu-button,
.menu-button-inactive {
  width: 210px;
  box-sizing: border-box;
  height: 30px;
  margin-bottom: 10px;
  border-radius: 10px;
  text-align: left;
  padding-left: 12px;
  font-weight: bold;
  transition:
    background-color 220ms ease,
    color 220ms ease,
    border-color 220ms ease,
    transform 120ms ease;
}

.menu-button-inactive:disabled {
  cursor: default;
  opacity: 0.75;
}

.menu-button-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.menu-button-inactive:active {
  transform: scale(0.98);
}

.user-info {
  margin-top: auto;
  padding: 20px;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
}

.user-info p {
  margin: 0;
}

.user-info button {
  margin-bottom: 0;
  background-color: var(--neutral);
  color: var(--bg);
  border: none;
  height: 30px;
  border-radius: 8px;
}
</style>
