<script setup lang="ts">
import { computed, reactive, ref, type CSSProperties } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/services/auth'
import { Key, Lock, Mail } from '@lucide/vue'
import InfoCard from '@/components/ui/InfoCard.vue'

const LOGIN_SUCCESS_ANIMATION_MS = 1120
const REDUCED_MOTION_SUCCESS_ANIMATION_MS = 140

type AuthPhase = 'idle' | 'submitting' | 'successAnimating'

const form = reactive({
  email: '',
  password: '',
  remember: false,
})

const router = useRouter()
const authPhase = ref<AuthPhase>('idle')
const cardStageRef = ref<HTMLElement | null>(null)
const feedbackMessage = ref('')
const feedbackTone = ref<'info' | 'error'>('info')
const cinematicOrigin = ref({
  x: 0,
  y: 0,
})

const isSubmitting = computed(() => authPhase.value !== 'idle')
const isSuccessAnimating = computed(() => authPhase.value === 'successAnimating')
const cinematicStyle = computed(
  () =>
    ({
      '--cinematic-origin-x': `${cinematicOrigin.value.x}px`,
      '--cinematic-origin-y': `${cinematicOrigin.value.y}px`,
    }) as CSSProperties,
)


function wait(durationMs: number) {
  return new Promise((resolve) => window.setTimeout(resolve, durationMs))
}

function getSuccessAnimationDuration() {
  return window.matchMedia('(prefers-reduced-motion: reduce)').matches
    ? REDUCED_MOTION_SUCCESS_ANIMATION_MS
    : LOGIN_SUCCESS_ANIMATION_MS
}

function syncCinematicOrigin() {
  const rect = cardStageRef.value?.getBoundingClientRect()

  cinematicOrigin.value = rect
    ? {
        x: rect.left + rect.width / 2,
        y: rect.top + rect.height / 2,
      }
    : {
        x: window.innerWidth / 2,
        y: window.innerHeight / 2,
      }
}


async function handleSubmit() {
  if (authPhase.value !== 'idle') {
    return
  }

  authPhase.value = 'submitting'
  feedbackMessage.value = ''

  try {
    await login(
      {
        email: form.email.trim(),
        password: form.password,
      },
      form.remember,
    )
  } catch (error) {
    authPhase.value = 'idle'
    feedbackTone.value = 'error'
    feedbackMessage.value =
      error instanceof Error ? error.message : 'Unable to sign in right now. Please try again.'

    return
  }

  syncCinematicOrigin()
  authPhase.value = 'successAnimating'

  await wait(getSuccessAnimationDuration())

  try {
    await router.push({
      name: 'dashboard',
      state: {
        loginTransition: 'success',
      },
    })
  } catch {
    authPhase.value = 'idle'
    feedbackTone.value = 'error'
    feedbackMessage.value = 'Signed in, but we could not open the dashboard. Please try again.'
  }
}

async function goToRegister() {
  if (isSubmitting.value) {
    return
  }

  feedbackTone.value = 'info'
  feedbackMessage.value = 'Ask your administrator for an invitation to join your workspace.'
}
</script>

