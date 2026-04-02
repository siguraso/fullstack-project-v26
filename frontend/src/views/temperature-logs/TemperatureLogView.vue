<script setup lang="ts">
import type { TemperatureZone } from '@/types/temperature-zone'
import { computed, nextTick, ref } from 'vue'
import CreateTemperatureLog from './components/CreateTemperatureLog.vue'
import CreateTemperatureZone from './components/CreateTemperatureZone.vue'
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
const isCreateZoneOverlayOpen = ref(false)
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

function openCreateZoneOverlay() {
  isCreateZoneOverlayOpen.value = true
}

function closeCreateZoneOverlay() {
  isCreateZoneOverlayOpen.value = false
}

function createZone(newZone: Omit<TemperatureZone, 'id'>) {
  const nextId = zones.value.length ? Math.max(...zones.value.map((zone) => zone.id)) + 1 : 1
  zones.value = [...zones.value, { id: nextId, ...newZone }]
  closeCreateZoneOverlay()
}
</script>

<template>
  <div class="temperature-log-content">
    <header>
      <h1>Temperature Logs</h1>
    </header>

    <div class="top-row">
      <CreateTemperatureLog class="create-log" />
      <TemperatureZoneOverview
        class="temperature-zone-overview"
        :zones="zones"
        @edit-zone="openEditZoneOverlay"
        @create-zone="openCreateZoneOverlay"
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

    <div
      v-if="isCreateZoneOverlayOpen"
      class="overlay-backdrop"
      @click.self="closeCreateZoneOverlay"
    >
      <CreateTemperatureZone @close="closeCreateZoneOverlay" @create="createZone" />
    </div>
  </div>

    <TemperatureLogHistory />

    <div v-if="overlayZone" class="overlay-backdrop" @click.self="closeEditZoneOverlay">
      <EditTemperatureZone
        :zone="overlayZone"
        @close="closeEditZoneOverlay"
        @save="saveZoneChanges"
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
