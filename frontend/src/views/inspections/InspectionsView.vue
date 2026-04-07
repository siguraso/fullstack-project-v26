<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getInspectionLogs, type InspectionLog } from '@/services/inspection'
import { Download } from '@lucide/vue'

const logs = ref<InspectionLog[]>([])
const loading = ref(true)

async function loadLogs() {
  try {
    logs.value = await getInspectionLogs()
  } finally {
    loading.value = false
  }
}

onMounted(loadLogs)

// 🔄 senere kan du legge polling her
// setInterval(loadLogs, 10000)

function formatDate(date: string) {
  return new Date(date).toLocaleString()
}

function exportJson() {
  const blob = new Blob([JSON.stringify(logs.value, null, 2)], {
    type: 'application/json',
  })

  const url = URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'inspection-logs.json'
  a.click()
  URL.revokeObjectURL(url)
}
</script>

<template>
  <div class="inspections-view">
    <div class="header-row">
      <h1>Inspection Logs</h1>

      <button class="export-btn" @click="exportJson">
        <Download :size="16" />
        Export
      </button>
    </div>

    <div class="table">
      <div class="table-head">
        <span>Type</span>
        <span>Title</span>
        <span>Status</span>
        <span>Actor</span>
        <span>Date</span>
      </div>

      <TransitionGroup name="row" tag="div" class="table-body">
        <div
          v-for="log in logs"
          :key="log.type + log.referenceId"
          class="table-row"
          :class="log.status?.toLowerCase()"
        >
          <span class="type">{{ log.type }}</span>

          <div>
            <strong>{{ log.title }}</strong>
            <p v-if="log.description">{{ log.description }}</p>
          </div>

          <span class="status" :class="log.status?.toLowerCase()">
            {{ log.status || '-' }}
          </span>

          <span>{{ log.actor || 'System' }}</span>

          <span>{{ formatDate(log.timestamp) }}</span>
        </div>
      </TransitionGroup>

      <div v-if="!loading && logs.length === 0" class="empty">No inspection logs yet</div>
    </div>
  </div>
</template>

<style scoped>
.inspections-view {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* HEADER */
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.export-btn {
  color: black;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 8px;
  background: var(--bg);
  cursor: pointer;
}

/* TABLE */
.table {
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid var(--stroke);
}

.table-head,
.table-row {
  display: grid;
  grid-template-columns: 120px 2fr 120px 150px 180px;
  gap: 12px;
  padding: 14px 16px;
}

.table-head {
  font-size: 12px;
  text-transform: uppercase;
  color: var(--text-secondary);
  background: var(--bg-secondary);
}

.table-row.open {
  background: #fff5f5;
}

.table-row.resolved {
  background: #f0fff4;
}

.table-row strong {
  display: block;
}

.table-row p {
  margin: 2px 0 0;
  font-size: 12px;
  color: var(--text-secondary);
}

/* STATUS */
.status {
  font-weight: 600;
}

.status.open {
  color: #c92a2a;
}

.status.resolved {
  color: #2b8a3e;
}

/* EMPTY */
.empty {
  padding: 20px;
  text-align: center;
  color: var(--text-secondary);
}

/* ANIMATION */
.row-enter-active,
.row-leave-active {
  transition: all 0.25s ease;
}

.row-enter-from {
  opacity: 0;
  transform: translateY(6px);
}

.row-leave-to {
  opacity: 0;
  transform: translateY(-6px);
}
</style>