<template>
  <main class="login-page">
    <div class="backdrop" aria-hidden="true">
      <div class="grid-layer"></div>
      <div class="wash wash-a"></div>
      <div class="wash wash-b"></div>
      <div class="wash wash-c"></div>
      <div class="dot-grid"></div>
    </div>

    <section class="scene">
      <div ref="cardStageRef" class="card-stage">
        <InfoCard
          :title="'Regula'"
          :icon="Key"
          :iconBackgroundColor="'var(--neutral)'"
          :iconColor="'white'"
          :iconTransform="'rotate(-45deg)'"
          class="card"
        >
          <header class="heading">
            <h1>Welcome back</h1>
            <p>Sign in to continue to your workspace.</p>
          </header>

          <form class="form" @submit.prevent="handleSubmit">
            <div class="field">
              <label for="email">Email</label>
              <div class="input-wrap">
                <Mail class="icon" :size="13" aria-hidden="true" />
                <input
                  id="email"
                  v-model="form.email"
                  type="email"
                  required
                  autocomplete="email"
                  placeholder="you@company.com"
                  :disabled="isSubmitting"
                />
              </div>
            </div>

            <div class="field">
              <label for="password">Password</label>
              <div class="input-wrap">
                <Lock class="icon" :size="13" aria-hidden="true" />
                <input
                  id="password"
                  v-model="form.password"
                  type="password"
                  required
                  autocomplete="current-password"
                  placeholder="Password"
                  :disabled="isSubmitting"
                />
              </div>
            </div>

            <div class="row">
              <label class="remember">
                <input v-model="form.remember" type="checkbox" :disabled="isSubmitting" />
                <span>Remember me</span>
              </label>
              <button type="button" class="link-button" :disabled="isSubmitting">
                Forgot password?
              </button>
            </div>

            <button class="submit-button" type="submit" :disabled="isSubmitting">
              {{ isSubmitting ? 'Signing in...' : 'Sign in' }}
            </button>

            <p v-if="feedbackMessage" :class="['status', `status-${feedbackTone}`]">
              {{ feedbackMessage }}
            </p>
          </form>

          <p class="footer">
            No account yet?
            <button
              type="button"
              class="footer-link"
              :disabled="isSubmitting"
              @click="goToRegister"
            >
              Create one free →
            </button>
          </p>

          <div class="accents" aria-hidden="true">
            <span class="accent accent-primary"></span>
            <span class="accent accent-secondary"></span>
            <span class="accent accent-tertiary"></span>
          </div>
        </InfoCard>
      </div>
    </section>

    <div
      v-if="isSuccessAnimating"
      class="cinematic-overlay"
      :style="cinematicStyle"
      aria-hidden="true"
    >
      <div class="cinematic-takeover"></div>
      <div class="cinematic-vignette"></div>
      <div class="cinematic-grid"></div>
      <div class="cinematic-shutter cinematic-shutter-top"></div>
      <div class="cinematic-shutter cinematic-shutter-bottom"></div>
      <div class="cinematic-beam cinematic-beam-horizontal"></div>
      <div class="cinematic-beam cinematic-beam-vertical"></div>
      <div class="cinematic-ring cinematic-ring-outer"></div>
      <div class="cinematic-ring cinematic-ring-mid"></div>
      <div class="cinematic-ring cinematic-ring-inner"></div>
      <div class="cinematic-core">
        <span class="cinematic-core-line cinematic-core-line-horizontal"></span>
        <span class="cinematic-core-line cinematic-core-line-vertical"></span>
      </div>
      <div class="cinematic-flash"></div>
    </div>
  </main>
</template>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background:
    radial-gradient(circle at top left, rgba(0, 102, 255, 0.08), transparent 28%),
    radial-gradient(circle at bottom right, rgba(0, 230, 118, 0.09), transparent 24%), var(--bg);
}

.backdrop {
  position: absolute;
  inset: 0;
  overflow: hidden;
}

.glow {
  position: absolute;
  border-radius: 999px;
  filter: blur(70px);
  opacity: 0.35;
}

.glow-primary {
  top: 12%;
  left: 8%;
  width: 260px;
  height: 260px;
  background: rgba(0, 102, 255, 0.16);
}

.glow-secondary {
  right: 10%;
  bottom: 8%;
  width: 240px;
  height: 240px;
  background: rgba(0, 230, 118, 0.14);
}

.grid-layer {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(0, 0, 0, 0.035) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 0, 0, 0.035) 1px, transparent 1px);
  background-size: 48px 48px;
}

.wash {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
  pointer-events: none;
}
.wash-a {
  width: 560px;
  height: 560px;
  top: -140px;
  left: -120px;
  background: rgba(0, 102, 255, 0.09);
}
.wash-b {
  width: 480px;
  height: 480px;
  bottom: -100px;
  right: -80px;
  background: rgba(124, 58, 237, 0.07);
}
.wash-c {
  width: 320px;
  height: 320px;
  top: 50%;
  right: 20%;
  transform: translateY(-50%);
  background: rgba(0, 230, 118, 0.05);
}

.dot-grid {
  position: absolute;
  top: 32px;
  right: 32px;
  width: 120px;
  height: 120px;
  background-image: radial-gradient(circle, rgba(0, 0, 0, 0.12) 1.2px, transparent 1.2px);
  background-size: 14px 14px;
  opacity: 0.5;
}
.scene {
  position: relative;
  z-index: 1;
  display: grid;
  min-height: 100vh;
  place-items: center;
  padding: 32px 20px;
}

.card-stage {
  width: min(100%, 420px);
}

.card {
  width: min(100%, 420px);
  border: 1px solid var(--border);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.88);
  padding: 48px 44px 40px;
  box-shadow: var(--shadow-soft);
  backdrop-filter: blur(16px);
  animation: rise 0.6s cubic-bezier(0.22, 1, 0.36, 1) both;
  transform-origin: center center;
  transition:
    background-color 0.45s ease,
    border-color 0.45s ease,
    box-shadow 0.45s ease,
    color 0.2s ease,
    transform 0.45s ease;
}

