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
import { createLogger } from '@/services/util/logger'

const zones = ref<TemperatureZone[]>([])
const logs = ref<TemperatureLog[]>([])
const isLoadingZones = ref(false)
const isLoadingLogs = ref(false)
const zoneLoadError = ref('')
const logLoadError = ref('')
const logger = createLogger('temperature-log-view')

async function loadZones() {
  logger.info('zone load started')
  isLoadingZones.value = true
  zoneLoadError.value = ''

  try {
    zones.value = await getTemperatureZones()
    logger.info('zone load succeeded', { zoneCount: zones.value.length })
  } catch (error) {
    zoneLoadError.value =
      error instanceof Error ? error.message : 'Failed to fetch temperature zones'
    logger.error('zone load failed', error)
  } finally {
    isLoadingZones.value = false
    logger.log('zone loading state updated', { loading: isLoadingZones.value })
  }
}

async function loadLogs() {
  logger.info('temperature log load started')
  isLoadingLogs.value = true
  logLoadError.value = ''

  try {
    logs.value = await fetchTemperatureLogs()
    logger.info('temperature log load succeeded', { logCount: logs.value.length })
  } catch (error) {
    logLoadError.value = error instanceof Error ? error.message : 'Failed to fetch temperature logs'
    logger.error('temperature log load failed', error)
  } finally {
    isLoadingLogs.value = false
    logger.log('temperature log loading state updated', { loading: isLoadingLogs.value })
  }
}

onMounted(() => {
  logger.info('view mounted')
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
    logger.log('overlay visibility changed', {
      isOpen,
      editZoneOpen: isEditZoneOverlayOpen.value,
      createZoneOpen: isCreateZoneOverlayOpen.value,
      viewLogOpen: isViewLogOverlayOpen.value,
    })

    if (isOpen) {
      lockBodyScroll()
      return
    }

    unlockBodyScroll()
  },
  { immediate: true },
)

onBeforeUnmount(() => {
  logger.info('view unmounted')
  unlockBodyScroll()
})

function handleTemperatureLogCreated(log: TemperatureLog) {
  logs.value = [log, ...logs.value]
  logger.info('temperature log appended', {
    logId: log.id,
    totalLogs: logs.value.length,
    deviationCreated: Boolean(log.deviationCreated),
  })
}

function openTemperatureLogDetails(log: TemperatureLog) {
  selectedLog.value = log
  isViewLogOverlayOpen.value = true
  logger.info('temperature log details opened', { logId: log.id })
}

function closeViewLogOverlay() {
  isViewLogOverlayOpen.value = false
  selectedLog.value = null
  logger.info('temperature log details closed')
}

function openEditZoneOverlay(zone: TemperatureZone) {
  selectedZone.value = zone
  isEditZoneOverlayOpen.value = true
  logger.info('temperature zone editor opened', { zoneId: zone.id, zoneName: zone.name })
}

async function closeEditZoneOverlay() {
  isEditZoneOverlayOpen.value = false
  await nextTick()
  selectedZone.value = null
  logger.info('temperature zone editor closed')
}

async function saveZoneChanges(updatedZone: TemperatureZone) {
  logger.info('temperature zone save started', {
    zoneId: updatedZone.id,
    zoneName: updatedZone.name,
  })

  editTemperatureZone(updatedZone.id, updatedZone)
    .then((responseZone) => {
      const zoneIndex = zones.value.findIndex((zone) => zone.id === updatedZone.id)
      if (zoneIndex === -1) {
        logger.warn('temperature zone save completed with missing local zone', {
          zoneId: updatedZone.id,
        })
        return
      }

      zones.value[zoneIndex] = responseZone
      selectedZone.value = responseZone
      logger.info('temperature zone save succeeded', {
        zoneId: responseZone.id,
        zoneName: responseZone.name,
      })
    })
    .catch((error) => {
      logger.error('temperature zone save failed', error, { zoneId: updatedZone.id })
    })
    .finally(() => {
      void closeEditZoneOverlay()
    })
}

async function deleteZone(zoneId: number) {
  logger.info('temperature zone delete started', { zoneId })
  deleteTemperatureZone(zoneId)
    .then(() => {
      zones.value = zones.value.filter((zone) => zone.id !== zoneId)
      logger.info('temperature zone delete succeeded', {
        zoneId,
        remainingZones: zones.value.length,
      })
    })
    .catch((error) => {
      logger.error('temperature zone delete failed', error, { zoneId })
    })
    .finally(() => {
      void closeEditZoneOverlay()
    })

  await closeEditZoneOverlay()
}

async function createZone(newZone: Omit<TemperatureZone, 'id'>) {
  logger.info('temperature zone create started', {
    zoneName: newZone.name,
    lowerLimitCelsius: newZone.lowerLimitCelsius,
    upperLimitCelsius: newZone.upperLimitCelsius,
  })

  addTemperatureZone(newZone)
    .then((createdZone) => {
      zones.value.push(createdZone)
      logger.info('temperature zone create succeeded', {
        zoneId: createdZone.id,
        totalZones: zones.value.length,
      })
    })
    .catch((error) => {
      logger.error('temperature zone create failed', error, { zoneName: newZone.name })
    })
    .finally(() => {
      closeCreateZoneOverlay()
    })
}

function openCreateZoneOverlay() {
  isCreateZoneOverlayOpen.value = true
  logger.info('temperature zone create dialog opened')
}

function closeCreateZoneOverlay() {
  isCreateZoneOverlayOpen.value = false
  logger.info('temperature zone create dialog closed')
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

    <Teleport to="body">
      <div v-if="overlayLog" class="overlay-backdrop" @click.self="closeViewLogOverlay">
        <TemperatureLogDetailsDialog
          :log="overlayLog"
          :zones="zones"
          @close="closeViewLogOverlay"
        />
      </div>
    </Teleport>

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
  align-items: center;
  justify-content: center;
  padding: 1rem;
  background-color: rgba(0, 0, 0, 0.5);
  overflow-y: auto;
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
    padding: 12px;
  }
}
</style>
