<script setup lang="ts">
import { computed } from 'vue'
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
  // placeholder for real sidebar
  { label: 'Temperature Logs', icon: SearchCheck, route: '/temperature-logs' },
]

const router = useRouter()
const route = useRoute()

const activeIndex = computed(() => menuItems.findIndex((item) => item.route === route.path))

function navigate(item: any) {
  if (item.route) {
    router.push(item.route)
  }
}
</script>

<template>
  <div class="sidebar">
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
  </div>
</template>

<style scoped>
.sidebar {
  width: var(--sidebar-width, 220px);
  min-width: var(--sidebar-width, 220px);
  height: calc(100vh - var(--navbar-height, 64px));
  position: fixed;
  top: var(--navbar-height, 64px);
  left: 0;
  background-color: var(--bg);
  border-right: 1px solid var(--stroke);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 0px 0px 0px;
}

.sidebar ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
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
</style>
