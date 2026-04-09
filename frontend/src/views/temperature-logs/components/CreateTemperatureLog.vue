<script setup lang="ts">
import InfoCard from '@/components/ui/InfoCard.vue'
import type { TemperatureLog, TemperatureLogInput } from '@/interfaces/TemperatureLog.interface'
import type { TemperatureZone } from '@/interfaces/TemperatureZone.interface'
import { createTemperatureLog } from '@/services/temperatureLog'
import { computed, ref } from 'vue'
import { AlertTriangle, Edit2, X } from '@lucide/vue'
import DeviationForm from '@/views/deviation/components/DeviationForm.vue'
import { useDeviationStore } from '@/stores/deviation'

const props = defineProps<{
  temperatureZones: TemperatureZone[]
}>()

const emit = defineEmits<{
  (event: 'created', log: TemperatureLog): void
}>()

const deviationStore = useDeviationStore()

const notes = ref<string>('')
const selectedZoneId = ref<number | null>(null)
const temperature = ref<number | null>(null)
const isSubmitting = ref(false)
const statusMessage = ref('')
const statusMessageType = ref<'success' | 'error' | ''>('')
const isDeviationModalOpen = ref(false)
const pendingTemperaturePayload = ref<TemperatureLogInput | null>(null)
const pendingCreatedLog = ref<TemperatureLog | null>(null)

const selectedZone = computed(
  () => props.temperatureZones.find((zone) => zone.id === selectedZoneId.value) ?? null,
)

function buildPayload(): TemperatureLogInput | null {
  if (!selectedZoneId.value || temperature.value === null) {
    return null
  }

  return {
    temperatureZoneId: selectedZoneId.value,
    temperatureCelsius: temperature.value,
    note: notes.value.trim().length > 0 ? notes.value.trim() : null,
  }
}

function isOutsideAllowedRange(zone: TemperatureZone, measuredTemperature: number) {
  return measuredTemperature < zone.lowerLimitCelsius || measuredTemperature > zone.upperLimitCelsius
}

function resetTemperatureForm() {
  selectedZoneId.value = null
  temperature.value = null
  notes.value = ''
}

function clearDeviationFlowState() {
  isDeviationModalOpen.value = false
  pendingTemperaturePayload.value = null
  pendingCreatedLog.value = null
  deviationStore.error = null
}

function seedDeviationForm(zone: TemperatureZone, measuredTemperature: number, note: string) {
  const today = new Date().toISOString().slice(0, 10)
  const noteLine = note ? ` Notes: ${note}` : ''

  deviationStore.resetForm()
  deviationStore.patchForm({
    title: `${zone.name} temperature deviation`,
    category: 'TEMPERATURE',
    severity: 'HIGH',
    module: 'IK_FOOD',
    status: 'OPEN',
    reportedDate: today,
    issueDescription: `Recorded ${measuredTemperature}°C in ${zone.name}. Allowed range is ${zone.lowerLimitCelsius}°C to ${zone.upperLimitCelsius}°C.${noteLine}`,
  })
}

async function submitNormalTemperatureLog(payload: TemperatureLogInput) {
  statusMessage.value = ''
  statusMessageType.value = ''
  isSubmitting.value = true

  try {
    const createdLog = await createTemperatureLog(payload)
    emit('created', createdLog)
    resetTemperatureForm()
    statusMessageType.value = 'success'
    statusMessage.value = 'Temperature log created successfully.'
  } catch (error) {
    statusMessageType.value = 'error'
    statusMessage.value =
      error instanceof Error ? error.message : 'Failed to create temperature log.'
    console.error('Failed to create temperature log', error)
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
      'The temperature log has already been saved. Complete the deviation before closing this dialog.'
    return
  }

  clearDeviationFlowState()
}

async function createLog() {
  const payload = buildPayload()

  if (!payload || !selectedZone.value || temperature.value === null) {
    statusMessageType.value = 'error'
    statusMessage.value = 'Please select a storage unit and enter a temperature.'
    return
  }

  if (isOutsideAllowedRange(selectedZone.value, temperature.value)) {
    statusMessage.value = ''
    statusMessageType.value = ''
    pendingTemperaturePayload.value = payload
    pendingCreatedLog.value = null
    seedDeviationForm(selectedZone.value, temperature.value, notes.value.trim())
    isDeviationModalOpen.value = true
    return
  }

  await submitNormalTemperatureLog(payload)
}