.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 36px;
}

.brand-name {
  color: var(--neutral);
}

.brand-mark {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: var(--neutral);
}

.logo-icon {
  transform: rotate(-45deg);
  color: var(--bg-secondary);
}

.brand-name {
  font-size: 20px;
  font-weight: 700;
  letter-spacing: -0.04em;
}

.heading {
  margin-bottom: 28px;
}

.heading h1 {
  margin: 0;
  color: var(--text);
  font-size: 24px;
  font-weight: 700;
  letter-spacing: -0.04em;
  line-height: 1.2;
}

.heading p {
  margin: 5px 0 0;
  color: var(--text-secondary);
  font-size: 13.5px;
}

.form {
  display: grid;
  gap: 14px;
}

.field {
  display: grid;
  gap: 7px;
}

label {
  color: var(--text-secondary);
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}

.input-wrap {
  position: relative;
}

.icon {
  position: absolute;
  top: 50%;
  left: 13px;
  width: 12px;
  height: 12px;
  transform: translateY(-50%);
  color: #c4c4c8;
  transition: color 0.2s ease;
  pointer-events: none;
}

input[type='email'],
input[type='password'] {
  width: 100%;
  border: 1.5px solid var(--border);
  border-radius: 12px;
  background: var(--bg);
  padding: 12px 14px 12px 37px;
  color: var(--text);
  font-size: 14px;
  outline: none;
  caret-color: var(--primary);
  transition:
    border-color 0.2s ease,
    background-color 0.2s ease;
}

input::placeholder {
  color: #c8c8cc;
}

input:focus {
  border-color: var(--primary);
  background: var(--bg-secondary);
}

.input-wrap:focus-within .icon {
  color: var(--primary);
}

.row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin: 4px 0 2px;
}

.remember {
  display: flex;
  align-items: center;
  gap: 7px;
  cursor: pointer;
  user-select: none;
}

.remember span {
  color: var(--text-secondary);
  font-size: 12.5px;
  font-weight: 400;
  letter-spacing: 0;
  text-transform: none;
}

.remember input[type='checkbox'] {
  width: 14px;
  height: 14px;
  margin: 0;
  accent-color: var(--primary);
}

.link-button,
.footer-link {
  border: 0;
  background: transparent;
  padding: 0;
  color: var(--primary);
  cursor: pointer;
}

.link-button {
  font-size: 12.5px;
  font-weight: 600;
}

.link-button:disabled,
.footer-link:disabled {
  cursor: not-allowed;
  opacity: 0.45;
}

.submit-button {
  width: 100%;
  border: 0;
  border-radius: 999px;
  background: var(--neutral);
  padding: 13.5px;
  color: var(--bg-secondary);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition:
    transform 0.12s ease,
    background-color 0.2s ease,
    opacity 0.2s ease;
}

.submit-button:hover:enabled {
  transform: translateY(-1px);
  background: #2c2e30;
}

.submit-button:disabled {
  cursor: progress;
  opacity: 0.7;
}

.status {
  margin: 0;
  border-left: 3px solid var(--primary);
  padding-left: 12px;
  color: var(--text-secondary);
  font-size: 12.5px;
  line-height: 1.5;
}

.status-error {
  border-left-color: #d64545;
  color: #b42318;
}

.accents {
  display: flex;
  gap: 5px;
  margin-top: 26px;
}

.accent {
  display: block;
  height: 3px;
  border-radius: 999px;
}

.accent-primary {
  flex: 3;
  background: var(--primary);
  opacity: 0.5;
}

.accent-secondary {
  flex: 1.5;
  background: var(--secondary);
  opacity: 0.45;
}

.accent-tertiary {
  flex: 1;
  background: var(--tertiary);
  opacity: 0.4;
}

.cinematic-overlay {
  position: fixed;
  inset: 0;
  z-index: 20;
  overflow: hidden;
  pointer-events: none;
  background: #000;
  isolation: isolate;
}

.cinematic-takeover,
.cinematic-vignette,
.cinematic-grid,
.cinematic-shutter,
.cinematic-beam,
.cinematic-ring,
.cinematic-core,
.cinematic-flash {
  position: absolute;
}

.cinematic-takeover {
  inset: -12vmax;
  background:
    radial-gradient(circle at center, rgba(255, 255, 255, 0.08), transparent 34%),
    radial-gradient(circle at center, rgba(255, 255, 255, 0.04), transparent 52%), #000;
  clip-path: circle(26px at var(--cinematic-origin-x) var(--cinematic-origin-y));
  animation: cinematic-takeover 230ms cubic-bezier(0.2, 0.9, 0.2, 1) forwards;
}

