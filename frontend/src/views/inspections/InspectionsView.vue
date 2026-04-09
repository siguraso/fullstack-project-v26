<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { getInspectionStats } from '@/services/inspection'
import type { InspectionStats } from '@/interfaces/Inspection.interface'
import ExportModal from '@/components/inspection/ExportModal.vue'
import { Download } from '@lucide/vue'

import { Doughnut, Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
} from 'chart.js'

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement)

const stats = ref<InspectionStats | null>(null)
const loading = ref(true)
const showExportModal = ref(false)

async function loadStats() {
  try {
    stats.value = await getInspectionStats()
  } catch {
    stats.value = null
  } finally {
    loading.value = false
  }
}

onMounted(loadStats)

const fill = {
  neutral: 'rgba(100, 116, 139, 0.18)',
  accent: 'rgba(59, 130, 246, 0.18)',
  critical: 'rgba(220, 38, 38, 0.18)',

  open: 'rgba(255,255,255,1)',
  inProgress: 'rgba(59, 130, 246, 0.18)',
  resolved: 'rgba(100, 116, 139, 0.18)',

  ok: 'rgba(100, 116, 139, 0.18)',
  warning: 'rgba(59, 130, 246, 0.18)',

  high: 'rgba(245, 158, 11, 0.18)',
  medium: 'rgba(59, 130, 246, 0.18)',
  low: 'rgba(100, 116, 139, 0.18)',
}

const doughnutOptions = {
  responsive: true,
  maintainAspectRatio: false,
  cutout: '72%',
  plugins: {
    legend: {
      position: 'bottom' as const,
      labels: {
        padding: 12,
        font: { size: 11 },
        color: '#374151',
      },
    },
  },
}

const barOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { display: false } },
  scales: {
    y: {
      beginAtZero: true,
      ticks: { color: '#6b7280' },
      grid: { color: '#f3f4f6' },
      border: { display: false },
    },
    x: {
      ticks: { color: '#374151' },
      grid: { display: false },
      border: { display: false },
    },
  },
}

const deviationStatusChart = computed(() => ({
  labels: ['Open', 'In Progress', 'Resolved'],
  datasets: [
    {
      data: [
        stats.value?.deviationOpen ?? 0,
        stats.value?.deviationInProgress ?? 0,
        stats.value?.deviationResolved ?? 0,
      ],
      backgroundColor: [fill.open, fill.inProgress, fill.resolved],
      borderColor: ['rgb(203,213,225)', 'rgb(59,130,246)', 'rgb(100,116,139)'],
      borderWidth: 1.5,
      hoverOffset: 4,
    },
  ],
}))

const deviationSeverityChart = computed(() => ({
  labels: ['Critical', 'High', 'Medium', 'Low'],
  datasets: [
    {
      label: 'Deviations',
      data: [
        stats.value?.deviationCritical ?? 0,
        stats.value?.deviationHigh ?? 0,
        stats.value?.deviationMedium ?? 0,
        stats.value?.deviationLow ?? 0,
      ],
      backgroundColor: [fill.critical, fill.high, fill.medium, fill.low],
      borderColor: ['rgb(220,38,38)', 'rgb(245,158,11)', 'rgb(59,130,246)', 'rgb(100,116,139)'],
      borderWidth: 1.5,
      borderRadius: 6,
    },
  ],
}))

const temperatureStatusChart = computed(() => ({
  labels: ['OK', 'Warning', 'Critical'],
  datasets: [
    {
      data: [
        stats.value?.temperatureOk ?? 0,
        stats.value?.temperatureWarning ?? 0,
        stats.value?.temperatureCritical ?? 0,
      ],
      backgroundColor: [fill.ok, fill.warning, fill.critical],
      borderColor: ['rgb(100,116,139)', 'rgb(59,130,246)', 'rgb(220,38,38)'],
      borderWidth: 1.5,
      hoverOffset: 4,
    },
  ],
}))

