<script setup lang="ts">
import { computed, reactive } from 'vue'
import type { RouteLocationRaw } from 'vue-router'
import { useRoute, useRouter } from 'vue-router'
import {
  ChevronDown,
  X,
  ClipboardCheck,
  FolderOpen,
  LayoutDashboard,
  SearchCheck,
  Settings,
  ScrollText,
  Thermometer,
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

const props = withDefaults(
  defineProps<{
    isMobile?: boolean
    open?: boolean
  }>(),
  {
    isMobile: false,
    open: true,
  },
)

const emit = defineEmits<{
  close: []
}>()

const primaryItems: NavItem[] = [
  { label: 'Dashboard', icon: LayoutDashboard, to: '/dashboard' },
  { label: 'Documents', icon: FolderOpen, to: '/documents' },
  { label: 'Deviations', icon: TriangleAlert, to: '/deviation' },
  { label: 'Inspections', icon: SearchCheck, to: '/inspections' },
  { label: 'Checklists', icon: ClipboardCheck, to: '/checklists' },
]

const complianceGroups: NavGroup[] = [
  {
    key: 'food',
    label: 'IK-food',
    items: [{ label: 'Temperature Logs', icon: Thermometer, to: '/temperature-logs' }],
  },
  {
    key: 'alcohol',
    label: 'IK-alcohol',
    items: [{ label: 'Alcohol Logs', icon: ScrollText, to: '/alcohol-logs' }],
  },
]

const router = useRouter()
const route = useRoute()
const session = getAuthSession()
const canAccessSettings = session?.role === 'ADMIN' || session?.role === 'MANAGER'

const footerItems: NavItem[] = canAccessSettings
  ? [{ label: 'Settings', icon: Settings, to: '/settings' }]
  : []

const userEmail = computed(() => session?.email ?? 'Signed in user')
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
  void router.push(destination)

  if (props.isMobile) {
    emit('close')
  }
}

function toggleGroup(key: NavGroup['key']) {
  openGroups[key] = !openGroups[key]
}

async function logout() {
  clearAuthSession()
  if (props.isMobile) {
    emit('close')
  }
  await router.push('/login')
}
</script>

<template>
  <aside
    class="sidebar"
    :class="{
      'sidebar-mobile': props.isMobile,
      'sidebar-open': props.open,
      'sidebar-closed': props.isMobile && !props.open,
    }"
    :aria-hidden="props.isMobile ? !props.open : undefined"
  >
    <div v-if="props.isMobile" class="mobile-header">
      <div class="mobile-brand">
        <span class="mobile-brand-title">Navigation</span>
        <p>{{ userEmail }}</p>
      </div>
      <button
        type="button"
        class="mobile-close"
        aria-label="Close navigation"
        @click="emit('close')"
      >
        <X :size="18" aria-hidden="true" />
      </button>
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
      <p>{{ userEmail }}</p>
      <button type="button" class="logout-button" @click="logout">Logout</button>
    </div>
  </aside>
</template>

<style scoped>
.sidebar {
  width: 100%;
  min-width: 0;
  max-width: var(--sidebar-width, 220px);
  height: calc(100dvh - var(--navbar-height, 64px));
  position: sticky;
  top: var(--navbar-height, 64px);
  left: 0;
  padding: 20px 14px 16px;
  box-sizing: border-box;
  background: linear-gradient(180deg, #ffffff 0%, #fbfbfc 100%);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  align-self: start;
  z-index: 100;
}

.brand-block {
  margin-bottom: 22px;
  padding: 6px 10px 0;
}

.brand-kicker {
  display: inline-block;
  margin-bottom: 8px;
  color: var(--text-secondary);
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

.mobile-header {
  display: none;
}

.nav-shell {
  display: flex;
  flex-direction: column;
  gap: 18px;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
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
  color: var(--text-secondary);
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

@media (max-width: 960px) {
  .sidebar-mobile {
    position: fixed;
    top: var(--navbar-height, 54px);
    left: 0;
    bottom: 0;
    width: min(86vw, 320px);
    min-width: 0;
    max-width: none;
    height: auto;
    padding: 16px 14px 18px;
    box-shadow: 0 18px 42px rgba(15, 23, 42, 0.18);
    transform: translateX(-110%);
    transition: transform 220ms ease;
    z-index: 1100;
  }

  .sidebar-mobile.sidebar-open {
    transform: translateX(0);
  }

  .sidebar-mobile.sidebar-closed {
    pointer-events: none;
  }

  .sidebar-mobile .mobile-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 12px;
    padding: 2px 4px 14px;
    margin-bottom: 8px;
    border-bottom: 1px solid var(--border);
  }

  .mobile-brand {
    min-width: 0;
  }

  .mobile-brand-title {
    display: block;
    margin-bottom: 4px;
    font-size: 11px;
    font-weight: 700;
    letter-spacing: 0.08em;
    text-transform: uppercase;
    color: var(--text-secondary);
  }

  .mobile-brand p {
    margin: 0;
    color: var(--text-secondary);
    font-size: 12px;
    overflow-wrap: anywhere;
  }

  .mobile-close {
    min-height: 36px;
    min-width: 36px;
    padding: 0;
    border-radius: 10px;
    border: 1px solid var(--border);
    background: var(--bg-secondary);
    color: var(--text);
    flex-shrink: 0;
  }
}
</style>
