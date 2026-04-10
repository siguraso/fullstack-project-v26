<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import Sidebar from '@/components/Sidebar.vue'
import { createLogger } from '@/services/util/logger'

const LOGIN_TRANSITION_STATE_KEY = 'loginTransition'
const LOGIN_SHELL_ENTER_DURATION_MS = 640
const MOBILE_SHELL_BREAKPOINT_PX = 960

const route = useRoute()
const playLoginShellEnter = ref(false)
const isMobileShell = ref(false)
const isSidebarOpen = ref(false)
const logger = createLogger('main-layout')

let clearAnimationTimer: number | undefined
let handleWindowResize: (() => void) | null = null

function closeSidebar() {
  if (!isSidebarOpen.value) {
    logger.warn('sidebar close skipped because sidebar was already closed')
    return
  }

  isSidebarOpen.value = false
  logger.info('sidebar closed')
}

function toggleSidebar() {
  if (!isMobileShell.value) {
    logger.warn('sidebar toggle ignored because shell was not mobile')
    return
  }

  isSidebarOpen.value = !isSidebarOpen.value
  logger.info('sidebar toggled', { isOpen: isSidebarOpen.value })
}

function syncShellMode() {
  const wasMobile = isMobileShell.value
  isMobileShell.value = window.innerWidth <= MOBILE_SHELL_BREAKPOINT_PX

  if (!isMobileShell.value) {
    closeSidebar()
  }

  if (wasMobile !== isMobileShell.value) {
    logger.info('shell mode updated', {
      isMobileShell: isMobileShell.value,
      viewportWidth: window.innerWidth,
    })
  }
}

function handleShellKeydown(event: KeyboardEvent) {
  if (event.key === 'Escape') {
    logger.info('escape key pressed; attempting sidebar close')
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
    logger.warn('login transition flag clear skipped because flag was absent')
    return
  }

  const nextState = { ...state }

  delete nextState[LOGIN_TRANSITION_STATE_KEY]

  // Preserve vue-router's own history bookkeeping while dropping the one-shot animation flag.
  window.history.replaceState(nextState, '', window.location.href)
  logger.info('login transition flag cleared')
}

onMounted(() => {
  logger.info('layout mounted', { route: route.fullPath })
  handleWindowResize = syncShellMode
  syncShellMode()
  window.addEventListener('resize', handleWindowResize)
  window.addEventListener('keydown', handleShellKeydown)

  if (!hasLoginTransitionFlag()) {
    logger.info('layout mounted without login transition animation')
    return
  }

  clearLoginTransitionFlag()

  window.requestAnimationFrame(() => {
    playLoginShellEnter.value = true
    logger.info('login transition animation started', {
      durationMs: LOGIN_SHELL_ENTER_DURATION_MS,
    })

    clearAnimationTimer = window.setTimeout(() => {
      playLoginShellEnter.value = false
      logger.info('login transition animation completed')
    }, LOGIN_SHELL_ENTER_DURATION_MS)
  })
})

onBeforeUnmount(() => {
  if (clearAnimationTimer) {
    window.clearTimeout(clearAnimationTimer)
  }

  document.body.style.overflow = ''
  window.removeEventListener('keydown', handleShellKeydown)
  if (handleWindowResize) {
    window.removeEventListener('resize', handleWindowResize)
  }

  logger.info('layout unmounted')
})

watch(
  () => route.fullPath,
  (nextPath, previousPath) => {
    logger.info('route changed inside shell', {
      previousPath,
      nextPath,
    })
    closeSidebar()
  },
)

watch([isMobileShell, isSidebarOpen], ([mobile, open]) => {
  document.body.style.overflow = mobile && open ? 'hidden' : ''
  logger.log('shell body overflow updated', { mobile, open })
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
      aria-label="Close sidebar overlay"
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
  min-height: calc(100vh - var(--navbar-height));
  display: grid;
  grid-template-columns: var(--sidebar-width) minmax(0, 1fr);
  align-items: start;
}

.dashboard-main {
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
    grid-template-columns: 1fr;
  }

  .dashboard-main {
    padding: 18px 16px 24px;
  }
}

@media (max-width: 640px) {
  .dashboard-main {
    padding: 14px 12px 20px;
  }
}
</style>
