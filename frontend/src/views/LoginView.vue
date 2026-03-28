<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/services/auth'

const form = reactive({
  email: '',
  password: '',
  remember: false,
})

const router = useRouter()
const isSubmitting = ref(false)
const feedbackMessage = ref('')
const feedbackTone = ref<'info' | 'error'>('info')

async function handleSubmit() {
  if (isSubmitting.value) {
    return
  }

  isSubmitting.value = true
  feedbackMessage.value = ''

  try {
    await login(
      {
        email: form.email.trim(),
        password: form.password,
      },
      form.remember,
    )

    await router.push('/dashboard')
  } catch (error) {
    feedbackTone.value = 'error'
    feedbackMessage.value =
      error instanceof Error ? error.message : 'Unable to sign in right now. Please try again.'
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <main class="login-page">
    <div class="backdrop" aria-hidden="true">
      <div class="glow glow-primary"></div>
      <div class="glow glow-secondary"></div>
      <svg class="threads" viewBox="0 0 1600 900" preserveAspectRatio="none">
        <path
          class="thread thread-primary"
          d="M260 0C340 110 180 250 300 430C380 550 240 715 310 900"
        />
        <path
          class="thread thread-secondary"
          d="M610 0C720 150 520 295 680 470C780 600 620 760 700 900"
        />
        <path
          class="thread thread-tertiary"
          d="M1030 0C1145 120 950 300 1120 480C1220 620 1060 780 1140 900"
        />
        <path
          class="thread thread-neutral"
          d="M1370 0C1450 105 1320 245 1410 430C1490 595 1360 760 1435 900"
        />
      </svg>
    </div>

    <section class="scene">
      <div class="card">
        <div class="brand">
          <svg
            class="brand-mark"
            width="24"
            height="38"
            viewBox="0 0 24 38"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            aria-hidden="true"
          >
            <circle cx="12" cy="10" r="8" stroke="currentColor" stroke-width="3.5" />
            <rect x="9.5" y="16" width="5" height="18" rx="2" fill="currentColor" />
            <rect x="14.5" y="26" width="6" height="4" rx="1.5" fill="currentColor" />
            <rect x="14.5" y="30" width="4" height="2" rx="1" fill="var(--bg)" />
          </svg>
          <span class="brand-name">Regula</span>
        </div>

        <header class="heading">
          <h1>Welcome back</h1>
          <p>Sign in to continue to your workspace.</p>
        </header>

        <form class="form" @submit.prevent="handleSubmit">
          <div class="field">
            <label for="email">Email</label>
            <div class="input-wrap">
              <svg class="icon" viewBox="0 0 24 24" aria-hidden="true">
                <rect x="2" y="4" width="20" height="16" rx="2" />
                <polyline points="2,4 12,13 22,4" />
              </svg>
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
              <svg class="icon" viewBox="0 0 24 24" aria-hidden="true">
                <rect x="3" y="11" width="18" height="11" rx="2" />
                <path d="M7 11V7a5 5 0 0 1 10 0v4" />
              </svg>
              <input
                id="password"
                v-model="form.password"
                type="password"
                required
                autocomplete="current-password"
                placeholder="••••••••"
                :disabled="isSubmitting"
              />
            </div>
          </div>

          <div class="row">
            <label class="remember">
              <input v-model="form.remember" type="checkbox" :disabled="isSubmitting" />
              <span>Remember me</span>
            </label>
            <button type="button" class="link-button">Forgot password?</button>
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
          <button type="button" class="footer-link">Create one free →</button>
        </p>

        <div class="accents" aria-hidden="true">
          <span class="accent accent-primary"></span>
          <span class="accent accent-secondary"></span>
          <span class="accent accent-tertiary"></span>
        </div>
      </div>
    </section>
  </main>
</template>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background:
    radial-gradient(circle at top left, rgba(0, 102, 255, 0.08), transparent 28%),
    radial-gradient(circle at bottom right, rgba(0, 230, 118, 0.09), transparent 24%),
    var(--bg);
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

.threads {
  width: 100%;
  height: 100%;
}

.thread {
  fill: none;
  stroke-linecap: round;
  animation: drift 16s ease-in-out infinite alternate;
}

.thread-primary {
  stroke: var(--primary);
  stroke-width: 6;
  opacity: 0.28;
}

.thread-secondary {
  stroke: var(--secondary);
  stroke-width: 4;
  opacity: 0.24;
  animation-delay: -3s;
}

.thread-tertiary {
  stroke: var(--tertiary);
  stroke-width: 3;
  opacity: 0.22;
  animation-delay: -6s;
}

.thread-neutral {
  stroke: var(--neutral);
  stroke-width: 2;
  opacity: 0.12;
  animation-delay: -2s;
}

.scene {
  position: relative;
  z-index: 1;
  display: grid;
  min-height: 100vh;
  place-items: center;
  padding: 32px 20px;
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
}

.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 36px;
}

.brand-mark,
.brand-name {
  color: var(--neutral);
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
  color: var(--text-muted);
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
  color: var(--text-muted);
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
  width: 14px;
  height: 14px;
  transform: translateY(-50%);
  stroke: #c4c4c8;
  fill: none;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-width: 1.7;
  transition: stroke 0.2s ease;
  pointer-events: none;
}

input[type='email'],
input[type='password'] {
  width: 100%;
  border: 1.5px solid var(--border);
  border-radius: 12px;
  background: var(--surface-muted);
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
  background: var(--surface);
}

.input-wrap:focus-within .icon {
  stroke: var(--primary);
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
  color: var(--text-muted);
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

.submit-button {
  width: 100%;
  border: 0;
  border-radius: 999px;
  background: var(--neutral);
  padding: 13.5px;
  color: var(--surface);
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
  color: var(--text-muted);
  font-size: 12.5px;
  line-height: 1.5;
}

.status-error {
  border-left-color: #d64545;
  color: #b42318;
}

.footer {
  margin: 22px 0 0;
  text-align: center;
  color: var(--text-muted);
  font-size: 12.5px;
}

.footer-link {
  font-weight: 600;
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
