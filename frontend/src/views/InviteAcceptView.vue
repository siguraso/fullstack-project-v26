<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CheckCircle2, KeyRound, Mail, ShieldCheck, UserRound } from '@lucide/vue'
import { register } from '@/services/auth'
import { validateInviteToken } from '@/services/invitation'

const route = useRoute()
const router = useRouter()

const isValidating = ref(true)
const isSubmitting = ref(false)
const inviteToken = ref('')
const invitedEmail = ref('')
const invitedOrgId = ref<number | null>(null)
const errorMessage = ref('')
const statusMessage = ref('')

const form = reactive({
  firstName: '',
  lastName: '',
  phone: '',
  password: '',
  remember: true,
})

const inviteSteps = [
  {
    icon: Mail,
    title: 'Verified invite',
    description: 'We checked the token and confirmed your email.',
  },
  {
    icon: UserRound,
    title: 'Complete profile',
    description: 'Add your name and optional phone number.',
  },
  {
    icon: ShieldCheck,
    title: 'Secure access',
    description: 'Choose a password and enter the workspace.',
  },
]

const inviteSummary = computed(() => {
  if (!invitedEmail.value) {
    return null
  }

  return {
    email: invitedEmail.value,
    organizationId: invitedOrgId.value,
  }
})

function toErrorMessage(error: unknown, fallback: string) {
  return error instanceof Error && error.message.trim().length > 0 ? error.message : fallback
}

function readTokenFromRoute() {
  const tokenQuery = route.query.token
  return Array.isArray(tokenQuery) ? tokenQuery[0] : tokenQuery
}

async function loadInvite() {
  isValidating.value = true
  errorMessage.value = ''
  statusMessage.value = ''

  const token = readTokenFromRoute()

  if (!token || token.trim().length === 0) {
    errorMessage.value = 'This invitation link is missing its token.'
    isValidating.value = false
    return
  }

  inviteToken.value = token

  try {
    const validation = await validateInviteToken(token)
    invitedEmail.value = validation.email
    invitedOrgId.value = validation.organizationId
    statusMessage.value = 'Invitation verified. Fill in your details to activate your account.'
    document.title = 'Accept invitation · Regula'
  } catch (error) {
    errorMessage.value = toErrorMessage(error, 'Invalid or expired invitation link.')
  } finally {
    isValidating.value = false
  }
}

async function acceptInvite() {
  if (isSubmitting.value || !invitedEmail.value || !inviteToken.value) {
    return
  }

  isSubmitting.value = true
  errorMessage.value = ''
  statusMessage.value = ''

  try {
    await register(
      {
        email: invitedEmail.value,
        password: form.password,
        firstName: form.firstName.trim(),
        lastName: form.lastName.trim(),
        phone: form.phone.trim(),
        inviteToken: inviteToken.value,
      },
      form.remember,
    )

    await router.push('/dashboard')
  } catch (error) {
    errorMessage.value = toErrorMessage(error, 'Unable to accept the invitation right now.')
  } finally {
    isSubmitting.value = false
  }
}

onMounted(() => {
  void loadInvite()
})
</script>

