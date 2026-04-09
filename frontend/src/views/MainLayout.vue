<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import Sidebar from '@/components/Sidebar.vue'

const LOGIN_TRANSITION_STATE_KEY = 'loginTransition'
const LOGIN_SHELL_ENTER_DURATION_MS = 640
const MOBILE_SHELL_BREAKPOINT_QUERY = '(max-width: 960px)'

const route = useRoute()
const playLoginShellEnter = ref(false)
const isMobileShell = ref(false)
const isSidebarOpen = ref(false)

let clearAnimationTimer: number | undefined
let shellMediaQuery: MediaQueryList | null = null

function closeSidebar() {
  isSidebarOpen.value = false
}

function toggleSidebar() {
  if (!isMobileShell.value) {
    return
  }

  isSidebarOpen.value = !isSidebarOpen.value
}

function syncShellMode(query: MediaQueryList | MediaQueryListEvent) {
  isMobileShell.value = query.matches

  if (!query.matches) {
    closeSidebar()
  }
}

function handleShellKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape') {
    closeSidebar()
  }
}

function hasLoginTransitionFlag() {
  const state = window.history.state as Record<string, unknown> | null

  return state?.[LOGIN_TRANSITION_STATE_KEY] === 'success'
}

function clearLoginTransitionFlag() {
  const state = window.history.state as Record<string, unknown> | null

  if (!state || state[LOGIN_TRANSITION_STATE_KEY] !== 'success') {
    return
  }

  const nextState = { ...state }

  delete nextState[LOGIN_TRANSITION_STATE_KEY]

  // Preserve vue-router's own history bookkeeping while dropping the one-shot animation flag.
  window.history.replaceState(nextState, '', window.location.href)
}

onMounted(() => {
  shellMediaQuery = window.matchMedia(MOBILE_SHELL_BREAKPOINT_QUERY)
  syncShellMode(shellMediaQuery)
  shellMediaQuery.addEventListener('change', syncShellMode)
  window.addEventListener('keydown', handleShellKeydown)

  if (!hasLoginTransitionFlag()) {
    return
  }

  clearLoginTransitionFlag()

  window.requestAnimationFrame(() => {
    playLoginShellEnter.value = true

    clearAnimationTimer = window.setTimeout(() => {
      playLoginShellEnter.value = false
    }, LOGIN_SHELL_ENTER_DURATION_MS)
  })
})

onBeforeUnmount(() => {
  if (clearAnimationTimer) {
    window.clearTimeout(clearAnimationTimer)
  }

  document.body.style.overflow = ''
  window.removeEventListener('keydown', handleShellKeydown)
  shellMediaQuery?.removeEventListener('change', syncShellMode)
})

watch(
  () => route.fullPath,
  () => {
    closeSidebar()
  },
)

watch([isMobileShell, isSidebarOpen], ([mobile, open]) => {
  document.body.style.overflow = mobile && open ? 'hidden' : ''
})
</script>

<template>
  <div class="screen-overlay" :class="{ 'screen-overlay-login-enter': playLoginShellEnter }">
    <NavBar
      :show-sidebar-toggle="isMobileShell"
      :sidebar-open="isSidebarOpen"
      @toggle-sidebar="toggleSidebar"
    />
    <button
      v-if="isMobileShell && isSidebarOpen"
      type="button"
      class="sidebar-backdrop"
      aria-label="Close navigation"
      @click="closeSidebar"
    ></button>
    <div class="dashboard-shell">
      <Sidebar
        :is-mobile="isMobileShell"
        :open="!isMobileShell || isSidebarOpen"
        @close="closeSidebar"
      />

      <main class="dashboard-main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.screen-overlay {
  min-height: 100vh;
  transform-origin: center top;
  isolation: isolate;
  will-change: transform, opacity, filter;
}

.screen-overlay-login-enter {
  animation: login-shell-enter 640ms cubic-bezier(0.16, 1, 0.3, 1) both;
}

.screen-overlay-login-enter::after {
  content: '';
  position: fixed;
  inset: 0;
  pointer-events: none;
  background:
    radial-gradient(circle at center, rgba(255, 255, 255, 0.7), transparent 34%),
    radial-gradient(circle at center, rgba(255, 255, 255, 0.18), transparent 52%);
  mix-blend-mode: screen;
  animation: login-shell-flare 360ms ease-out both;
}

.dashboard-shell {
  --navbar-height: 54px;
  --sidebar-width: max(15vw, 220px);
  min-height: 100vh;
  padding-top: var(--navbar-height);
}

.dashboard-main {
  margin-left: var(--sidebar-width);
  padding: 24px;
  min-width: 0;
}

.sidebar-backdrop {
  position: fixed;
  inset: var(--navbar-height, 54px) 0 0;
  z-index: 1040;
  border: 0;
  padding: 0;
  background: rgba(15, 23, 42, 0.38);
  backdrop-filter: blur(3px);
}

@keyframes login-shell-enter {
  from {
    opacity: 0.08;
    transform: scale(1.16);
    filter: blur(18px);
  }

  to {
    opacity: 1;
    transform: scale(1);
    filter: blur(0);
  }
}

@keyframes login-shell-flare {
  from {
    opacity: 0.95;
  }

  to {
    opacity: 0;
  }
}

@media (prefers-reduced-motion: reduce) {
  .screen-overlay-login-enter {
    animation-duration: 0.01ms !important;
  }
}

@media (max-width: 960px) {
  .dashboard-shell {
    --sidebar-width: 0px;
  }

  .dashboard-main {
    margin-left: 0;
    padding: 18px 16px 24px;
  }
}

@media (max-width: 640px) {
  .dashboard-main {
    padding: 14px 12px 20px;
  }
}
</style>
