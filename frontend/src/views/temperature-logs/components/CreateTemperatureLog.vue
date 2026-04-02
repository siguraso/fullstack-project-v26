<script setup lang="ts">
import InfoCard from '@/components/ui/InfoCard.vue'
import { Edit2 } from '@lucide/vue'

// TODO: Replace with actual storage units from API
const storageUnits = [
  { id: 1, name: 'Unit A' },
  { id: 2, name: 'Unit B' },
  { id: 3, name: 'Unit C' },
]
</script>

<template>
  <InfoCard
    class="info-card"
    title="Create Temperature Log"
    :icon="Edit2"
    iconBackgroundColor="var(--neutral)"
    iconColor="white"
  >
    <div class="input-field">
      <p class="subtext">Storage Unit</p>
      <select class="deviation-filter" aria-label="Filter deviations">
        <option value="" selected disabled>Select Storage Unit</option>
        <option v-for="unit in storageUnits" :key="unit.id" :value="unit.id">
          {{ unit.name }}
        </option>
      </select>
    </div>

    <div class="input-field">
      <p class="subtext">Temperature (°C)</p>
      <input class="temperature-input" type="number" placeholder="Enter temperature" />
    </div>

    <div class="input-field">
      <p class="subtext">Notes (Optional)</p>
      <input type="text" placeholder="Additional notes" />
    </div>

    <button class="create-btn">Create Log</button>
  </InfoCard>
</template>

<style scoped>
.input-field {
  display: flex;
  flex-direction: column;
  margin-bottom: 20px;
  gap: 10px;
}

.deviation-filter {
  width: 100%;
}

.subtext {
  font-size: 0.9em;
  color: var(--text-secondary);
  margin-bottom: 5px;
}

.temperature-input {
  width: 100%;
}

.create-btn {
  margin-top: 10px;
  width: 100%;
  cursor: pointer;
}
</style>
