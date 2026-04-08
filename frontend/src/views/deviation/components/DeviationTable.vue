<script setup lang="ts">
import InfoCard from '@/components/ui/InfoCard.vue'
import { useDeviationStore, type Deviation } from '@/stores/deviation'
import { ArrowDown, ArrowUp, ChevronLeft, Filter, History, Minus } from '@lucide/vue'
import { computed, ref, watch } from 'vue'

type SortField = 'category' | 'severity' | 'status' | 'createdAt'
type SortDirection = 'asc' | 'desc' | null

const store = useDeviationStore()
const props = withDefaults(
  defineProps<{
    category?: Deviation['category']
    title?: string
  }>(),
  {
    title: 'Existing Deviations',
  },
)

const rows = computed(() =>
  store.filtered.filter((deviation) => !props.category || deviation.category === props.category),
)

const pageSize = 5
const currentPage = ref(1)
const sortField = ref<SortField | null>(null)
const sortDirection = ref<SortDirection>(null)

const severityOrder: Record<Deviation['severity'], number> = {
  LOW: 1,
  MEDIUM: 2,
  HIGH: 3,
  CRITICAL: 4,
}

const statusOrder: Record<Deviation['status'], number> = {
  OPEN: 1,
  IN_PROGRESS: 2,
  RESOLVED: 3,
}

const sortedRows = computed(() => {
  if (!sortField.value || !sortDirection.value) {
    return rows.value
  }

  const direction = sortDirection.value === 'asc' ? 1 : -1
  const sorted = [...rows.value]

  sorted.sort((left, right) => {
    let leftValue: number | string
    let rightValue: number | string

    if (sortField.value === 'category') {
      leftValue = left.category
      rightValue = right.category
    } else if (sortField.value === 'severity') {
      leftValue = severityOrder[left.severity]
      rightValue = severityOrder[right.severity]
    } else if (sortField.value === 'status') {
      leftValue = statusOrder[left.status]
      rightValue = statusOrder[right.status]
    } else {
      leftValue = left.createdAt ? new Date(left.createdAt).getTime() : -1
      rightValue = right.createdAt ? new Date(right.createdAt).getTime() : -1
    }

    if (leftValue < rightValue) {
      return -1 * direction
    }

    if (leftValue > rightValue) {
      return 1 * direction
    }

    return 0
  })

  return sorted
})

const totalPages = computed(() => Math.max(1, Math.ceil(sortedRows.value.length / pageSize)))

const currentPageRows = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return sortedRows.value.slice(start, start + pageSize)
})

const emptyMessage = computed(() =>
  props.category
    ? 'No temperature deviations found for current filters.'
    : 'No deviations found for current filters.',
)

function formatCategory(value: Deviation['category']) {
  return value.charAt(0) + value.slice(1).toLowerCase()
}

