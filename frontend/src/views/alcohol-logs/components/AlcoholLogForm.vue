<script setup lang="ts">
import InfoCard from '@/components/ui/InfoCard.vue'
import { AlertTriangle, Edit2, X } from '@lucide/vue'
import type { AlcoholLog, AlcoholLogInput, AlcoholLogType, LogStatus } from '@/interfaces/AlcoholLog.interface'
import { createAlcoholLog } from '@/services/alcoholLog'
import { computed, ref, watch } from 'vue'
import DeviationForm from '@/views/deviation/components/DeviationForm.vue'
import { useDeviationStore } from '@/stores/deviation'

const emit = defineEmits<{
  (event: 'created', log: AlcoholLog): void
}>()

const ALCOHOL_LOG_TYPES = [
  { value: 'AGE_VERIFICATION', label: 'Age Verification' },
  { value: 'SERVICE_REFUSAL', label: 'Service Refusal' },
  { value: 'CLOSING_STOCK', label: 'Closing Stock' },
  { value: 'TRAINING', label: 'Training' },
  { value: 'INCIDENT', label: 'Incident' },
  { value: 'OTHER', label: 'Other' },
] as const

const deviationStore = useDeviationStore()

const title = ref<string>('')
const description = ref<string>('')
const logType = ref<AlcoholLogType | ''>('')
const status = ref<LogStatus | ''>('')
const idChecked = ref<boolean>(false)
const serviceRefused = ref<boolean>(false)
const estimatedAge = ref<number | null>(null)

const isSubmitting = ref(false)
const error = ref<string>('')
const isDeviationModalOpen = ref(false)
const pendingAlcoholPayload = ref<AlcoholLogInput | null>(null)
const pendingCreatedLog = ref<AlcoholLog | null>(null)

const shouldShowIdChecked = computed(() =>
  ['AGE_VERIFICATION', 'SERVICE_REFUSAL', 'INCIDENT'].includes(logType.value),
)
const shouldShowServiceRefused = computed(() =>
  ['SERVICE_REFUSAL', 'INCIDENT'].includes(logType.value),
)
const shouldShowEstimatedAge = computed(() =>
  ['AGE_VERIFICATION', 'SERVICE_REFUSAL', 'INCIDENT'].includes(logType.value),
)

watch(logType, () => {
  if (!shouldShowIdChecked.value) {
    idChecked.value = false
  }

  if (!shouldShowServiceRefused.value) {
    serviceRefused.value = false
  }

  if (!shouldShowEstimatedAge.value) {
    estimatedAge.value = null
  }
})

function resetAlcoholForm() {
  title.value = ''
  description.value = ''
  logType.value = ''
  status.value = ''
  idChecked.value = false
  serviceRefused.value = false
  estimatedAge.value = null
}

function buildPayload(): AlcoholLogInput | null {
  if (!title.value.trim() || !logType.value) {
    return null
  }

  return {
    title: title.value.trim(),
    description: description.value.trim() || null,
    logType: logType.value,
    status: status.value || null,
    idChecked: shouldShowIdChecked.value ? idChecked.value || null : null,
    serviceRefused: shouldShowServiceRefused.value ? serviceRefused.value || null : null,
    estimatedAge: shouldShowEstimatedAge.value ? estimatedAge.value || null : null,
  }
}

function requiresDeviation(payload: AlcoholLogInput) {
  return (
    payload.logType === 'SERVICE_REFUSAL' ||
    payload.logType === 'INCIDENT' ||
    payload.serviceRefused === true
  )
}

function resolveDeviationSeverity(payload: AlcoholLogInput) {
  if (payload.status === 'CRITICAL' || payload.logType === 'INCIDENT') {
    return 'CRITICAL' as const
  }

  return 'HIGH' as const
}

