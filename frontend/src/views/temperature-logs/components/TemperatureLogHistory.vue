<script setup lang="ts">
import InfoCard from '@/components/ui/InfoCard.vue'
import { History, Check, TriangleAlert, ChevronLeft, Filter } from '@lucide/vue'
import { computed, ref, watch } from 'vue'

// TODO: Replace with real data from backend
const temperatureLogs = [
  {
    id: 1,
    storageUnit: 'Fridge1',
    temperature: 4,
    // 10 minutes ago
    timestamp: new Date(Date.now() - 10 * 60 * 1000),
    status: 'Optimal',
    loggedBy: 'John Doe',
  },
  {
    id: 2,
    storageUnit: 'Freezer room',
    temperature: -18,
    // 30 minutes ago
    timestamp: new Date(Date.now() - 30 * 60 * 1000),
    status: 'Abnormal',
    loggedBy: 'Sigurd Andris',
  },
  {
    id: 3,
    storageUnit: 'Fridge2',
    temperature: 3,
    // 1 hour ago
    timestamp: new Date(Date.now() - 60 * 60 * 1000),
    status: 'Optimal',
    loggedBy: 'Freddy Five Bears',
  },
  {
    id: 4,
    storageUnit: 'Fridge1',
    temperature: 5,
    // 2 hours ago
    timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000),
    status: 'Optimal',
    loggedBy: 'John Doe',
  },
  {
    id: 5,
    storageUnit: 'Freezer room',
    temperature: -20,
    // 3 hours ago
    timestamp: new Date(Date.now() - 3 * 60 * 60 * 1000),
    status: 'Optimal',
    loggedBy: 'Sigurd Andris',
  },
  {
    id: 6,
    storageUnit: 'Fridge2',
    temperature: 6,
    // 4 hours ago
    timestamp: new Date(Date.now() - 4 * 60 * 60 * 1000),
    status: 'Abnormal',
    loggedBy: 'Freddy Five Bears',
  },
]

const pageSize = 4

const storageUnits = [
  { id: 1, name: 'Fridge1' },
  { id: 2, name: 'Freezer room' },
  { id: 3, name: 'Fridge2' },
]

const currentPage = ref(1)
const filteredStorageUnit = ref('')

const filteredTemperatureLogs = computed(() => {
  if (!filteredStorageUnit.value) {
    return temperatureLogs
  }

  return temperatureLogs.filter((log) => log.storageUnit === filteredStorageUnit.value)
})

const temperatureLogsSplit = computed(() =>
  Array.from({ length: Math.ceil(filteredTemperatureLogs.value.length / pageSize) }, (_, index) =>
    filteredTemperatureLogs.value.slice(index * pageSize, index * pageSize + pageSize),
  ),
)

const currentPageLogs = computed(() => temperatureLogsSplit.value[currentPage.value - 1] ?? [])

watch(filteredStorageUnit, () => {
  currentPage.value = 1
})

function isRowAbnormal(log: { status: string }) {
  return log.status === 'Abnormal'
}

function pageLeft() {
  if (currentPage.value > 1) {
    currentPage.value -= 1
  }
}

function pageRight() {
  if (currentPage.value < temperatureLogsSplit.value.length) {
    currentPage.value += 1
  }
}
</script>

<template>
  <InfoCard
    class="info-card"
    title="Temperature Log History"
    :icon="History"
    iconBackgroundColor="var(--neutral)"
    iconColor="white"
  >
    <template #extra-header-content>
      <div class="unit-filter-wrap">
        <Filter class="unit-filter-icon" :size="16" aria-hidden="true" />
        <select class="unit-filter" v-model="filteredStorageUnit">
          <option value="">All Storage Units</option>
          <option v-for="unit in storageUnits" :key="unit.id" :value="unit.name">
            {{ unit.name }}
          </option>
        </select>
      </div>
    </template>
    <table class="log-table">
      <thead class="log-table-header">
        <tr>
          <th>Time Logged</th>
          <th>Storage Unit</th>
          <th>Temperature (°C)</th>
          <th>Status</th>
          <th>Logged By</th>
        </tr>
      </thead>
      <tbody class="log-table-body">
        <tr
          v-for="log in currentPageLogs"
          :key="log.id"
          v-bind:class="isRowAbnormal(log) ? 'abnormal-row' : ''"
        >
          <td>{{ log.timestamp.toLocaleString() }}</td>
          <td>{{ log.storageUnit }}</td>
          <td>{{ log.temperature }} °C</td>
          <td class="status-column">
            <Check v-if="log.status === 'Optimal'" :size="20" />
            <TriangleAlert v-else-if="log.status === 'Abnormal'" :size="20" />
            <span>{{ log.status }}</span>
          </td>
          <td>{{ log.loggedBy }}</td>
        </tr>
      </tbody>
    </table>

    <div class="paging">
      <button class="page-button" @click="pageLeft"><ChevronLeft /></button>
      <p>Page {{ currentPage }} of {{ temperatureLogsSplit.length || 1 }}</p>
      <button class="page-button" @click="pageRight">
        <ChevronLeft style="transform: rotate(180deg)" />
      </button>
    </div>
  </InfoCard>
</template>

<style scoped>
.unit-filter-wrap {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 8px;
}

.unit-filter-icon {
  color: var(--text-secondary);
}

.log-table {
  width: 100%;
  border-collapse: collapse;
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

.log-table-header {
  color: var(--text-secondary);
  padding: 10px;
}

.log-table-header th {
  text-align: left;
  padding: 12px 10px;
  font-size: 14px;
}

.log-table-body td {
  padding: 15px 10px;
}

.abnormal-row td {
  background-color: var(--cta-red-bg);
  color: var(--cta-red-text);
  transition:
    background-color 0.15s ease,
    border-color 0.15s ease;
}

.abnormal-row td:first-child {
  border-top-left-radius: 10px;
  border-bottom-left-radius: 10px;
}

.abnormal-row td:last-child {
  border-top-right-radius: 10px;
  border-bottom-right-radius: 10px;
}

.opotimal-row td {
  color: var(--neutral);
  transition:
    background-color 0.15s ease,
    border-color 0.15s ease;
}

.opotimal-row td:first-child {
  border-top-left-radius: 10px;
  border-bottom-left-radius: 10px;
}

.opotimal-row td:last-child {
  border-top-right-radius: 10px;
  border-bottom-right-radius: 10px;
}

.status-column {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
