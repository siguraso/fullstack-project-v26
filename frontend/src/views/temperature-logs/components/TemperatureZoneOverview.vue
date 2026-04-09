<script setup lang="ts">
import InfoCard from '@/components/ui/InfoCard.vue'
import { getAuthSession } from '@/services/auth'
import type { TemperatureZone } from '@/interfaces/TemperatureZone.interface'
import { Box, ChevronLeft, Pencil } from '@lucide/vue'
import { computed, onMounted, ref } from 'vue'

const props = defineProps<{
  zones: TemperatureZone[]
}>()

const emit = defineEmits<{
  (event: 'edit-zone', zone: TemperatureZone): void
  (event: 'create-zone'): void
}>()

const pageSize = 4
const currentPage = ref(1)

const role = ref<string | null>(null)

onMounted(() => {
  const session = getAuthSession()
  role.value = session?.role ?? null
})

const zonesSplit = computed(() =>
  Array.from({ length: Math.ceil(props.zones.length / pageSize) }, (_, index) =>
    props.zones.slice(index * pageSize, index * pageSize + pageSize),
  ),
)

const currentPageZones = computed(() => zonesSplit.value[currentPage.value - 1] ?? [])

function pageLeft() {
  if (currentPage.value > 1) {
    currentPage.value -= 1
  }
}

function pageRight() {
  if (currentPage.value < zonesSplit.value.length) {
    currentPage.value += 1
  }
}

function openEditOverlay(zone: TemperatureZone) {
  emit('edit-zone', zone)
}

function openCreateOverlay() {
  emit('create-zone')
}
</script>

<template>
  <InfoCard
    class="info-card"
    title="Temperature zones"
    :icon="Box"
    iconBackgroundColor="var(--icon-bg-green)"
    iconColor="var(--icon-stroke-green)"
    :addToHeader="true"
  >
    <template #extra-header-content>
      <button v-if="role == 'ADMIN'" class="add-button" @click="openCreateOverlay">+</button>
    </template>
    <table class="log-table">
      <thead class="log-table-header">
        <tr>
          <th>Zone Name</th>
          <th>Lower limit</th>
          <th>Upper limit</th>
          <th v-if="role == 'ADMIN'">Actions</th>
        </tr>
      </thead>
      <tbody class="log-table-body">
        <tr v-for="zone in currentPageZones" :key="zone.id">
          <td>{{ zone.name }}</td>
          <td>{{ zone.lowerLimitCelsius }}°C</td>
          <td>{{ zone.upperLimitCelsius }}°C</td>
          <td v-if="role == 'ADMIN'">
            <button class="edit-btn" @click="openEditOverlay(zone)" aria-label="Edit zone">
              <Pencil :size="16" aria-hidden="true" />
            </button>
          </td>
        </tr>
      </tbody>
    </table>
    <div class="paging">
      <button class="page-button" @click="pageLeft"><ChevronLeft /></button>
      <p>Page {{ currentPage }} of {{ zonesSplit.length || 1 }}</p>
      <button class="page-button" @click="pageRight">
        <ChevronLeft style="transform: rotate(180deg)" />
      </button>
    </div>
  </InfoCard>
</template>

<style scoped>
.add-button {
  margin-left: auto;
  width: 32px;
  font-size: 15px;
}

.log-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
}

.log-table-header th:nth-child(1) {
  width: 40%;
}

.log-table-header th:nth-child(2) {
  width: 25%;
}

.log-table-header th:nth-child(3) {
  width: 25%;
}

.log-table-header th:nth-child(4) {
  width: 10%;
}

.paging {
  display: flex;
  justify-content: flex-start;
  margin-top: 10px;
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

.log-table-header th:last-child {
  padding: 12px 4px;
}

.log-table-body td {
  padding: 15px 10px;
  border-bottom: 1px solid var(--border);
}

.log-table-body tr:last-child td {
  border-bottom: none;
}

.edit-btn {
  background-color: var(--bg-secondary);
  color: var(--neutral);
  border: 1px solid var(--border);
  width: 36px;
  height: 36px;
  place-items: center;
}

.edit-btn:hover {
  background-color: var(--bg-hover);
}

.edit-btn:active {
  background-color: var(--bg-active);
}
</style>
