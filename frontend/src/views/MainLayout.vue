<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import NavBar from '@/components/NavBar.vue'
import Sidebar from '@/components/Sidebar.vue'

const LOGIN_TRANSITION_STATE_KEY = 'loginTransition'
const LOGIN_SHELL_ENTER_DURATION_MS = 640

const playLoginShellEnter = ref(false)

let clearAnimationTimer: number | undefined

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
})
</script>

<template>
  <div class="screen-overlay" :class="{ 'screen-overlay-login-enter': playLoginShellEnter }">
    <NavBar />
    <div class="dashboard-shell">
      <Sidebar />

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
</style>