async function submitTemperatureDeviation() {
  if (!pendingTemperaturePayload.value || !deviationStore.validateForm()) {
    return
  }

  statusMessage.value = ''
  statusMessageType.value = ''
  isSubmitting.value = true

  try {
    let createdLog = pendingCreatedLog.value

    if (!createdLog) {
      createdLog = await createTemperatureLog(pendingTemperaturePayload.value)
      pendingCreatedLog.value = createdLog
    }

    const createdDeviation = await deviationStore.createDeviation({ logId: createdLog.id })

    if (!createdDeviation) {
      statusMessageType.value = 'error'
      statusMessage.value =
        'The temperature log was saved, but the deviation still needs to be completed.'
      return
    }

    emit('created', {
      ...createdLog,
      deviationCreated: true,
    })
    resetTemperatureForm()
    clearDeviationFlowState()
    statusMessageType.value = 'success'
    statusMessage.value = 'Temperature log and deviation created successfully.'
  } catch (error) {
    statusMessageType.value = 'error'
    statusMessage.value =
      error instanceof Error
        ? error.message
        : 'Failed to create the temperature log for this deviation.'
    console.error('Failed to submit out-of-range temperature flow', error)
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <InfoCard
    class="info-card"
    title="Create Temperature Log"
    :icon="Edit2"
    iconBackgroundColor="var(--icon-bg-purple)"
    iconColor="var(--icon-stroke-purple)"
  >
    <div class="input-field">
      <p class="subtext">Storage Unit</p>
      <select v-model.number="selectedZoneId" class="deviation-filter" aria-label="Select storage unit">
        <option :value="null" selected disabled>Select Storage Unit</option>
        <option v-for="zone in temperatureZones" :key="zone.id" :value="zone.id">
          {{ zone.name }}
        </option>
      </select>
    </div>

    <div v-if="selectedZone" class="zone-range">
      Allowed range: {{ selectedZone.lowerLimitCelsius }}°C to {{ selectedZone.upperLimitCelsius }}°C
    </div>

    <div class="input-field">
      <p class="subtext">Temperature (°C)</p>
      <input
        v-model.number="temperature"
        class="temperature-input"
        type="number"
        placeholder="Enter temperature"
      />
    </div>

    <div class="input-field">
      <p class="subtext">Notes (Optional)</p>
      <input v-model="notes" type="text" placeholder="Additional notes" />
    </div>

    <button class="create-btn" :disabled="isSubmitting" @click="createLog">
      {{ isSubmitting ? 'Submitting...' : 'Create Log' }}
    </button>

    <p v-if="statusMessage" class="status-message" :class="statusMessageType">
      {{ statusMessage }}
    </p>
  </InfoCard>

  <div v-if="isDeviationModalOpen" class="overlay-backdrop" @click.self="closeDeviationModal">
    <div class="deviation-dialog">
      <div class="dialog-header">
        <div class="dialog-copy">
          <div class="dialog-badge">
            <AlertTriangle :size="16" aria-hidden="true" />
            <span>Out-of-range reading</span>
          </div>
          <h2>Complete the deviation before saving this temperature log</h2>
          <p>
            This temperature is outside the configured range for the selected zone. Fill in the
            rest of the deviation details to continue.
          </p>
        </div>

        <button class="close-btn" type="button" :disabled="isSubmitting" @click="closeDeviationModal">
          <X :size="18" aria-hidden="true" />
        </button>
      </div>

      <DeviationForm
        lockedCategory="TEMPERATURE"
        lockedModule="IK_FOOD"
        title="Complete Temperature Deviation"
        submitLabel="Save Log and Deviation"
        footerText="The temperature log will be saved together with this completed deviation."
        :showInternalTracking="false"
        :useExternalSubmit="true"
        :isSubmitting="isSubmitting"
        maxCardHeight="calc(100dvh - 120px)"
        @submit="submitTemperatureDeviation"
      />
    </div>
  </div>
</template>

<style scoped>
.info-card {
  width: 100%;
  min-width: 0;
}

.input-field {
  display: flex;
  flex-direction: column;
  margin-bottom: 15px;
  gap: 10px;
}

.deviation-filter {
  width: 100%;
}

.subtext {
  font-size: 0.9em;
  color: var(--text-secondary);
  margin-bottom: 5px;
}

.temperature-input {
  width: 100%;
}

.zone-range {
  margin: -4px 0 12px;
  font-size: 13px;
  color: var(--text-secondary);
}

.create-btn {
  margin-top: 10px;
  width: 100%;
  cursor: pointer;
}

.status-message {
  margin-top: 12px;
  font-size: 0.9rem;
}

.status-message.success {
  color: #00a45f;
}

.status-message.error {
  color: var(--cta-red);
}

@media (max-width: 640px) {
  .info-card {
    max-width: 100%;
  }
}
</style>
