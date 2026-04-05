<script setup lang="ts">
import ViewHeader from '@/components/ui/ViewHeader.vue'
import type { TemperatureLog } from '@/interfaces/TemperatureLog.interface'
import type { TemperatureZone } from '@/interfaces/TemperatureZone.interface'
import { computed, nextTick, onMounted, ref } from 'vue'
import CreateTemperatureLog from './components/CreateTemperatureLog.vue'
import CreateTemperatureZone from './components/CreateTemperatureZone.vue'
import TemperatureZoneOverview from './components/TemperatureZoneOverview.vue'
import TemperatureLogHistory from './components/TemperatureLogHistory.vue'
import EditTemperatureZone from './components/EditTemperatureZone.vue'
import { fetchTemperatureLogs } from '@/services/temperatureLog'
import {
  deleteTemperatureZone,
  editTemperatureZone,
  getTemperatureZones,
} from '@/services/temperatureZone'
import { addTemperatureZone } from '@/services/temperatureZone'

const zones = ref<TemperatureZone[]>([])
const logs = ref<TemperatureLog[]>([])
const isLoadingZones = ref(false)
const isLoadingLogs = ref(false)
const zoneLoadError = ref('')
const logLoadError = ref('')

async function loadZones() {
  isLoadingZones.value = true
  zoneLoadError.value = ''

  try {
    zones.value = await getTemperatureZones()
  } catch (error) {
    zoneLoadError.value =
      error instanceof Error ? error.message : 'Failed to fetch temperature zones'
    console.error('Failed to fetch temperature zones', error)
  } finally {
    isLoadingZones.value = false
  }
}

async function loadLogs() {
  isLoadingLogs.value = true
  logLoadError.value = ''

  try {
    logs.value = await fetchTemperatureLogs()
  } catch (error) {
    logLoadError.value = error instanceof Error ? error.message : 'Failed to fetch temperature logs'
    console.error('Failed to fetch temperature logs', error)
  } finally {
    isLoadingLogs.value = false
  }
}

onMounted(() => {
  void loadZones()
  void loadLogs()
})

const isEditZoneOverlayOpen = ref(false)
const isCreateZoneOverlayOpen = ref(false)
const selectedZone = ref<TemperatureZone | null>(null)
const overlayZone = computed(() => (isEditZoneOverlayOpen.value ? selectedZone.value : null))

function handleTemperatureLogCreated(log: TemperatureLog) {
  logs.value = [log, ...logs.value]
}

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
  editTemperatureZone(updatedZone.id, updatedZone)
    .then((responseZone) => {
      const zoneIndex = zones.value.findIndex((zone) => zone.id === updatedZone.id)
      if (zoneIndex === -1) {
        return
      }

      zones.value[zoneIndex] = responseZone
      selectedZone.value = responseZone
    })
    .catch((error) => {
      console.error('Failed to update temperature zone', error)
    })
    .finally(() => {
      closeEditZoneOverlay()
    })
}

async function deleteZone(zoneId: number) {
  deleteTemperatureZone(zoneId)
    .then(() => {
      zones.value = zones.value.filter((zone) => zone.id !== zoneId)
    })
    .catch((error) => {
      console.error('Failed to delete temperature zone', error)
    })
    .finally(() => {
      closeEditZoneOverlay()
    })

  await closeEditZoneOverlay()
}

async function createZone(newZone: Omit<TemperatureZone, 'id'>) {
  addTemperatureZone(newZone)
    .then((createdZone) => {
      zones.value.push(createdZone)
    })
    .catch((error) => {
      console.error('Failed to create temperature zone', error)
    })
    .finally(() => {
      closeCreateZoneOverlay()
    })
}

function openCreateZoneOverlay() {
  isCreateZoneOverlayOpen.value = true
}

function closeCreateZoneOverlay() {
  isCreateZoneOverlayOpen.value = false
}
</script>

<template>
  <div class="temperature-log-content">
    <header>
      <ViewHeader title="Temperature Logs" />
    </header>

    <p v-if="isLoadingZones" class="status-message">Loading temperature zones...</p>
    <p v-else-if="zoneLoadError" class="status-message error">{{ zoneLoadError }}</p>
    <p v-if="isLoadingLogs" class="status-message">Loading temperature logs...</p>
    <p v-else-if="logLoadError" class="status-message error">{{ logLoadError }}</p>

    <div class="top-row">
      <CreateTemperatureLog
        class="create-log"
        :temperatureZones="zones"
        @created="handleTemperatureLogCreated"
      />
      <TemperatureZoneOverview
        class="temperature-zone-overview"
        :zones="zones"
        @edit-zone="openEditZoneOverlay"
        @create-zone="openCreateZoneOverlay"
      />
    </div>

    <TemperatureLogHistory :temperatureZones="zones" :temperatureLogs="logs" />

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
</template>

<style scoped>
.temperature-log-content {
  position: relative;
}

.status-message {
  margin: 0.75rem 0 1rem;
}

.status-message.error {
  color: #b42222;
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
