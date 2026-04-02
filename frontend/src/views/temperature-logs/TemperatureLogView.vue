<script setup lang="ts">
import ViewHeader from '@/components/ui/ViewHeader.vue'
import type { TemperatureZone } from '@/types/temperature-zone'
import { computed, nextTick, ref } from 'vue'
import CreateTemperatureLog from './components/CreateTemperatureLog.vue'
import TemperatureZoneOverview from './components/TemperatureZoneOverview.vue'
import TemperatureLogHistory from './components/TemperatureLogHistory.vue'
import EditTemperatureZone from './components/EditTemperatureZone.vue'

const zones = ref<TemperatureZone[]>(
  Array.from({ length: 10 }, (_, index) => ({
    id: index + 1,
    name: `Zone ${index + 1}`,
    lowerLimitCelcius: Math.floor(Math.random() * 10) - 5,
    upperLimitCelcius: Math.floor(Math.random() * 10) + 5,
  })),
)

const isEditZoneOverlayOpen = ref(false)
const selectedZone = ref<TemperatureZone | null>(null)
const overlayZone = computed(() => (isEditZoneOverlayOpen.value ? selectedZone.value : null))

function openEditZoneOverlay(zone: TemperatureZone) {
  selectedZone.value = zone
  isEditZoneOverlayOpen.value = true
}

async function closeEditZoneOverlay() {
  isEditZoneOverlayOpen.value = false
  await nextTick()
  selectedZone.value = null
}

async function saveZoneChanges(updatedZone: TemperatureZone) {
  const zoneIndex = zones.value.findIndex((zone) => zone.id === updatedZone.id)
  if (zoneIndex === -1) {
    return
  }

  zones.value[zoneIndex] = updatedZone
  selectedZone.value = updatedZone
  await closeEditZoneOverlay()
}

async function deleteZone(zoneId: number) {
  zones.value = zones.value.filter((zone) => zone.id !== zoneId)
  await closeEditZoneOverlay()
}
</script>

<template>
  <div class="temperature-log-content">
    <header>
      <ViewHeader title="Temperature Logs" />
    </header>

    <div class="top-row">
      <CreateTemperatureLog class="create-log" />
      <TemperatureZoneOverview
        class="temperature-zone-overview"
        :zones="zones"
        @edit-zone="openEditZoneOverlay"
      />
    </div>

    <TemperatureLogHistory />

    <div v-if="overlayZone" class="overlay-backdrop" @click.self="closeEditZoneOverlay">
      <EditTemperatureZone
        :zone="overlayZone"
        @close="closeEditZoneOverlay"
        @save="saveZoneChanges"
        @delete="deleteZone"
      />
    </div>
  </div>
</template>

<style scoped>
.temperature-log-content {
  position: relative;
}

.top-row {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 1rem;
  margin-bottom: 2rem;
}

.temperature-zone-overview {
  align-self: start;
}

.create-log {
  align-self: start;
}

.overlay-backdrop {
  position: fixed;
  inset: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  background-color: rgba(0, 0, 0, 0.5);
}
</style>
