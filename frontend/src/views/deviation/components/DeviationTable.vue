<script setup lang="ts">
import { useDeviationStore, type Deviation } from '@/stores/deviation'

const store = useDeviationStore()

function formatCategory(value: Deviation['category']) {
  return value.charAt(0) + value.slice(1).toLowerCase()
}

function formatStatus(value: Deviation['status']) {
  return value.replace('_', ' ')
}

function formatModule(value?: Deviation['module']) {
  if (!value) {
    return '-'
  }

  return value === 'IK_ALCOHOL' ? 'IK-Alcohol' : 'IK-Food'
}

function formatDate(value?: string) {
  if (!value) {
    return '-'
  }

  const parsed = new Date(value)

  return Number.isNaN(parsed.getTime()) ? value : parsed.toLocaleDateString('en-GB')
}

function severityClass(value: Deviation['severity']) {
  return `sev-${value.toLowerCase()}`
}

function statusClass(value: Deviation['status']) {
  return `status-${value.toLowerCase()}`
}
</script>

<template>
  <div class="table-wrap">
    <div class="table-head">
      <h3>Existing Deviations</h3>

      <div class="filters">
        <select v-model="store.filters.status">
          <option value="ALL">Status: All</option>
          <option value="OPEN">Open</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="RESOLVED">Resolved</option>
        </select>

        <select v-model="store.filters.severity">
          <option value="ALL">Severity: All</option>
          <option value="LOW">Low</option>
          <option value="MEDIUM">Medium</option>
          <option value="HIGH">High</option>
          <option value="CRITICAL">Critical</option>
        </select>
      </div>
    </div>

    <div class="table-shell">
      <table>
        <thead>
        <tr>
          <th>Title</th>
          <th>Category</th>
          <th>Severity</th>
          <th>Module</th>
          <th>Status</th>
          <th>Created date</th>
          <th>Actions</th>
        </tr>
        </thead>

        <tbody>
        <tr v-for="d in store.filtered" :key="d.id ?? d.title">
          <td class="title">{{ d.title }}</td>
          <td>{{ formatCategory(d.category) }}</td>
          <td>
            <span class="badge" :class="severityClass(d.severity)">{{ d.severity }}</span>
          </td>
          <td>{{ formatModule(d.module) }}</td>
          <td>
            <span class="badge" :class="statusClass(d.status)">{{ formatStatus(d.status) }}</span>
          </td>
          <td>{{ formatDate(d.createdAt) }}</td>
          <td><button class="view" type="button">View</button></td>
        </tr>
        <tr v-if="store.filtered.length === 0">
          <td colspan="7" class="empty">No deviations found for current filters.</td>
        </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.table-wrap {
  margin-top: 26px;
}

.table-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  gap: 12px;
}

h3 {
  margin: 0;
  font-size: 34px;
  color: var(--text);
}

.filters {
  display: flex;
  gap: 8px;
}

.filters select {
  min-height: 34px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--surface);
  font-size: 11px;
  color: var(--text-secondary);
}

.table-shell {
  background: var(--surface);
  border: 1px solid var(--border);
  border-radius: 14px;
  box-shadow: var(--shadow-soft);
  overflow: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  text-align: left;
  padding: 13px 14px;
  border-bottom: 1px solid var(--border);
  white-space: nowrap;
}

th {
  font-size: 10px;
  letter-spacing: 0.06em;
  text-transform: uppercase;
  color: var(--text-secondary);
}

td {
  color: var(--text);
  font-size: 13px;
}

td.title {
  font-weight: 700;
  max-width: 260px;
  white-space: normal;
}

.badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  min-height: 22px;
  padding: 2px 9px;
  font-size: 10px;
  text-transform: uppercase;
  font-weight: 700;
  letter-spacing: 0.03em;
}

.sev-low {
  background: #e8fbef;
  color: #23975c;
}

.sev-medium {
  background: #fff6df;
  color: #b3891a;
}

.sev-high {
  background: #fff0e6;
  color: #c96f3a;
}

.sev-critical {
  background: var(--cta-red);
  color: var(--bg);
}

.status-open {
  background: #ffeaea;
  color: var(--cta-red);
}

.status-in_progress {
  background: #e8efff;
  color: var(--primary);
}

.status-resolved {
  background: var(--surface-muted);
  color: var(--text-secondary);
}

.view {
  border: 0;
  min-height: 22px;
  background: transparent;
  color: var(--text);
  text-transform: uppercase;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.03em;
  padding: 0;
}

.view:hover {
  text-decoration: underline;
  background: transparent;
}

.empty {
  text-align: center;
  color: var(--text-muted);
  padding: 24px;
}
</style>