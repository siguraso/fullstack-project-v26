<script setup lang="ts">
import { computed, reactive } from 'vue'
import type { RouteLocationRaw } from 'vue-router'
import { useRoute, useRouter } from 'vue-router'
import {
  ChevronDown,
  ClipboardCheck,
  LayoutDashboard,
  SearchCheck,
  ScrollText,
  TriangleAlert,
} from '@lucide/vue'
import { clearAuthSession, getAuthSession } from '@/services/auth'

interface NavItem {
  label: string
  icon: any
  to: RouteLocationRaw
}

interface NavGroup {
  key: 'food' | 'alcohol'
  label: string
  items: NavItem[]
}

const primaryItems: NavItem[] = [
  { label: 'Dashboard', icon: LayoutDashboard, to: '/dashboard' },
  { label: 'Deviations', icon: TriangleAlert, to: '/deviation' },
  { label: 'Inspections', icon: SearchCheck, to: '/inspections' },
]

const complianceGroups: NavGroup[] = [
  {
    key: 'food',
    label: 'IK-food',
    items: [
      {
        label: 'Checklists',
        icon: ClipboardCheck,
        to: { path: '/checklists', query: { ik: 'food' } },
      },
      { label: 'Logs', icon: ScrollText, to: { path: '/logs', query: { ik: 'food' } } },
    ],
  },
  {
    key: 'alcohol',
    label: 'IK-alcohol',
    items: [
      {
        label: 'Checklists',
        icon: ClipboardCheck,
        to: { path: '/checklists', query: { ik: 'alcohol' } },
      },
      { label: 'Logs', icon: ScrollText, to: { path: '/logs', query: { ik: 'alcohol' } } },
    ],
  },
]

const router = useRouter()
const route = useRoute()
const session = getAuthSession()
const userLabel = computed(() => session?.email ?? 'Signed in user')
const openGroups = reactive<Record<NavGroup['key'], boolean>>({
  food: true,
  alcohol: true,
})

function isActive(destination: RouteLocationRaw) {
  return router.resolve(destination).fullPath === route.fullPath
}

function groupIsActive(group: NavGroup) {
  return group.items.some((item) => isActive(item.to))
}

function navigate(destination: RouteLocationRaw) {
  router.push(destination)
}

function toggleGroup(key: NavGroup['key']) {
  openGroups[key] = !openGroups[key]
}

async function logout() {
  clearAuthSession()
  await router.push('/login')
}
</script>

<template>
  <aside class="sidebar">
    <div class="brand-block">
      <h2>Regula</h2>
      <h3>storename</h3>
    </div>

    <nav class="nav-shell" aria-label="Sidebar">
      <ul class="nav-list">
        <li v-for="item in primaryItems" :key="item.label">
          <button
            type="button"
            class="nav-button"
            :class="{ 'nav-button-active': isActive(item.to) }"
            @click="navigate(item.to)"
          >
            <span class="nav-button-content">
              <component :is="item.icon" :size="20" aria-hidden="true" />
              <span>{{ item.label }}</span>
            </span>
          </button>
        </li>
      </ul>

      <section v-for="group in complianceGroups" :key="group.key" class="nav-group">
        <button
          type="button"
          class="group-trigger"
          :class="{ 'group-trigger-active': groupIsActive(group) }"
          @click="toggleGroup(group.key)"
        >
          <span>{{ group.label }}</span>
          <ChevronDown
            :size="18"
            aria-hidden="true"
            class="group-chevron"
            :class="{ 'group-chevron-collapsed': !openGroups[group.key] }"
          />
        </button>

        <ul v-if="openGroups[group.key]" class="subnav-list">
          <li v-for="item in group.items" :key="group.key + item.label">
            <button
              type="button"
              class="subnav-button"
              :class="{ 'subnav-button-active': isActive(item.to) }"
              @click="navigate(item.to)"
            >
              <span class="nav-button-content">
                <component :is="item.icon" :size="18" aria-hidden="true" />
                <span>{{ item.label }}</span>
              </span>
            </button>
          </li>
        </ul>
      </section>
    </nav>

    <div class="user-info">
      <p>{{ userLabel }}</p>
      <button type="button" class="logout-button" @click="logout">Logout</button>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  width: var(--sidebar-width, 220px);
  min-width: var(--sidebar-width, 220px);
  height: 100vh;
  position: fixed;
  top: 0;
  left: 0;
  padding: 26px 18px 20px;
  box-sizing: border-box;
  background: linear-gradient(180deg, #f7f7f7 0%, #f1f2f3 100%);
  border-right: 1px solid rgba(26, 28, 30, 0.08);
  display: flex;
  flex-direction: column;
}

.brand-block {
  text-align: center;
  margin-bottom: 28px;
}

.brand-block h2 {
  margin: 0;
  font-size: 30px;
  line-height: 1.1;
}

.brand-block h3 {
  margin: 10px 0 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-secondary);
}

.nav-shell {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.nav-list,
.subnav-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-button,
.subnav-button,
.group-trigger,
.logout-button {
  width: 100%;
  border: 0;
  background: transparent;
  color: #5f6b7c;
  border-radius: 18px;
  transition:
    background-color 180ms ease,
    color 180ms ease,
    box-shadow 180ms ease,
    transform 120ms ease;
}

.nav-button,
.group-trigger,
.logout-button {
  min-height: 58px;
  padding: 0 18px;
}

.subnav-button {
  min-height: 50px;
  padding: 0 18px 0 32px;
}

.nav-button-content,
.group-trigger {
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 15px;
  font-weight: 600;
}

.nav-button-content {
  justify-content: flex-start;
}

.group-trigger {
  justify-content: space-between;
}

.nav-button:hover,
.subnav-button:hover,
.group-trigger:hover,
.logout-button:hover {
  background: rgba(255, 255, 255, 0.7);
  color: var(--neutral);
}

.nav-button-active,
.subnav-button-active {
  background: #ffffff;
  color: #0f172a;
  box-shadow: 0 12px 24px rgba(15, 23, 42, 0.08);
}

.group-trigger-active {
  color: #253247;
}

.group-chevron {
  flex-shrink: 0;
  transition: transform 180ms ease;
}

.group-chevron-collapsed {
  transform: rotate(-90deg);
}

.subnav-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 6px;
}

.user-info {
  margin-top: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 20px;
}

.user-info p {
  margin: 0;
  text-align: center;
  color: var(--text-secondary);
  font-size: 13px;
}

.logout-button {
  background: #ffffff;
  color: var(--neutral);
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.05);
}

.logout-button:active,
.nav-button:active,
.subnav-button:active,
.group-trigger:active {
  transform: scale(0.99);
}
</style>