const alcoholStatusChart = computed(() => ({
  labels: ['OK', 'Warning', 'Critical'],
  datasets: [
    {
      data: [
        stats.value?.alcoholOk ?? 0,
        stats.value?.alcoholWarning ?? 0,
        stats.value?.alcoholCritical ?? 0,
      ],
      backgroundColor: [fill.ok, fill.warning, fill.critical],
      borderColor: ['rgb(100,116,139)', 'rgb(59,130,246)', 'rgb(220,38,38)'],
      borderWidth: 1.5,
      hoverOffset: 4,
    },
  ],
}))
</script>

<template>
  <div class="inspections-view">
    <div class="header-row">
      <h1>Inspection Overview</h1>
      <button class="export-btn" @click="showExportModal = true">
        <Download :size="16" />
        Export to PDF
      </button>
    </div>

    <div v-if="loading" class="loading">Loading…</div>

    <template v-else-if="stats">
      <div class="kpi-strip">
        <div class="kpi">
          <span class="kpi-value">{{ stats.deviationTotal }}</span>
          <span class="kpi-label">Total Deviations</span>
        </div>
        <div class="kpi">
          <span class="kpi-value">{{ stats.deviationOpen }}</span>
          <span class="kpi-label">Open</span>
        </div>
        <div class="kpi">
          <span class="kpi-value">{{ stats.deviationCritical }}</span>
          <span class="kpi-label">Critical</span>
        </div>
        <div class="kpi">
          <span class="kpi-value">{{ stats.temperatureTotal }}</span>
          <span class="kpi-label">Temp. Logs</span>
        </div>
        <div class="kpi">
          <span class="kpi-value">{{ stats.alcoholTotal }}</span>
          <span class="kpi-label">Alcohol Logs</span>
        </div>
      </div>

      <div class="charts-row">
        <div class="chart-card">
          <p class="chart-title">Deviation Status</p>
          <div class="chart-wrap">
            <Doughnut :data="deviationStatusChart" :options="doughnutOptions" />
          </div>
        </div>

        <div class="chart-card chart-card--wide">
          <p class="chart-title">Deviation Severity</p>
          <div class="chart-wrap">
            <Bar :data="deviationSeverityChart" :options="barOptions" />
          </div>
        </div>
      </div>

      <div class="charts-row">
        <div class="chart-card">
          <p class="chart-title">Temperature Logs</p>
          <div class="chart-wrap">
            <Doughnut :data="temperatureStatusChart" :options="doughnutOptions" />
          </div>
        </div>

        <div class="chart-card">
          <p class="chart-title">Alcohol Logs</p>
          <div class="chart-wrap">
            <Doughnut :data="alcoholStatusChart" :options="doughnutOptions" />
          </div>
        </div>
      </div>
    </template>

    <div v-else class="loading">Could not load inspection data.</div>

    <ExportModal v-if="showExportModal" @close="showExportModal = false" />
  </div>
</template>

<style scoped>
.inspections-view {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.export-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 10px;
  background: var(--neutral);
  color: var(--bg);
  border: none;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  transition: background-color 0.15s ease;
}

.export-btn:hover {
  background-color: var(--neutral-hover);
}

.kpi-strip {
  display: flex;
  gap: 12px;
}

.kpi {
  flex: 1;
  background: var(--bg-secondary);
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 14px 16px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  box-shadow: var(--shadow-soft);
}

.kpi-value {
  font-size: 28px;
  font-weight: 800;
  line-height: 1;
  color: var(--neutral);
}

.kpi-label {
  font-size: 11px;
  color: var(--text-secondary);
}

.charts-row {
  display: flex;
  gap: 12px;
}

.chart-card,
.kpi {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  box-shadow: none;
}

.chart-card--wide {
  flex: 2;
}

.chart-title {
  color: #6b7280;
}

.chart-wrap {
  position: relative;
  height: 200px;
}

.kpi-value {
  color: #111827;
}

.kpi-label {
  color: #9ca3af;
}

.loading {
  text-align: center;
  color: var(--text-secondary);
  padding: 60px;
}

@media (max-width: 768px) {
  .kpi-strip,
  .charts-row {
    flex-direction: column;
  }

  .chart-card--wide {
    flex: 1;
  }
}
</style>
