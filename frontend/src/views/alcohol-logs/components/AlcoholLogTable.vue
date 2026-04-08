<script setup lang="ts">
import InfoCard from '@/components/ui/InfoCard.vue'
import type { AlcoholLog } from '@/interfaces/AlcoholLog.interface'
import { ScrollText } from '@lucide/vue'
import { computed, ref } from 'vue'

type SortField = 'logType' | 'status' | 'recordedAt'
type SortDirection = 'asc' | 'desc' | null

const props = defineProps<{
  logs: AlcoholLog[]
  isLoading?: boolean
}>()

const emit = defineEmits<{
  (event: 'view-requested', log: AlcoholLog): void
}>()

const pageSize = 10
const currentPage = ref(1)
const sortField = ref<SortField | null>(null)
const sortDirection = ref<SortDirection>(null)

const statusOrder: Record<string, number> = {
  OK: 1,
  WARNING: 2,
  CRITICAL: 3,
}

const sortedLogs = computed(() => {
  if (!sortField.value || !sortDirection.value) {
    return props.logs
  }

  const direction = sortDirection.value === 'asc' ? 1 : -1
  const sorted = [...props.logs]

  sorted.sort((left, right) => {
    let leftValue: number | string
    let rightValue: number | string

    if (sortField.value === 'logType') {
      leftValue = left.logType
      rightValue = right.logType
    } else if (sortField.value === 'status') {
      leftValue = statusOrder[left.status || ''] ?? 0
      rightValue = statusOrder[right.status || ''] ?? 0
    } else {
      const leftDate = left.recordedAt ?? left.createdAt
      const rightDate = right.recordedAt ?? right.createdAt
      leftValue = leftDate ? new Date(leftDate).getTime() : -1
      rightValue = rightDate ? new Date(rightDate).getTime() : -1
    }

    if (leftValue < rightValue) return -1 * direction
    if (leftValue > rightValue) return 1 * direction
    return 0
  })

  return sorted
})

const totalPages = computed(() => Math.max(1, Math.ceil(sortedLogs.value.length / pageSize)))

const currentPageLogs = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return sortedLogs.value.slice(start, start + pageSize)
})

function toggleSort(field: SortField) {
  if (sortField.value === field) {
    if (sortDirection.value === 'asc') {
      sortDirection.value = 'desc'
    } else {
      sortField.value = null
      sortDirection.value = null
    }
  } else {
    sortField.value = field
    sortDirection.value = 'asc'
  }
  currentPage.value = 1
}

function formatLogType(type: string): string {
  return type.split('_').map(word => word.charAt(0) + word.slice(1).toLowerCase()).join(' ')
}

function formatDate(date?: string): string {
  if (!date) return '—'
  return new Date(date).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

function getSortIcon(field: SortField): string {
  if (sortField.value !== field) return '↕'
  return sortDirection.value === 'asc' ? '↑' : '↓'
}

function requestView(log: AlcoholLog) {
  emit('view-requested', log)
}
</script>

<template>
  <InfoCard
    :icon="ScrollText"
    title="Existing Alcohol Logs"
    iconBackgroundColor="var(--neutral)"
    iconColor="#ffffff"
  >
    <div class="table-wrapper">
      <table v-if="!isLoading && currentPageLogs.length" class="table">
        <colgroup>
          <col class="col-title" />
          <col class="col-type" />
          <col class="col-status" />
          <col class="col-details" />
          <col class="col-date" />
          <col class="col-actions" />
        </colgroup>
        <thead>
          <tr>
            <th>Title</th>
            <th class="sortable" @click="toggleSort('logType')">
              Log Type <span class="sort-icon">{{ getSortIcon('logType') }}</span>
            </th>
            <th class="sortable" @click="toggleSort('status')">
              Status <span class="sort-icon">{{ getSortIcon('status') }}</span>
            </th>
            <th>Details</th>
            <th class="sortable" @click="toggleSort('recordedAt')">
              Date <span class="sort-icon">{{ getSortIcon('recordedAt') }}</span>
            </th>
            <th class="actions">Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="log in currentPageLogs" :key="log.id">
            <td class="title-cell">{{ log.title }}</td>
            <td class="type-cell">
              <span class="log-type-pill type-pill">{{ formatLogType(log.logType) }}</span>
            </td>
            <td>
              <span v-if="log.status" class="status" :class="log.status.toLowerCase()">
                {{ log.status }}
              </span>
              <span v-else class="empty-pill">—</span>
            </td>
            <td class="details-cell">
              <div class="detail-list">
                <span v-if="log.idChecked" class="detail-tag">ID Checked</span>
                <span v-if="log.serviceRefused" class="detail-tag">Service Refused</span>
                <span v-if="log.estimatedAge !== null" class="detail-tag">Age: {{ log.estimatedAge }}</span>
              </div>
            </td>
            <td class="date-cell">{{ formatDate(log.recordedAt ?? log.createdAt) }}</td>
            <td class="actions-cell">
              <button class="action-btn view" @click="requestView(log)" title="View details">
                <span>View</span>
              </button>
            </td>
          </tr>
        </tbody>
      </table>

      <div v-if="isLoading" class="empty-state">
        <p>Loading logs...</p>
      </div>

      <div v-if="!isLoading && !currentPageLogs.length" class="empty-state">
        <p>No alcohol logs found.</p>
      </div>

      <div v-if="totalPages > 1" class="pagination">
        <button
          v-for="page in totalPages"
          :key="page"
          :class="['page-btn', { active: currentPage === page }]"
          @click="currentPage = page"
        >
          {{ page }}
        </button>
      </div>
    </div>
  </InfoCard>
</template>

<style scoped>
.table-wrapper {
  overflow-x: auto;
}

:deep(.header) {
  justify-content: flex-start;
}

:deep(.title) {
  text-align: left;
}

.table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
  font-size: 13px;
}

