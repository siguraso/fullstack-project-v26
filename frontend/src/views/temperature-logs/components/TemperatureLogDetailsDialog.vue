<script setup lang="ts">
import type { TemperatureLog } from '@/interfaces/TemperatureLog.interface'
import type { TemperatureZone } from '@/interfaces/TemperatureZone.interface'
import InfoCard from '@/components/ui/InfoCard.vue'
import { ScrollText } from '@lucide/vue'
import { computed } from 'vue'

const props = defineProps<{
  log: TemperatureLog | null
  zones: TemperatureZone[]
}>()

const emit = defineEmits<{
  (event: 'close'): void
}>()

const resolvedZoneName = computed(() => {
  if (!props.log) {
    return 'Not set'
  }

  if (props.log.temperatureZoneName) {
    return props.log.temperatureZoneName
  }

  return (
    props.zones.find((zone) => zone.id === props.log?.temperatureZoneId)?.name ?? 'Unknown zone'
  )
})

function formatDate(date?: string): string {
  if (!date) return 'Not available'

  const parsedDate = new Date(date)

  if (Number.isNaN(parsedDate.getTime())) {
    return date
  }

  return parsedDate.toLocaleString('en-GB', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

function formatOptional(value: unknown): string {
  if (value === null || value === undefined || value === '') {
    return 'Not set'
  }

  return String(value)
}
</script>

<template>
  <InfoCard
    v-if="log"
    class="dialog-content"
    title="Temperature Log Details"
    :icon="ScrollText"
    iconBackgroundColor="var(--neutral)"
    iconColor="white"
    :addToHeader="true"
  >
    <template #extra-header-content>
      <button type="button" class="close-btn" @click="emit('close')">Close</button>
    </template>

    <div class="details-grid">
      <div class="detail-item">
        <span class="label">Temperature Zone: </span>
        <span class="value">{{ resolvedZoneName }}</span>
      </div>

      <div class="detail-item">
        <span class="label">Temperature (°C): </span>
        <span class="value">{{ log.temperatureCelsius }}</span>
      </div>

      <div class="detail-item">
        <span class="label">Timestamp</span>
        <span class="value">{{ formatDate(log.timestamp) }}</span>
      </div>

      <div class="detail-item">
        <span class="label">Recorded By ID</span>
        <span class="value">{{ formatOptional(log.recordedById) }}</span>
      </div>

      <div class="detail-item">
        <span class="label">Recorded By </span>
        <span class="value">{{ formatOptional(log.recordedByName) }}</span>
      </div>
    </div>

    <div class="description">
      <h4>Note</h4>
      <p>{{ formatOptional(log.note) }}</p>
    </div>
  </InfoCard>
</template>

<style scoped>
.dialog-content {
  width: min(780px, 92vw);
  max-height: 88vh;
  overflow: auto;
  margin: 0;
}

.close-btn {
  margin-left: auto;
  border: 1px solid var(--border);
  background: var(--bg);
  color: var(--text);
  border-radius: 8px;
  padding: 8px 12px;
  cursor: pointer;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.detail-item {
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.label {
  color: var(--text-secondary);
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.value {
  color: var(--text);
  font-weight: 600;
  overflow-wrap: anywhere;
}

.description {
  margin-top: 14px;
  border-top: 1px solid var(--border);
  padding-top: 12px;
}

.description h4 {
  margin: 0 0 8px;
  color: var(--text);
}

.description p {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.45;
  overflow-wrap: anywhere;
}

@media (max-width: 740px) {
  .dialog-content {
    width: 100%;
    max-height: calc(100vh - 24px);
  }

  .details-grid {
    grid-template-columns: 1fr;
  }

  .details-grid .detail-item {
    align-items: flex-start;
  }

  .close-btn {
    width: 100%;
    margin-top: 8px;
  }
}
</style>