function formatStatus(value: Deviation['status']) {
  return value.replace('_', ' ')
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

function pageLeft() {
  if (currentPage.value > 1) {
    currentPage.value -= 1
  }
}

function pageRight() {
  if (currentPage.value < totalPages.value) {
    currentPage.value += 1
  }
}

function toggleSort(field: SortField) {
  if (sortField.value !== field) {
    sortField.value = field
    sortDirection.value = 'asc'
    currentPage.value = 1
    return
  }

  if (sortDirection.value === 'asc') {
    sortDirection.value = 'desc'
  } else if (sortDirection.value === 'desc') {
    sortField.value = null
    sortDirection.value = null
  } else {
    sortDirection.value = 'asc'
  }

  currentPage.value = 1
}

function sortStateFor(field: SortField): SortDirection {
  if (sortField.value !== field) {
    return null
  }

  return sortDirection.value
}

watch(
  () => [store.filters.status, store.filters.severity, props.category],
  () => {
    currentPage.value = 1
  },
)

watch(totalPages, (nextTotalPages) => {
  if (currentPage.value > nextTotalPages) {
    currentPage.value = nextTotalPages
  }
})
</script>

<template>
  <InfoCard
    class="info-card"
    :title="props.title"
    :icon="History"
    iconBackgroundColor="var(--icon-bg-blue)"
    iconColor="var(--icon-stroke-blue)"
    :addToHeader="true"
  >
    <template #extra-header-content>
      <div class="filters-wrap">
        <Filter class="filter-icon" :size="16" aria-hidden="true" />
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
    </template>

    <table class="log-table">
      <thead class="log-table-header">
        <tr>
          <th>Title</th>
          <th>
            <button type="button" class="sort-header-button" @click="toggleSort('category')">
              Category
              <ArrowUp v-if="sortStateFor('category') === 'asc'" :size="14" aria-hidden="true" />
              <ArrowDown
                v-else-if="sortStateFor('category') === 'desc'"
                :size="14"
                aria-hidden="true"
              />
              <Minus v-else :size="14" aria-hidden="true" />
            </button>
          </th>
          <th>
            <button type="button" class="sort-header-button" @click="toggleSort('severity')">
              Severity
              <ArrowUp v-if="sortStateFor('severity') === 'asc'" :size="14" aria-hidden="true" />
              <ArrowDown
                v-else-if="sortStateFor('severity') === 'desc'"
                :size="14"
                aria-hidden="true"
              />
              <Minus v-else :size="14" aria-hidden="true" />
            </button>
          </th>
          <th>
            <button type="button" class="sort-header-button" @click="toggleSort('status')">
              Status
              <ArrowUp v-if="sortStateFor('status') === 'asc'" :size="14" aria-hidden="true" />
              <ArrowDown
                v-else-if="sortStateFor('status') === 'desc'"
                :size="14"
                aria-hidden="true"
              />
              <Minus v-else :size="14" aria-hidden="true" />
            </button>
          </th>
          <th>
            <button type="button" class="sort-header-button" @click="toggleSort('createdAt')">
              Created date
              <ArrowUp v-if="sortStateFor('createdAt') === 'asc'" :size="14" aria-hidden="true" />
              <ArrowDown
                v-else-if="sortStateFor('createdAt') === 'desc'"
                :size="14"
                aria-hidden="true"
              />
              <Minus v-else :size="14" aria-hidden="true" />
            </button>
          </th>
          <th>Actions</th>
        </tr>
      </thead>

      <tbody class="log-table-body">
        <tr v-for="d in currentPageRows" :key="d.id ?? d.title">
          <td class="title">{{ d.title }}</td>
          <td>{{ formatCategory(d.category) }}</td>
          <td class="severity-cell">
            <span class="deviation-badge" :class="severityClass(d.severity)">{{ d.severity }}</span>
          </td>
          <td class="status-cell">
            <span class="deviation-badge" :class="statusClass(d.status)">{{
              formatStatus(d.status)
            }}</span>
          </td>
          <td>{{ formatDate(d.createdAt) }}</td>
          <td><button class="view" type="button">View</button></td>
        </tr>
        <tr v-if="rows.length === 0">
          <td colspan="6" class="empty">{{ emptyMessage }}</td>
        </tr>
      </tbody>
    </table>

    <div class="paging">
      <button class="page-button" @click="pageLeft"><ChevronLeft /></button>
      <p>Page {{ currentPage }} of {{ totalPages }}</p>
      <button class="page-button" @click="pageRight">
        <ChevronLeft style="transform: rotate(180deg)" />
      </button>
    </div>
  </InfoCard>
</template>

<style scoped>
.info-card {
  margin-top: 18px;
}

.filters-wrap {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: auto;
}

.filter-icon {
  color: var(--text-secondary);
}

.filters select {
  min-height: 34px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--bg-secondary);
  font-size: 11px;
  color: var(--text-secondary);
}

.log-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.log-table-header th:nth-child(1) {
  width: 30%;
}

.log-table-header th:nth-child(2) {
  width: 14%;
}

.log-table-header th:nth-child(3),
.log-table-header th:nth-child(4) {
  width: 14%;
}

.log-table-header th:nth-child(5) {
  width: 18%;
}

.log-table-header th:nth-child(6) {
  width: 10%;
}

.log-table-header th {
  text-align: left;
  padding: 12px 10px;
  font-size: 14px;
  color: var(--text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.sort-header-button {
  border: 0;
  background: transparent;
  padding: 0;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: inherit;
  font: inherit;
  cursor: pointer;
}

.log-table-body td {
  text-align: left;
  padding: 15px 10px;
  border-bottom: 1px solid var(--border);
  color: var(--text);
  font-size: 13px;
}

.log-table-body tr:last-child td {
  border-bottom: none;
}

.log-table-body td.title {
  font-weight: 700;
  white-space: normal;
}

.severity-cell,
.status-cell {
  vertical-align: middle;
}

.deviation-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: static;
  float: none;
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
  color: var(--text);
}

.status-resolved {
  background: var(--bg);
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

.paging {
  display: flex;
  justify-content: flex-start;
  margin-top: 15px;
  gap: 10px;
}

.page-button {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--text-secondary);
  aspect-ratio: 1 / 1;
  transition: background-color 0.15s ease;
}

.page-button:hover {
  background-color: var(--bg);
}

.page-button:active {
  background-color: var(--bg-hover);
}

.empty {
  text-align: center;
  color: var(--text-secondary);
  padding: 24px;
}

@media (max-width: 900px) {
  .filters-wrap {
    width: 100%;
    margin-left: 0;
  }

  .filters {
    width: 100%;
    display: grid;
    grid-template-columns: 1fr 1fr;
  }
}
</style>
