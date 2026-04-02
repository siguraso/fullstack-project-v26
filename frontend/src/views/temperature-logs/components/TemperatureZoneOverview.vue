<script setup lang="ts">
import InfoCard from '@/components/ui/InfoCard.vue'
import { Box, ChevronLeft } from '@lucide/vue'
import { computed, ref } from 'vue'

const zones = [
  { id: 1, name: 'Fridge1', lowerLimitCelcius: 0.0, upperLimitCelcius: 5.0 },
  { id: 2, name: 'Freezer room', lowerLimitCelcius: -25.0, upperLimitCelcius: -15.0 },
  { id: 3, name: 'Room', lowerLimitCelcius: 15.0, upperLimitCelcius: 25.0 },
  { id: 4, name: 'Fridge2', lowerLimitCelcius: 0.0, upperLimitCelcius: 5.0 },
  { id: 5, name: 'Fridge3', lowerLimitCelcius: 0.0, upperLimitCelcius: 5.0 },
  { id: 6, name: 'Fridge4', lowerLimitCelcius: 0.0, upperLimitCelcius: 5.0 },
  { id: 7, name: 'Fridge5', lowerLimitCelcius: 0.0, upperLimitCelcius: 5.0 },
  { id: 8, name: 'Fridge6', lowerLimitCelcius: 0.0, upperLimitCelcius: 5.0 },
  { id: 9, name: 'Fridge7', lowerLimitCelcius: 0.0, upperLimitCelcius: 5.0 },
  { id: 10, name: 'Fridge8', lowerLimitCelcius: 0.0, upperLimitCelcius: 5.0 },
]

const pageSize = 4
const currentPage = ref(1)

const zonesSplit = computed(() =>
  Array.from({ length: Math.ceil(zones.length / pageSize) }, (_, index) =>
    zones.slice(index * pageSize, index * pageSize + pageSize),
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
</script>

<template>
  <InfoCard
    class="info-card"
    title="Temperature zones"
    :icon="Box"
    iconBackgroundColor="var(--neutral)"
    iconColor="white"
  >
    <table class="log-table">
      <thead class="log-table-header">
        <tr>
          <th>Zone Name</th>
          <th>Lower limit</th>
          <th>Upper limit</th>
          <th></th>
        </tr>
      </thead>
      <tbody class="log-table-body">
        <tr v-for="zone in currentPageZones" :key="zone.id">
          <td>{{ zone.name }}</td>
          <td>{{ zone.lowerLimitCelcius }}°C</td>
          <td>{{ zone.upperLimitCelcius }}°C</td>
          <td><button class="edit-btn">Edit</button></td>
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
  margin-top: 12px;
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
  border-bottom: 1px solid var(--stroke);
}

.log-table-body tr:last-child td {
  border-bottom: none;
}

.edit-btn {
  width: 100%;
}
</style>