<template>
  <main class="invite-page">
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
      <div class="invite-shell">
        <aside class="invite-panel">
          <div class="brand">
            <div class="brand-mark">
              <KeyRound class="logo-icon" />
            </div>
            <span class="brand-name">Regula</span>
          </div>

          <p class="eyebrow">Invitation accepted by admin</p>
          <h1>Set up your staff account</h1>
          <p class="lead">
            You were invited to join your team workspace. Confirm your details once, and you’ll be
            ready to sign in immediately.
          </p>

          <div class="invite-steps">
            <div v-for="step in inviteSteps" :key="step.title" class="step-card">
              <div class="step-icon">
                <component :is="step.icon" />
              </div>
              <div>
                <strong>{{ step.title }}</strong>
                <p>{{ step.description }}</p>
              </div>
            </div>
          </div>

          <p class="footnote">
            Your account will be created under the organization that sent the invite.
          </p>
        </aside>

        <section class="invite-card">
          <header class="card-header">
            <p class="card-kicker">Complete registration</p>
            <h2>Finish your profile</h2>
            <p>We’ll use the email from your invitation link.</p>
          </header>

          <div v-if="isValidating" class="state-card state-loading">
            <span class="state-dot"></span>
            Validating invitation...
          </div>

          <template v-else>
            <div v-if="errorMessage" class="state-card state-error">
              <span class="state-dot"></span>
              <div>
                <strong>Invitation unavailable</strong>
                <p>{{ errorMessage }}</p>
              </div>
            </div>

            <div v-else class="state-card state-success">
              <CheckCircle2 class="state-icon" />
              <div>
                <strong>Invitation verified</strong>
                <p>{{ statusMessage }}</p>
              </div>
            </div>

            <div v-if="inviteSummary" class="invite-summary">
              <div>
                <span>Email</span>
                <strong>{{ inviteSummary.email }}</strong>
              </div>
              <div>
                <span>Organization</span>
                <strong>#{{ inviteSummary.organizationId }}</strong>
              </div>
            </div>

            <form v-if="!errorMessage" class="form" @submit.prevent="acceptInvite">
              <div class="field-grid">
                <label class="field">
                  <span>First name</span>
                  <input
                    v-model="form.firstName"
                    type="text"
                    required
                    autocomplete="given-name"
                    placeholder="Jane"
                  />
                </label>

                <label class="field">
                  <span>Last name</span>
                  <input
                    v-model="form.lastName"
                    type="text"
                    required
                    autocomplete="family-name"
                    placeholder="Doe"
                  />
                </label>
              </div>

              <label class="field">
                <span>Email</span>
                <input :value="invitedEmail" type="email" readonly />
              </label>

              <label class="field">
                <span>Phone number</span>
                <input
                  v-model="form.phone"
                  type="tel"
                  autocomplete="tel"
                  placeholder="+47 99 99 99 99"
                />
              </label>

              <label class="field">
                <span>Password</span>
                <input
                  v-model="form.password"
                  type="password"
                  required
                  minlength="8"
                  autocomplete="new-password"
                  placeholder="At least 8 characters"
                />
              </label>

              <label class="remember">
                <input v-model="form.remember" type="checkbox" />
                <span>Keep me signed in on this device</span>
              </label>

              <button type="submit" class="submit-button" :disabled="isSubmitting">
                {{ isSubmitting ? 'Creating account...' : 'Create account' }}
              </button>
            </form>
          </template>
        </section>
      </div>
    </section>
  </main>
</template>