.col-title {
  width: 24%;
}

.col-type {
  width: 18%;
}

.col-status {
  width: 13%;
}

.col-details {
  width: 24%;
}

.col-date {
  width: 13%;
}

.col-actions {
  width: 8%;
}

thead {
  background: var(--bg);
  border-bottom: 2px solid var(--border);
}

th {
  padding: 12px;
  text-align: left;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--text-secondary);
  white-space: nowrap;
}

th.sortable {
  cursor: pointer;
  user-select: none;
}

th.sortable:hover {
  color: var(--text);
}

.sort-icon {
  opacity: 0.5;
  font-size: 11px;
}

th.sortable:hover .sort-icon {
  opacity: 1;
}

td {
  padding: 12px;
  border-bottom: 1px solid var(--border);
  color: var(--text);
  vertical-align: middle;
}

tbody tr:hover {
  background: var(--bg);
}

.title-cell {
  font-weight: 600;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.type-cell {
  position: relative;
  overflow: hidden;
  white-space: normal;
}

.log-type-pill {
  display: inline-block;
  padding: 4px 8px;
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.02em;
  color: var(--text-secondary);
  position: static !important;
  top: auto !important;
  right: auto !important;
  left: auto !important;
  bottom: auto !important;
  float: none !important;
  transform: none !important;
  margin: 0;
}

.type-pill {
  display: inline-block;
  max-width: 100%;
  white-space: normal;
  word-break: break-word;
  overflow-wrap: anywhere;
  text-overflow: clip;
  line-height: 1.2;
}

.empty-pill {
  color: var(--text-secondary);
}

.status {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.status.ok {
  background: #dcfce7;
  color: #15803d;
  border: 1px solid #bbf7d0;
}

.status.warning {
  background: #fef3c7;
  color: #b45309;
  border: 1px solid #fde68a;
}

.status.critical {
  background: #fee2e2;
  color: #991b1b;
  border: 1px solid #fecaca;
}

.details-cell {
  max-width: 200px;
}

.detail-list {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.detail-tag {
  display: inline-block;
  padding: 2px 6px;
  background: var(--neutral);
  color: white;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 600;
}

.date-cell {
  color: var(--text-secondary);
  white-space: nowrap;
}

.actions-cell {
  text-align: center;
}

.action-btn {
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.action-btn:hover {
  background: rgba(0, 0, 0, 0.06);
  color: var(--text);
}

.action-btn.view {
  border: 1px solid var(--border);
  background: var(--surface);
  padding: 4px 10px;
}

.empty-state {
  padding: 40px 20px;
  text-align: center;
  color: var(--text-secondary);
}

.pagination {
  display: flex;
  gap: 8px;
  justify-content: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border);
}

.page-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid var(--border);
  background: var(--bg);
  color: var(--text);
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.2s;
}

.page-btn:hover {
  background: var(--surface);
  border-color: var(--cta);
}

.page-btn.active {
  background: var(--cta);
  color: white;
  border-color: var(--cta);
}

th.actions,
td.actions-cell {
  text-align: center;
}
</style>









