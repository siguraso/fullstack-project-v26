<script setup lang="ts">
import { ref } from 'vue'
import { X, Download } from '@lucide/vue'
import { exportInspectionPdf } from '@/services/inspection'
import type { InspectionExportFilter } from '@/interfaces/Inspection.interface'

const emit = defineEmits<{ close: [] }>()

const types = ref<string[]>([])
const severities = ref<string[]>([])
const statuses = ref<string[]>([])
const dateFrom = ref('')
const dateTo = ref('')
const loading = ref(false)
const error = ref('')

const typeOptions = [
  { value: 'DEVIATION', label: 'Deviations' },
  { value: 'TEMPERATURE', label: 'Temperature Logs' },
  { value: 'ALCOHOL', label: 'Alcohol Logs' },
]

const severityOptions = [
  { value: 'CRITICAL', label: 'Critical' },
  { value: 'HIGH', label: 'High' },
  { value: 'MEDIUM', label: 'Medium' },
  { value: 'LOW', label: 'Low' },
]

const statusOptions = [
  { value: 'OPEN', label: 'Open' },
  { value: 'IN_PROGRESS', label: 'In Progress' },
  { value: 'RESOLVED', label: 'Resolved' },
  { value: 'OK', label: 'OK' },
  { value: 'WARNING', label: 'Warning' },
  { value: 'CRITICAL', label: 'Critical' },
]

function toggleItem(list: string[], value: string) {
  const idx = list.indexOf(value)
  if (idx === -1) list.push(value)
  else list.splice(idx, 1)
}

async function handleExport() {
  loading.value = true
  error.value = ''

  const filter: InspectionExportFilter = {
    types: types.value.length ? [...types.value] : undefined,
    severities: severities.value.length ? [...severities.value] : undefined,
    statuses: statuses.value.length ? [...statuses.value] : undefined,
    dateFrom: dateFrom.value || undefined,
    dateTo: dateTo.value || undefined,
  }

  try {
    const blob = await exportInspectionPdf(filter)
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'inspection-report.pdf'
    a.click()
    URL.revokeObjectURL(url)
    emit('close')
  } catch {
    error.value = 'Failed to generate PDF. Please try again.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="modal-backdrop" @click.self="emit('close')">
    <div class="modal">
      <div class="modal-header">
        <h2>Export to PDF</h2>
        <button class="close-btn" @click="emit('close')">
          <X :size="18" />
        </button>
      </div>

      <div class="modal-body">
        <!-- Log types -->
        <div class="filter-group">
          <span class="group-label">Log Types</span>
          <p class="group-hint">Leave empty to include all types</p>
          <div class="chip-row">
            <button
              v-for="opt in typeOptions"
              :key="opt.value"
              class="chip"
              :class="{ active: types.includes(opt.value) }"
              @click="toggleItem(types, opt.value)"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>

        <!-- Severity (applies to deviations) -->
        <div class="filter-group">
          <span class="group-label">Severity <span class="group-sub">(deviations)</span></span>
          <p class="group-hint">Leave empty to include all severities</p>
          <div class="chip-row">
            <button
              v-for="opt in severityOptions"
              :key="opt.value"
              class="chip"
              :class="['chip-severity-' + opt.value.toLowerCase(), { active: severities.includes(opt.value) }]"
              @click="toggleItem(severities, opt.value)"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>

        <!-- Status -->
        <div class="filter-group">
          <span class="group-label">Status</span>
          <p class="group-hint">Leave empty to include all statuses</p>
          <div class="chip-row">
            <button
              v-for="opt in statusOptions"
              :key="opt.value"
              class="chip"
              :class="{ active: statuses.includes(opt.value) }"
              @click="toggleItem(statuses, opt.value)"
            >
              {{ opt.label }}
            </button>
          </div>
        </div>

        <!-- Date range -->
        <div class="filter-group">
          <span class="group-label">Date Range</span>
          <div class="date-row">
            <div class="date-field">
              <label for="export-date-from">From</label>
              <input id="export-date-from" type="date" v-model="dateFrom" />
            </div>
            <div class="date-field">
              <label for="export-date-to">To</label>
              <input id="export-date-to" type="date" v-model="dateTo" />
            </div>
          </div>
        </div>

        <p v-if="error" class="error-msg">{{ error }}</p>
      </div>

      <div class="modal-footer">
        <button class="btn-secondary" @click="emit('close')">Cancel</button>
        <button class="btn-primary" :disabled="loading" @click="handleExport">
          <Download :size="15" />
          {{ loading ? 'Generating…' : 'Export PDF' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: var(--bg-secondary);
  border-radius: 14px;
  width: 100%;
  max-width: 520px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.18);
}

/* HEADER */
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px 0;
}

.modal-header h2 {
  font-size: 18px;
  font-weight: 700;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--text-secondary);
  padding: 4px;
  display: flex;
  align-items: center;
  border-radius: 6px;
}

.close-btn:hover {
  background: var(--border);
}

/* BODY */
.modal-body {
  padding: 20px 24px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.group-label {
  font-size: 13px;
  font-weight: 600;
}

.group-sub {
  font-weight: 400;
  color: var(--text-secondary);
  font-size: 11px;
}

.group-hint {
  font-size: 11px;
  color: var(--text-secondary);
  margin-top: -4px;
}

/* CHIPS */
.chip-row {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  padding: 5px 12px;
  border-radius: 20px;
  border: 1px solid var(--border);
  background: var(--bg-primary, #fff);
  font-size: 12px;
  cursor: pointer;
  transition: all 0.15s;
  color: var(--text-primary);
}

.chip:hover {
  border-color: #555;
}

.chip.active {
  background: var(--neutral);
  color: var(--bg);
  border-color: var(--neutral);
}

/* Severity chips when active */
.chip-severity-critical.active { background: #991b1b; border-color: #991b1b; }
.chip-severity-high.active     { background: #9a3412; border-color: #9a3412; }
.chip-severity-medium.active   { background: #854d0e; border-color: #854d0e; }
.chip-severity-low.active      { background: #166534; border-color: #166534; }

/* DATES */
.date-row {
  display: flex;
  gap: 12px;
}

.date-field {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.date-field label {
  font-size: 11px;
  color: var(--text-secondary);
}

.date-field input {
  padding: 7px 10px;
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 13px;
  background: var(--bg-primary, #fff);
  color: var(--text-primary);
}

/* ERROR */
.error-msg {
  color: #991b1b;
  font-size: 12px;
}

/* FOOTER */
.modal-footer {
  padding: 16px 24px;
  border-top: 1px solid var(--border);
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-secondary {
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid var(--border);
  background: transparent;
  cursor: pointer;
  font-size: 13px;
  color: var(--text-primary);
}

.btn-secondary:hover {
  background: var(--border);
}

.btn-primary {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 8px;
  border: none;
  background: #1a1a2e;
  color: #fff;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  transition: opacity 0.15s;
}

.btn-primary:hover:not(:disabled) {
  opacity: 0.85;
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