<style scoped>
.invite-page {
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

.invite-shell {
  width: min(100%, 1040px);
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(380px, 460px);
  gap: 24px;
  align-items: stretch;
}

.invite-panel,
.invite-card {
  border: 1px solid var(--border);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: var(--shadow-soft);
  backdrop-filter: blur(16px);
}

.invite-panel {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 40px;
}

.invite-card {
  padding: 32px;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 40px;
}

.brand-mark {
  display: grid;
  place-items: center;
  width: 40px;
  height: 40px;
  border-radius: 14px;
  background: linear-gradient(135deg, var(--primary), #4bb7ff);
  color: white;
  box-shadow: 0 14px 30px rgba(0, 102, 255, 0.24);
}

.logo-icon {
  width: 20px;
  height: 20px;
}

.brand-name {
  font-size: 1.1rem;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.eyebrow {
  margin: 0 0 10px;
  font-size: 0.78rem;
  letter-spacing: 0.14em;
  text-transform: uppercase;
  color: var(--primary);
  font-weight: 700;
}

h1,
h2,
p {
  margin: 0;
}

h1 {
  font-size: clamp(2rem, 4vw, 3.2rem);
  line-height: 1.04;
  margin-bottom: 14px;
}

.lead {
  max-width: 56ch;
  font-size: 1.02rem;
  line-height: 1.7;
  color: var(--text-secondary);
}

.invite-steps {
  display: grid;
  gap: 14px;
  margin-top: 32px;
}

.step-card {
  display: grid;
  grid-template-columns: 44px minmax(0, 1fr);
  gap: 14px;
  align-items: start;
  padding: 16px;
  border: 1px solid rgba(48, 84, 49, 0.12);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.66);
}

.step-icon {
  display: grid;
  place-items: center;
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: rgba(0, 102, 255, 0.08);
  color: var(--primary);
}

.step-card strong {
  display: block;
  margin-bottom: 4px;
}

.step-card p,
.footnote,
.card-header p,
.state-card p,
.invite-summary span {
  color: var(--text-secondary);
  line-height: 1.55;
}

.footnote {
  margin-top: 28px;
  font-size: 0.92rem;
}

.card-header {
  margin-bottom: 18px;
}

.card-kicker {
  font-size: 0.78rem;
  text-transform: uppercase;
  letter-spacing: 0.14em;
  font-weight: 700;
  color: var(--secondary);
  margin-bottom: 8px;
}

h2 {
  font-size: 1.6rem;
  line-height: 1.15;
  margin-bottom: 8px;
}

.state-card,
.invite-summary {
  border-radius: 18px;
  border: 1px solid var(--border);
  background: rgba(255, 255, 255, 0.9);
}

.state-card {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  padding: 14px 16px;
  margin-bottom: 16px;
}

.state-icon,
.state-dot {
  flex: 0 0 auto;
}

.state-icon {
  width: 20px;
  height: 20px;
  color: var(--secondary);
  margin-top: 2px;
}

.state-dot {
  width: 10px;
  height: 10px;
  margin-top: 6px;
  border-radius: 999px;
  background: var(--primary);
  box-shadow: 0 0 0 6px rgba(0, 102, 255, 0.1);
}

.state-loading {
  background: rgba(227, 242, 255, 0.75);
  color: #0f3d91;
}

.state-error {
  background: rgba(254, 236, 235, 0.8);
  color: #8e1f17;
}

.state-success {
  background: rgba(236, 250, 242, 0.85);
  color: #12663a;
}

.invite-summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  padding: 14px 16px;
  margin-bottom: 18px;
}

.invite-summary div {
  min-width: 0;
}

.invite-summary strong {
  display: block;
  margin-top: 2px;
}

.form {
  display: grid;
  gap: 14px;
}

.field-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.field {
  display: grid;
  gap: 8px;
}

.field span,
.remember span {
  font-size: 0.92rem;
  font-weight: 600;
}

.field input {
  width: 100%;
  border: 1px solid var(--border);
  border-radius: 14px;
  padding: 12px 14px;
  font: inherit;
  background: rgba(255, 255, 255, 0.95);
  transition:
    border-color 0.15s ease,
    box-shadow 0.15s ease,
    transform 0.15s ease;
}

.field input:focus {
  outline: none;
  border-color: rgba(0, 102, 255, 0.5);
  box-shadow: 0 0 0 4px rgba(0, 102, 255, 0.1);
}

.field input[readonly] {
  background: #f6f7f9;
  color: var(--text-secondary);
}

.remember {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 0 2px;
}

.remember input {
  width: 16px;
  height: 16px;
}

.submit-button {
  width: 100%;
  border: 0;
  border-radius: 14px;
  padding: 13px 16px;
  background: linear-gradient(135deg, #2f4fa8, #1f75d8);
  color: #fff;
  font: inherit;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 14px 30px rgba(33, 66, 127, 0.24);
  transition:
    transform 0.15s ease,
    box-shadow 0.15s ease,
    opacity 0.15s ease;
}

.submit-button:hover:not(:disabled) {
  transform: translateY(-1px);
  background: linear-gradient(135deg, #263f88, #165daa);
  box-shadow: 0 18px 32px rgba(33, 66, 127, 0.3);
}

.submit-button:disabled {
  opacity: 0.72;
  cursor: not-allowed;
}

@media (max-width: 960px) {
  .invite-shell {
    grid-template-columns: 1fr;
  }

  .invite-panel,
  .invite-card {
    padding: 28px;
  }
}

@media (max-width: 640px) {
  .scene {
    padding: 18px 14px;
  }

  .invite-panel,
  .invite-card {
    padding: 22px 18px;
    border-radius: 18px;
  }

  .field-grid,
  .invite-summary {
    grid-template-columns: 1fr;
  }

  h1 {
    font-size: 1.9rem;
  }
}
</style>
