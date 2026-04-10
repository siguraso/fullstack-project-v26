<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import AlcoholLogForm from './components/AlcoholLogForm.vue'
import AlcoholLogGuide from './components/AlcoholLogGuide.vue'
import AlcoholLogTable from './components/AlcoholLogTable.vue'
import AlcoholLogDetailsDialog from './components/AlcoholLogDetailsDialog.vue'
import { fetchAlcoholLogs } from '@/services/alcoholLog'
import type { AlcoholLog } from '@/interfaces/AlcoholLog.interface'
import { createLogger } from '@/services/util/logger'

const route = useRoute()
const logs = ref<AlcoholLog[]>([])
const isLoadingLogs = ref(false)
const error = ref('')
const logger = createLogger('alcohol-log-view')

const isDetailsOpen = ref(false)
const selectedLog = ref<AlcoholLog | null>(null)
const formPreset = computed(() =>
  typeof route.query.preset === 'string' ? route.query.preset : null,
)

async function loadLogs() {
  logger.info('alcohol log load started', { preset: formPreset.value })
  isLoadingLogs.value = true
  error.value = ''

  try {
    logs.value = await fetchAlcoholLogs()
    logger.info('alcohol log load succeeded', { logCount: logs.value.length })
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Failed to fetch alcohol logs'
    logger.error('alcohol log load failed', err, { preset: formPreset.value })
  } finally {
    isLoadingLogs.value = false
    logger.log('alcohol log loading state updated', { loading: isLoadingLogs.value })
  }
}

onMounted(() => {
  logger.info('view mounted', { preset: formPreset.value })
  void loadLogs()
})

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
  isDetailsOpen,
  (isOpen) => {
    logger.log('details dialog visibility changed', {
      isOpen,
      selectedLogId: selectedLog.value?.id ?? null,
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

function handleAlcoholLogCreated(log: AlcoholLog) {
  logs.value = [log, ...logs.value]
  logger.info('alcohol log appended', { logId: log.id, totalLogs: logs.value.length })
}

function openDetails(log: AlcoholLog) {
  selectedLog.value = log
  isDetailsOpen.value = true
  logger.info('alcohol log details opened', { logId: log.id })
}

function closeDetails() {
  isDetailsOpen.value = false
  selectedLog.value = null
  logger.info('alcohol log details closed')
}
</script>

<template>
  <div class="page">
    <header class="page-header">
      <h1>Alcohol Compliance Logs</h1>
    </header>

    <p v-if="error" class="error-banner">{{ error }}</p>

    <div class="top-row">
      <AlcoholLogForm
        class="form-panel"
        :preset="formPreset"
        @created="handleAlcoholLogCreated"
      />
      <AlcoholLogGuide class="guide-panel" />
    </div>

    <AlcoholLogTable :logs="logs" :isLoading="isLoadingLogs" @view-requested="openDetails" />

    <AlcoholLogDetailsDialog :log="selectedLog" :isOpen="isDetailsOpen" @close="closeDetails" />
  </div>
</template>

<style scoped>
.page {
  background: var(--bg);
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 0;
}

.page-header {
  margin-bottom: 0;
}

.page-header h1 {
  color: var(--text);
}

.page-header p {
  margin: 8px 0 0;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  font-weight: 600;
  font-size: 11px;
  color: var(--text-secondary);
}

.error-banner {
  margin: 0 0 14px;
  border: 1px solid var(--cta-red);
  background: #fff0f0;
  color: var(--cta-red);
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 13px;
}

.top-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(0, 2fr);
  gap: 16px;
  align-items: stretch;
  margin-bottom: 16px;
}

.form-panel {
  width: 100%;
  height: 100%;
}

.guide-panel {
  height: 100%;
}

@media (max-width: 1250px) {
  .top-row {
    grid-template-columns: 1fr;
  }
}
</style>
