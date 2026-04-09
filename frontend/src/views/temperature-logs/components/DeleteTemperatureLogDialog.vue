<script setup lang="ts">
import Card from '@/components/ui/Card.vue'
import type { TemperatureLog } from '@/interfaces/TemperatureLog.interface'
import type { TemperatureZone } from '@/interfaces/TemperatureZone.interface'
import { computed } from 'vue'

const props = defineProps<{
  log: TemperatureLog
  zones: TemperatureZone[]
}>()

const emit = defineEmits<{
  (event: 'close'): void
  (event: 'confirm', logId: number): void
}>()

const resolvedZoneName = computed(() => {
  if (props.log.temperatureZoneName) {
    return props.log.temperatureZoneName
  }

  return props.zones.find((zone) => zone.id === props.log.temperatureZoneId)?.name ?? 'Unknown zone'
})

function closeOverlay() {
  emit('close')
}

function confirmDelete() {
  emit('confirm', props.log.id)
}
</script>

<template>
  <Card class="card">
    <template #card-header>
      <h3 class="title">Delete Temperature Log</h3>
    </template>

    <template #card-content>
      <p class="subtext">This temperature log will be permanently deleted:</p>

      <div class="details-wrap">
        <p><strong>Zone:</strong> {{ resolvedZoneName }}</p>
        <p><strong>Temperature:</strong> {{ log.temperatureCelsius }} °C</p>
        <p>
          <strong>Recorded At:</strong>
          {{
            Intl.DateTimeFormat('en-GB', {
              year: 'numeric',
              month: '2-digit',
              day: '2-digit',
              hour: '2-digit',
              minute: '2-digit',
            }).format(new Date(log.timestamp))
          }}
        </p>
      </div>

      <div class="buttons">
        <button class="close-button" @click="closeOverlay">Cancel</button>
        <button class="delete-button" @click="confirmDelete">Delete Log</button>
      </div>
    </template>
  </Card>
</template>

<style scoped>
.card {
  width: min(480px, 92vw);
  max-width: 480px;
  max-height: calc(100vh - 3rem);
  overflow: auto;
}

.title {
  margin: 0;
}

.subtext {
  margin: 0 0 12px 0;
  color: var(--text-secondary);
  font-size: 14px;
}

.details-wrap {
  margin: 0 0 10px 0;
}

.details-wrap p {
  margin: 0 0 6px 0;
}

.close-button {
  background-color: var(--bg);
  color: var(--text-primary);
  margin-top: 1.5rem;
}

.close-button:hover {
  background-color: var(--bg-hover);
}

.close-button:active {
  background-color: var(--bg-active);
}

.delete-button {
  margin-top: 1.5rem;
  width: 100%;
  background-color: var(--cta-red-btn);
  color: white;
}

.buttons {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 10px;
}

@media (max-width: 640px) {
  .buttons {
    grid-template-columns: 1fr;
  }
}
</style>