.cinematic-vignette {
  inset: 0;
  background:
    radial-gradient(circle at center, rgba(255, 255, 255, 0.08), transparent 38%),
    radial-gradient(circle at center, transparent 28%, rgba(0, 0, 0, 0.82) 82%);
  opacity: 0;
  animation: cinematic-vignette-bloom 720ms ease 180ms forwards;
}

.cinematic-grid {
  inset: -35%;
  background-image:
    linear-gradient(rgba(255, 255, 255, 0.12) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.12) 1px, transparent 1px);
  background-size: 68px 68px;
  opacity: 0;
  transform: perspective(1100px) rotateX(78deg) scale(1.85) translateY(22vh);
  transform-origin: center center;
  animation: cinematic-grid-rush 820ms cubic-bezier(0.16, 1, 0.3, 1) 200ms forwards;
}

.cinematic-shutter {
  left: -10%;
  width: 120%;
  height: 50%;
  background: linear-gradient(180deg, rgba(10, 10, 10, 0.98), rgba(0, 0, 0, 1));
  opacity: 0;
}

.cinematic-shutter-top {
  top: 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: 0 22px 44px rgba(255, 255, 255, 0.07);
  transform: translateY(-112%);
  animation: cinematic-shutter-top 700ms cubic-bezier(0.2, 1, 0.28, 1) 180ms forwards;
}

.cinematic-shutter-bottom {
  bottom: 0;
  border-top: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: 0 -22px 44px rgba(255, 255, 255, 0.07);
  transform: translateY(112%);
  animation: cinematic-shutter-bottom 700ms cubic-bezier(0.2, 1, 0.28, 1) 180ms forwards;
}

.cinematic-beam {
  top: 50%;
  left: 50%;
  opacity: 0;
}

.cinematic-beam-horizontal {
  width: 130vmax;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.92), transparent);
  transform: translate(-50%, -50%) scaleX(0.16);
  animation: cinematic-beam-horizontal 520ms ease-out 380ms forwards;
}

.cinematic-beam-vertical {
  width: 1px;
  height: 130vmax;
  background: linear-gradient(180deg, transparent, rgba(255, 255, 255, 0.92), transparent);
  transform: translate(-50%, -50%) scaleY(0.16);
  animation: cinematic-beam-vertical 520ms ease-out 420ms forwards;
}

.cinematic-ring {
  top: 50%;
  left: 50%;
  border: 1px solid rgba(255, 255, 255, 0.72);
  border-radius: 999px;
  opacity: 0;
}

.cinematic-ring-outer {
  width: 62vmax;
  height: 62vmax;
  transform: translate(-50%, -50%) scale(0.18);
  animation: cinematic-ring-pulse 860ms cubic-bezier(0.16, 1, 0.3, 1) 240ms forwards;
}

.cinematic-ring-mid {
  width: 38vmax;
  height: 38vmax;
  transform: translate(-50%, -50%) scale(0.16);
  animation: cinematic-ring-pulse 740ms cubic-bezier(0.16, 1, 0.3, 1) 320ms forwards;
}

.cinematic-ring-inner {
  width: 16vmax;
  height: 16vmax;
  transform: translate(-50%, -50%) scale(0.18);
  animation: cinematic-ring-pulse 620ms cubic-bezier(0.16, 1, 0.3, 1) 400ms forwards;
}

.cinematic-core {
  top: 50%;
  left: 50%;
  width: min(15vmin, 144px);
  height: min(15vmin, 144px);
  border: 2px solid rgba(255, 255, 255, 0.94);
  box-shadow:
    0 0 0 1px rgba(255, 255, 255, 0.08),
    0 0 60px rgba(255, 255, 255, 0.12);
  opacity: 0;
  transform: translate(-50%, -50%) rotate(45deg) scale(0.26);
  animation: cinematic-core-lock 760ms cubic-bezier(0.16, 1, 0.3, 1) 320ms forwards;
}

.cinematic-core-line {
  position: absolute;
  top: 50%;
  left: 50%;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 0 18px rgba(255, 255, 255, 0.3);
}

.cinematic-core-line-horizontal {
  width: 64%;
  height: 2px;
  transform: translate(-50%, -50%) scaleX(0.18);
  animation: cinematic-core-line-horizontal 600ms cubic-bezier(0.16, 1, 0.3, 1) 420ms forwards;
}

