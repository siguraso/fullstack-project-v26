<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import AlcoholLogForm from './components/AlcoholLogForm.vue'
import AlcoholLogGuide from './components/AlcoholLogGuide.vue'
import AlcoholLogTable from './components/AlcoholLogTable.vue'
import AlcoholLogDetailsDialog from './components/AlcoholLogDetailsDialog.vue'
import { fetchAlcoholLogs } from '@/services/alcoholLog'
import type { AlcoholLog } from '@/interfaces/AlcoholLog.interface'

const route = useRoute()
const logs = ref<AlcoholLog[]>([])
const isLoadingLogs = ref(false)
const error = ref('')

const isDetailsOpen = ref(false)
const selectedLog = ref<AlcoholLog | null>(null)
const formPreset = computed(() =>
  typeof route.query.preset === 'string' ? route.query.preset : null,
)

async function loadLogs() {
  isLoadingLogs.value = true
  error.value = ''

  try {
    logs.value = await fetchAlcoholLogs()
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Failed to fetch alcohol logs'
    console.error('Failed to fetch alcohol logs', err)
  } finally {
    isLoadingLogs.value = false
  }
}

onMounted(() => {
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

function handleAlcoholLogCreated(log: AlcoholLog) {
  logs.value = [log, ...logs.value]
}

function openDetails(log: AlcoholLog) {
  selectedLog.value = log
  isDetailsOpen.value = true
}

function closeDetails() {
  isDetailsOpen.value = false
  selectedLog.value = null
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
