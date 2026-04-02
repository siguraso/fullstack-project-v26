<script setup lang="ts">
import { computed, reactive } from 'vue'
import type { RouteLocationRaw } from 'vue-router'
import { useRoute, useRouter } from 'vue-router'
import {
  ChevronDown,
  ClipboardCheck,
  LayoutDashboard,
  SearchCheck,
  Settings,
  ScrollText,
  Thermometer,
  TriangleAlert,
} from '@lucide/vue'

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
      { label: 'Checklists', icon: ClipboardCheck, to: { path: '/checklists', query: { ik: 'food' } } },
      { label: 'Logs', icon: ScrollText, to: { path: '/logs', query: { ik: 'food' } } },
      { label: 'Temperature Logs', icon: Thermometer, to: '/temperature' },
    ],
  },
  {
    key: 'alcohol',
    label: 'IK-alcohol',
    items: [
      { label: 'Checklists', icon: ClipboardCheck, to: { path: '/checklists', query: { ik: 'alcohol' } } },
      { label: 'Logs', icon: ScrollText, to: { path: '/logs', query: { ik: 'alcohol' } } },
    ],
  },
]

const footerItems: NavItem[] = [{ label: 'Settings', icon: Settings, to: '/settings' }]

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
</script>

<template>
  <aside class="sidebar">
    <div class="brand-block">
      <span class="brand-kicker">Compliance workspace</span>
      <h2>Regula</h2>
      <p class="brand-meta">storename</p>
    </div>

    <nav class="nav-shell" aria-label="Sidebar">
      <div class="nav-main">
        <section class="nav-section">
          <p class="nav-section-label">Overview</p>

          <ul class="nav-list">
            <li v-for="item in primaryItems" :key="item.label">
              <button
                type="button"
                class="nav-button"
                :class="{ 'nav-button-active': isActive(item.to) }"
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

        <section class="nav-section">
          <p class="nav-section-label">Compliance</p>

          <section v-for="group in complianceGroups" :key="group.key" class="nav-group">
            <button
              type="button"
              class="group-trigger"
              :class="{ 'group-trigger-active': groupIsActive(group) }"
              @click="toggleGroup(group.key)"
            >
              <span>{{ group.label }}</span>
              <ChevronDown
                :size="16"
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
                    <component :is="item.icon" :size="16" aria-hidden="true" />
                    <span>{{ item.label }}</span>
                  </span>
                </button>
              </li>
            </ul>
          </section>
        </section>
      </div>

      <section class="nav-section nav-section-bottom">
        <ul class="nav-list">
          <li v-for="item in footerItems" :key="item.label">
            <button
              type="button"
              class="nav-button"
              :class="{ 'nav-button-active': isActive(item.to) }"
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
  height: calc(100vh - var(--navbar-height, 64px));
  position: fixed;
  top: var(--navbar-height, 64px);
  left: 0;
  padding: 20px 14px 16px;
  box-sizing: border-box;
  background: linear-gradient(180deg, #ffffff 0%, #fbfbfc 100%);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
}

.brand-block {
  margin-bottom: 22px;
  padding: 6px 10px 0;
}

.brand-kicker {
  display: inline-block;
  margin-bottom: 8px;
  color: var(--text-muted);
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.brand-block h2 {
  margin: 0;
  font-size: 26px;
  line-height: 1.1;
  letter-spacing: -0.03em;
}

.brand-meta {
  margin: 6px 0 0;
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
}

.nav-shell {
  display: flex;
  flex-direction: column;
  gap: 18px;
  flex: 1;
  min-height: 0;
}

.nav-main {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.nav-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-section-bottom {
  margin-top: auto;
  padding-top: 14px;
  border-top: 1px solid var(--border);
}

.nav-section-label {
  margin: 0;
  padding: 0 10px;
  color: var(--text-muted);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.nav-list,
.subnav-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-button,
.subnav-button,
.group-trigger,
.logout-button {
  width: 100%;
  border: 0;
  background: transparent;
  color: var(--text-secondary);
  border-radius: 10px;
  transition:
    background-color 180ms ease,
    color 180ms ease,
    border-color 180ms ease,
    box-shadow 180ms ease,
    transform 120ms ease;
}

.nav-button,
.group-trigger,
.logout-button {
  min-height: 42px;
  padding: 0 12px;
  border: 1px solid transparent;
}

.subnav-button {
  min-height: 36px;
  padding: 0 12px 0 14px;
  border: 1px solid transparent;
}

.nav-button-content,
.group-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  font-weight: 500;
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
  background: rgba(26, 28, 30, 0.045);
  color: var(--text);
}

.nav-button-active,
.subnav-button-active {
  background: #f4f4f5;
  color: var(--text);
  border-color: #ececef;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.9);
}

.group-trigger-active {
  color: var(--text);
  background: rgba(26, 28, 30, 0.03);
}

.group-chevron {
  flex-shrink: 0;
  transition: transform 180ms ease;
}

.group-chevron-collapsed {
  transform: rotate(-90deg);
}

.subnav-list {
  margin: 0 0 4px 12px;
  padding-left: 10px;
  border-left: 1px solid var(--border);
}

.user-info {
  margin-top: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 18px 10px 0;
  border-top: 1px solid var(--border);
}

.user-info p {
  margin: 0;
  text-align: left;
  color: var(--text-secondary);
  font-size: 12px;
}

.logout-button {
  justify-content: center;
  background: #f7f7f8;
  color: var(--text);
  font-size: 13px;
  font-weight: 600;
  border-color: #ececef;
  box-shadow: none;
}

.logout-button:active,
.nav-button:active,
.subnav-button:active,
.group-trigger:active {
  transform: scale(0.99);
}
</style>
