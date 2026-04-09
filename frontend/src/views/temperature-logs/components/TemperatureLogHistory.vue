<script setup lang="ts">
import InfoCard from '@/components/ui/InfoCard.vue'
import { getAuthSession } from '@/services/auth'
import type { TemperatureLog } from '@/interfaces/TemperatureLog.interface'
import type { TemperatureZone } from '@/interfaces/TemperatureZone.interface'
import { History, Check, TriangleAlert, ChevronLeft, Filter, Trash2 } from '@lucide/vue'
import { computed, onMounted, ref } from 'vue'

const props = defineProps<{
  temperatureLogs: TemperatureLog[]
  temperatureZones: TemperatureZone[]
}>()

const emit = defineEmits<{
  (event: 'delete-log', logId: number): void
}>()

const pageSize = 4

const currentPage = ref(1)
const filteredtemperatureZoneId = ref<number | null>(null)
const filteredTemperatureZone = ref<string>('')
const role = ref<string | null>(null)

onMounted(() => {
  const session = getAuthSession()
  role.value = session?.role ?? null
})

const canDeleteLogs = computed(() => role.value === 'ADMIN' || role.value === 'MANAGER')

const filteredTemperatureLogs = computed(() => {
  if (!filteredtemperatureZoneId.value) {
    return props.temperatureLogs
  }

  return props.temperatureLogs.filter(
    (log) => log.temperatureZoneId === filteredtemperatureZoneId.value,
  )
})

const temperatureLogsSplit = computed(() =>
  Array.from({ length: Math.ceil(filteredTemperatureLogs.value.length / pageSize) }, (_, index) =>
    filteredTemperatureLogs.value.slice(index * pageSize, index * pageSize + pageSize),
  ),
)

const currentPageLogs = computed(() => temperatureLogsSplit.value[currentPage.value - 1] ?? [])

function setFilteredTemperatureZone() {
  const selectedZone = props.temperatureZones.find(
    (zone) => zone.name === filteredTemperatureZone.value,
  )

  filteredtemperatureZoneId.value = selectedZone ? selectedZone.id : null

  currentPage.value = 1
}

function isRowAbnormal(log: TemperatureLog) {
  const zone = props.temperatureZones.find(
    (temperatureZone) => temperatureZone.id === log.temperatureZoneId,
  )

  if (!zone) {
    return false
  }

  if (log.temperatureCelsius >= zone.upperLimitCelsius) {
    return true
  } else if (log.temperatureCelsius <= zone.lowerLimitCelsius) {
    return true
  }

  return false
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

function formatTimestamp(timestamp: string) {
  const parsedDate = new Date(timestamp)

  if (Number.isNaN(parsedDate.getTime())) {
    return timestamp
  }

  return new Intl.DateTimeFormat('en-GB', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  }).format(parsedDate)
}

function deleteLog(logId: number) {
  emit('delete-log', logId)
}
</script>

<template>
  <InfoCard
    class="info-card"
    title="Temperature Log History"
    :icon="History"
    iconBackgroundColor="var(--icon-bg-blue)"
    iconColor="var(--icon-stroke-blue)"
  >
    <template #extra-header-content>
      <div class="zone-filter-wrap">
        <Filter class="zone-filter-icon" :size="16" aria-hidden="true" />
        <select v-model="filteredTemperatureZone" @change="setFilteredTemperatureZone">
          <option value="">All Temperature Zones</option>
          <option v-for="zone in temperatureZones" :key="zone.id" :value="zone.name">
            {{ zone.name }}
          </option>
        </select>
      </div>
    </template>
    <div class="table-scroll">
      <table class="log-table">
        <thead class="log-table-header">
          <tr>
            <th>Time Logged</th>
            <th>Temperature Zone</th>
            <th>Temperature (°C)</th>
            <th>Status</th>
            <th>Logged By</th>
            <th v-if="canDeleteLogs">Actions</th>
          </tr>
        </thead>
        <tbody class="log-table-body">
          <tr
            v-for="log in currentPageLogs"
            :key="log.id"
            v-bind:class="isRowAbnormal(log) ? 'abnormal-row' : ''"
          >
            <td>{{ formatTimestamp(log.timestamp) }}</td>
            <td>
              {{
                log.temperatureZoneName ??
                props.temperatureZones.find((zone) => zone.id === log.temperatureZoneId)?.name ??
                'Unknown zone'
              }}
            </td>
            <td>{{ log.temperatureCelsius }} °C</td>
            <td class="status-column">
              <div class="status-content">
                <Check v-if="!isRowAbnormal(log)" :size="20" />
                <TriangleAlert v-else :size="20" />
                <span>{{ isRowAbnormal(log) ? 'Abnormal' : 'Optimal' }}</span>
              </div>
            </td>
            <td>{{ log.recordedByName ?? 'Unknown user' }}</td>
            <td v-if="canDeleteLogs">
              <button class="delete-btn" @click="deleteLog(log.id)">
                <Trash2 :size="16" aria-hidden="true" />
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

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
.zone-filter-wrap {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 8px;
}

.zone-filter-icon {
  color: var(--text-secondary);
}

.table-scroll {
  overflow-x: auto;
}

.log-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
  min-width: 780px;
}

.log-table-header th:nth-child(1) {
  width: 22%;
}

.log-table-header th:nth-child(2) {
  width: 20%;
}

.log-table-header th:nth-child(3) {
  width: 14%;
}

.log-table-header th:nth-child(4) {
  width: 14%;
}

.log-table-header th:nth-child(5) {
  width: 20%;
}

.log-table-header th:nth-child(6) {
  width: 10%;
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
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.log-table-body td {
  padding: 15px 10px;
  border-bottom: 1px solid var(--border);
}

.log-table-body tr:last-child td {
  border-bottom: none;
}

.abnormal-row td {
  background-color: var(--cta-red-bg);
  color: var(--cta-red-text);
  transition:
    background-color 0.15s ease,
    border-color 0.15s ease;
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
  white-space: nowrap;
}

.status-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.delete-btn {
  background-color: var(--cta-red-btn);
  color: white;
  height: 36px;
  width: 36px;
}

@media (max-width: 640px) {
  .zone-filter-wrap {
    width: 100%;
    margin-left: 0;
  }

  .zone-filter-wrap select {
    width: 100%;
  }

  .paging {
    flex-wrap: wrap;
    align-items: center;
  }
}
</style>