function seedDeviationForm(payload: AlcoholLogInput) {
  const today = new Date().toISOString().slice(0, 10)
  const details = [
    `Alcohol log type: ${payload.logType.replace(/_/g, ' ')}`,
    payload.status ? `Status: ${payload.status}` : null,
    payload.idChecked !== null ? `ID checked: ${payload.idChecked ? 'Yes' : 'No'}` : null,
    payload.serviceRefused !== null
      ? `Service refused: ${payload.serviceRefused ? 'Yes' : 'No'}`
      : null,
    typeof payload.estimatedAge === 'number' ? `Estimated age: ${payload.estimatedAge}` : null,
    payload.description ? `Description: ${payload.description}` : null,
  ]
    .filter((item): item is string => Boolean(item))
    .join('. ')

  deviationStore.resetForm()
  deviationStore.patchForm({
    title: payload.title,
    category: 'ALCOHOL',
    severity: resolveDeviationSeverity(payload),
    module: 'IK_ALCOHOL',
    status: 'OPEN',
    reportedDate: today,
    issueDescription: details,
  })
}

function clearDeviationFlowState() {
  isDeviationModalOpen.value = false
  pendingAlcoholPayload.value = null
  pendingCreatedLog.value = null
  deviationStore.error = null
}

async function submitRegularLog(payload: AlcoholLogInput) {
  error.value = ''
  isSubmitting.value = true

  try {
    const createdLog = await createAlcoholLog(payload)
    emit('created', createdLog)
    resetAlcoholForm()
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Failed to create alcohol log'
    console.error('Failed to create alcohol log', err)
  } finally {
    isSubmitting.value = false
  }
}

function closeDeviationModal() {
  if (isSubmitting.value) {
    return
  }

  if (pendingCreatedLog.value) {
    deviationStore.error =
      'The alcohol log has already been saved. Complete the deviation before closing this dialog.'
    return
  }

  clearDeviationFlowState()
}

async function createLog() {
  error.value = ''

  if (!title.value.trim()) {
    error.value = 'Title is required'
    return
  }

  if (!logType.value) {
    error.value = 'Log type is required'
    return
  }

  const payload = buildPayload()

  if (!payload) {
    error.value = 'Unable to build alcohol log payload'
    return
  }

  if (requiresDeviation(payload)) {
    pendingAlcoholPayload.value = payload
    pendingCreatedLog.value = null
    seedDeviationForm(payload)
    isDeviationModalOpen.value = true
    return
  }

  await submitRegularLog(payload)
}

