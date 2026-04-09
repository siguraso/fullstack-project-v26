<script setup lang="ts">
import { onMounted, ref } from 'vue'
import AlcoholLogForm from './components/AlcoholLogForm.vue'
import AlcoholLogGuide from './components/AlcoholLogGuide.vue'
import AlcoholLogTable from './components/AlcoholLogTable.vue'
import AlcoholLogDetailsDialog from './components/AlcoholLogDetailsDialog.vue'
import { fetchAlcoholLogs } from '@/services/alcoholLog'
import type { AlcoholLog } from '@/interfaces/AlcoholLog.interface'

const logs = ref<AlcoholLog[]>([])
const isLoadingLogs = ref(false)
const error = ref('')

const isDetailsOpen = ref(false)
const selectedLog = ref<AlcoholLog | null>(null)

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
      <AlcoholLogForm class="form-panel" @created="handleAlcoholLogCreated" />
      <AlcoholLogGuide class="guide-panel" />
    </div>

    <AlcoholLogTable
      :logs="logs"
      :isLoading="isLoadingLogs"
      @view-requested="openDetails"
    />

    <AlcoholLogDetailsDialog
      :log="selectedLog"
      :isOpen="isDetailsOpen"
      @close="closeDetails"
    />
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
  grid-template-columns: minmax(0, 1fr) minmax(280px, 340px);
  gap: 16px;
  align-items: start;
  margin-bottom: 16px;
}

.form-panel {
  width: 100%;
}

.guide-panel {
  position: sticky;
  top: 24px;
}

@media (max-width: 1250px) {
  .top-row {
    grid-template-columns: 1fr;
  }

  .guide-panel {
    position: static;
  }
}
</style>



