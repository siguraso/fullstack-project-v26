<script setup lang="ts">
import type { TemperatureLog } from '@/interfaces/TemperatureLog.interface'
import type { TemperatureZone } from '@/interfaces/TemperatureZone.interface'
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import CreateTemperatureLog from './components/CreateTemperatureLog.vue'
import CreateTemperatureZone from './components/CreateTemperatureZone.vue'
import TemperatureZoneOverview from './components/TemperatureZoneOverview.vue'
import TemperatureLogHistory from './components/TemperatureLogHistory.vue'
import EditTemperatureZone from './components/EditTemperatureZone.vue'
import TemperatureLogDetailsDialog from './components/TemperatureLogDetailsDialog.vue'
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
const isViewLogOverlayOpen = ref(false)
const selectedZone = ref<TemperatureZone | null>(null)
const selectedLog = ref<TemperatureLog | null>(null)
const overlayZone = computed(() => (isEditZoneOverlayOpen.value ? selectedZone.value : null))
const overlayLog = computed(() => (isViewLogOverlayOpen.value ? selectedLog.value : null))
const isAnyOverlayOpen = computed(
  () => isEditZoneOverlayOpen.value || isCreateZoneOverlayOpen.value || isViewLogOverlayOpen.value,
)

function lockBodyScroll() {
  if (typeof window === 'undefined' || typeof document === 'undefined') {
    return
  }

  document.documentElement.style.overflow = 'hidden'
  document.body.style.overflow = 'hidden'
}

function unlockBodyScroll() {
  if (typeof window === 'undefined' || typeof document === 'undefined') {
    return
  }

  document.documentElement.style.overflow = ''
  document.body.style.overflow = ''
}

watch(
  isAnyOverlayOpen,
  (isOpen) => {
    if (isOpen) {
      lockBodyScroll()
      return
    }

    unlockBodyScroll()
  },
  { immediate: true },
)

onBeforeUnmount(() => {
  unlockBodyScroll()
})

function handleTemperatureLogCreated(log: TemperatureLog) {
  logs.value = [log, ...logs.value]
}

function openTemperatureLogDetails(log: TemperatureLog) {
  selectedLog.value = log
  isViewLogOverlayOpen.value = true
}

function closeViewLogOverlay() {
  isViewLogOverlayOpen.value = false
  selectedLog.value = null
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
      <h1>Temperature Logs</h1>
    </header>

    <p v-if="zoneLoadError" class="error-container">{{ zoneLoadError }}</p>
    <p v-if="logLoadError" class="error-container">{{ logLoadError }}</p>

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

    <TemperatureLogHistory
      :temperatureZones="zones"
      :temperatureLogs="logs"
      @view-log="openTemperatureLogDetails"
    />

    <div v-if="overlayLog" class="overlay-backdrop" @click.self="closeViewLogOverlay">
      <TemperatureLogDetailsDialog :log="overlayLog" :zones="zones" @close="closeViewLogOverlay" />
    </div>

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

.top-row {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 1rem;
  margin-bottom: 2rem;
}

.top-row > * {
  min-width: 0;
  width: 100%;
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
  min-height: 100dvh;
  z-index: 1000;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: 1rem;
  background-color: rgba(0, 0, 0, 0.5);
}

.error-container {
  margin: 0 0 14px;
  border: 1px solid var(--cta-red);
  background: #fff0f0;
  color: var(--cta-red);
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 13px;
}

@media (max-width: 960px) {
  .top-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .temperature-log-content {
    overflow-x: hidden;
  }

  .overlay-backdrop {
    align-items: flex-start;
    overflow-y: auto;
    padding: 12px;
  }
}
</style>