.cinematic-core-line-vertical {
  width: 2px;
  height: 64%;
  transform: translate(-50%, -50%) scaleY(0.18);
  animation: cinematic-core-line-vertical 600ms cubic-bezier(0.16, 1, 0.3, 1) 460ms forwards;
}

.cinematic-flash {
  inset: 0;
  background: radial-gradient(
    circle at center,
    rgba(255, 255, 255, 0.96),
    rgba(255, 255, 255, 0.18) 24%,
    transparent 60%
  );
  mix-blend-mode: screen;
  opacity: 0;
  animation: cinematic-flash-pop 280ms ease-out 780ms forwards;
}

@keyframes rise {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.98);
  }

  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes drift {
  from {
    transform: translateX(-12px) translateY(-8px);
  }

  to {
    transform: translateX(14px) translateY(10px);
  }
}

@keyframes cinematic-takeover {
  0% {
    clip-path: circle(26px at var(--cinematic-origin-x) var(--cinematic-origin-y));
  }

  100% {
    clip-path: circle(155vmax at 50% 50%);
  }
}

@keyframes cinematic-vignette-bloom {
  0% {
    opacity: 0;
  }

  100% {
    opacity: 1;
  }
}

@keyframes cinematic-grid-rush {
  0% {
    opacity: 0;
    transform: perspective(1100px) rotateX(78deg) scale(2.05) translateY(28vh);
  }

  30% {
    opacity: 0.46;
  }

  100% {
    opacity: 0;
    transform: perspective(1100px) rotateX(78deg) scale(1.12) translateY(-12vh);
  }
}

@keyframes cinematic-shutter-top {
  0% {
    opacity: 0;
    transform: translateY(-112%);
  }

  22% {
    opacity: 1;
  }

  48% {
    transform: translateY(4%);
  }

  100% {
    opacity: 0.92;
    transform: translateY(-118%);
  }
}

@keyframes cinematic-shutter-bottom {
  0% {
    opacity: 0;
    transform: translateY(112%);
  }

  22% {
    opacity: 1;
  }

  48% {
    transform: translateY(-4%);
  }

  100% {
    opacity: 0.92;
    transform: translateY(118%);
  }
}

@keyframes cinematic-beam-horizontal {
  0% {
    opacity: 0;
    transform: translate(-50%, -50%) scaleX(0.16);
  }

  35% {
    opacity: 0.94;
  }

  100% {
    opacity: 0;
    transform: translate(-50%, -50%) scaleX(1.35);
  }
}

@keyframes cinematic-beam-vertical {
  0% {
    opacity: 0;
    transform: translate(-50%, -50%) scaleY(0.16);
  }

  35% {
    opacity: 0.94;
  }

  100% {
    opacity: 0;
    transform: translate(-50%, -50%) scaleY(1.35);
  }
}

@keyframes cinematic-ring-pulse {
  0% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.18);
  }

  32% {
    opacity: 0.88;
  }

  100% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(1.26);
  }
}

@keyframes cinematic-core-lock {
  0% {
    opacity: 0;
    transform: translate(-50%, -50%) rotate(45deg) scale(0.26);
  }

  30% {
    opacity: 1;
  }

  60% {
    transform: translate(-50%, -50%) rotate(45deg) scale(1);
  }

  100% {
    opacity: 0;
    transform: translate(-50%, -50%) rotate(45deg) scale(1.85);
  }
}

@keyframes cinematic-core-line-horizontal {
  0% {
    transform: translate(-50%, -50%) scaleX(0.18);
  }

  100% {
    transform: translate(-50%, -50%) scaleX(1);
  }
}

@keyframes cinematic-core-line-vertical {
  0% {
    transform: translate(-50%, -50%) scaleY(0.18);
  }

  100% {
    transform: translate(-50%, -50%) scaleY(1);
  }
}

@keyframes cinematic-flash-pop {
  0% {
    opacity: 0;
  }

  30% {
    opacity: 0.9;
  }

  100% {
    opacity: 0;
  }
}

@media (prefers-reduced-motion: reduce) {
  .card,
  .cinematic-takeover,
  .cinematic-vignette,
  .cinematic-grid,
  .cinematic-shutter,
  .cinematic-beam,
  .cinematic-ring,
  .cinematic-core,
  .cinematic-core-line,
  .cinematic-flash {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}

@media (max-width: 640px) {
  .scene {
    padding: 20px 16px;
  }

  .card {
    padding: 36px 24px 28px;
  }

  .row {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