async function submitAlcoholDeviation() {
  if (!pendingAlcoholPayload.value || !deviationStore.validateForm()) {
    return
  }

  error.value = ''
  isSubmitting.value = true

  try {
    let createdLog = pendingCreatedLog.value

    if (!createdLog) {
      createdLog = await createAlcoholLog(pendingAlcoholPayload.value)
      pendingCreatedLog.value = createdLog
    }

    const createdDeviation = await deviationStore.createDeviation({ logId: createdLog.id })

    if (!createdDeviation) {
      error.value = 'The alcohol log was saved, but the linked deviation still needs to be completed.'
      return
    }

    emit('created', createdLog)
    resetAlcoholForm()
    clearDeviationFlowState()
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Failed to create alcohol log'
    console.error('Failed to create alcohol log', err)
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <InfoCard
    class="card"
    title="Create Alcohol Log"
    :icon="Edit2"
    iconBackgroundColor="var(--icon-bg-purple)"
    iconColor="var(--icon-stroke-purple)"
  >
    <form @submit.prevent="createLog" class="form-grid">
      <label>
        <span>Log title *</span>
        <input v-model="title" placeholder="e.g., Age verification at entry" />
      </label>

      <label>
        <span>Log type *</span>
        <select v-model="logType" required>
          <option value="">Select log type...</option>
          <option v-for="type in ALCOHOL_LOG_TYPES" :key="type.value" :value="type.value">
            {{ type.label }}
          </option>
        </select>
      </label>

      <label>
        <span>Status</span>
        <select v-model="status">
          <option value="">No Status</option>
          <option value="OK">OK</option>
          <option value="WARNING">Warning</option>
          <option value="CRITICAL">Critical</option>
        </select>
      </label>

      <label class="field-wide">
        <span>Description</span>
        <textarea
          v-model="description"
          maxlength="500"
          placeholder="Additional details about the alcohol compliance log..."
        />
      </label>

      <label v-if="shouldShowIdChecked || shouldShowServiceRefused" class="field-wide checkboxes">
        <span>Flags</span>
        <div class="checkbox-group">
          <label v-if="shouldShowIdChecked" class="checkbox-label">
            <input v-model="idChecked" type="checkbox" />
            <span>ID Checked</span>
          </label>
          <label v-if="shouldShowServiceRefused" class="checkbox-label">
            <input v-model="serviceRefused" type="checkbox" />
            <span>Service Refused</span>
          </label>
        </div>
      </label>

      <label v-if="shouldShowEstimatedAge">
        <span>Estimated Age</span>
        <input v-model.number="estimatedAge" type="number" min="0" max="150" placeholder="Age" />
      </label>

      <div class="field-wide error-message" v-if="error">
        {{ error }}
      </div>
    </form>

    <div class="footer">
      <button class="submit" type="button" :disabled="isSubmitting" @click="createLog">
        {{ isSubmitting ? 'Submitting...' : 'Create log' }}
      </button>
    </div>
  </InfoCard>

  <div v-if="isDeviationModalOpen" class="overlay-backdrop" @click.self="closeDeviationModal">
    <div class="deviation-dialog">
      <div class="dialog-header">
        <div class="dialog-copy">
          <div class="dialog-badge">
            <AlertTriangle :size="16" aria-hidden="true" />
            <span>Deviation required</span>
          </div>
          <h2>Complete the deviation before saving this alcohol log</h2>
          <p>
            This alcohol log matches the backend deviation rule. Fill in the remaining deviation
            details to continue.
          </p>
        </div>

        <button class="close-btn" type="button" :disabled="isSubmitting" @click="closeDeviationModal">
          <X :size="18" aria-hidden="true" />
        </button>
      </div>

      <DeviationForm
        lockedCategory="ALCOHOL"
        lockedModule="IK_ALCOHOL"
        title="Complete Alcohol Deviation"
        submitLabel="Save Log and Deviation"
        footerText="The alcohol log will be saved together with this completed deviation."
        :showInternalTracking="false"
        :useExternalSubmit="true"
        :isSubmitting="isSubmitting"
        maxCardHeight="calc(100vh - 120px)"
        @submit="submitAlcoholDeviation"
      />
    </div>
  </div>
</template>

<style scoped>
.card {
  margin: 0;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 14px;
}

label {
  display: grid;
  gap: 6px;
}

label span {
  font-size: 0.9em;
  color: var(--text-secondary);
  margin-bottom: 5px;
}

.field-wide {
  grid-column: 1 / -1;
}

.checkboxes span {
  margin-bottom: 8px;
}

.checkbox-group {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: normal;
  letter-spacing: normal;
  color: var(--text);
}

.checkbox-label input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  padding: 0;
}

.error-message {
  color: var(--cta-red);
  font-size: 13px;
  padding: 10px 12px;
  background: #fff0f0;
  border: 1px solid var(--cta-red);
  border-radius: 8px;
  margin-top: 4px;
}

.footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding-top: 18px;
}

.submit {
  cursor: pointer;
  transition: all 0.2s;
  min-width: 120px;
}

.submit:disabled,
.close-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.overlay-backdrop {
  position: fixed;
  inset: 0;
  z-index: 1100;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: rgba(15, 23, 42, 0.5);
}

.deviation-dialog {
  width: min(960px, 100%);
  max-height: calc(100vh - 40px);
  overflow-y: auto;
  border-radius: 18px;
  background: var(--bg);
  box-shadow: 0 30px 80px rgba(15, 23, 42, 0.28);
  padding: 20px;
  display: grid;
  gap: 18px;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.dialog-copy {
  display: grid;
  gap: 10px;
}

.dialog-copy h2 {
  margin: 0;
  font-size: 24px;
  line-height: 1.15;
}

.dialog-copy p {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.5;
}

.dialog-badge {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: fit-content;
  border-radius: 999px;
  background: #fff3d9;
  color: #8a5d00;
  padding: 8px 12px;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.02em;
  text-transform: uppercase;
}

.close-btn {
  width: 40px;
  height: 40px;
  min-width: 40px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  border: 1px solid var(--border);
  background: #ffffff;
}

@media (max-width: 640px) {
  .overlay-backdrop {
    align-items: flex-start;
    padding: 12px;
  }

  .deviation-dialog {
    max-height: none;
    padding: 14px;
  }

  .dialog-header {
    align-items: flex-start;
  }

  .dialog-copy h2 {
    font-size: 20px;
  }
}
</style>
