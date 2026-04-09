<script setup lang="ts">
import InfoCard from '@/components/ui/InfoCard.vue'
import type { TemperatureLog, TemperatureLogInput } from '@/interfaces/TemperatureLog.interface'
import type { TemperatureZone } from '@/interfaces/TemperatureZone.interface'
import { createTemperatureLog } from '@/services/temperatureLog'
import { ref } from 'vue'
import { Edit2 } from '@lucide/vue'

const props = defineProps<{
  temperatureZones: TemperatureZone[]
}>()

const emit = defineEmits<{
  (event: 'created', log: TemperatureLog): void
}>()

const notes = ref<string>('')
const temperatureZoneSelected = ref<number | null>(null)
const selectedZoneId = ref<number | null>(null)
const temperature = ref<number | null>(null)
const isSubmitting = ref(false)
const statusMessage = ref('')
const statusMessageType = ref<'success' | 'error' | ''>('')

async function createLog() {
  if (!selectedZoneId.value || temperature.value === null) {
    statusMessageType.value = 'error'
    statusMessage.value = 'Please select a storage unit and enter a temperature.'
    return
  }

  statusMessage.value = ''
  statusMessageType.value = ''

  const payload: TemperatureLogInput = {
    temperatureZoneId: selectedZoneId.value,
    temperatureCelsius: temperature.value,
    note: notes.value.trim().length > 0 ? notes.value.trim() : null,
  }

  isSubmitting.value = true

  try {
    const createdLog = await createTemperatureLog(payload)
    emit('created', createdLog)

    temperatureZoneSelected.value = null
    selectedZoneId.value = null
    temperature.value = null
    notes.value = ''
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

function setSelectedZone() {
  const selectedZone = props.temperatureZones.find(
    (zone) => zone.id === temperatureZoneSelected.value,
  )

  if (selectedZone) {
    selectedZoneId.value = selectedZone.id
  } else {
    selectedZoneId.value = null
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
      <select
        class="deviation-filter"
        aria-label="Filter deviations"
        v-model.number="temperatureZoneSelected"
        @change="setSelectedZone"
      >
        <option :value="null" selected disabled>Select Storage Unit</option>
        <option v-for="zone in temperatureZones" :key="zone.id" :value="zone.id">
          {{ zone.name }}
        </option>
      </select>
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
      {{ isSubmitting ? 'Creating...' : 'Create Log' }}
    </button>

    <p v-if="statusMessage" class="status-message" :class="statusMessageType">
      {{ statusMessage }}
    </p>
  </InfoCard>
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
