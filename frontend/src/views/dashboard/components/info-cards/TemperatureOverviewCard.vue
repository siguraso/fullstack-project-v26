<script lang="ts" setup>
import { ref } from 'vue'

import { Thermometer } from '@lucide/vue'
import InfoCard from './InfoCard.vue'

const numberOfCriticalLocations = ref(2) // TODO replace with actual data fetching
const criticalLocations = ref(['Freezer 1', 'Fridge 2']) // TODO replace with actual data fetching
</script>

<template>
  <InfoCard
    v-if="numberOfCriticalLocations == 0"
    class="info-card"
    title="Temperature Overview"
    :icon="Thermometer"
    cardColor="{{ headerColor }}"
  >
    <h3>Everything is good</h3>
    <p>Current Temperature</p>
    <p>No critical logs in the last 24 hours.</p>
  </InfoCard>
  <InfoCard
    v-else
    class="info-card"
    title="Temperature Overview"
    :icon="Thermometer"
    cardColor="var(--cta-red)"
  >
    <h3 class="critical-header">{{ numberOfCriticalLocations }} Critical Temperature Issues:</h3>
    <p>{{ criticalLocations.join(', ') }}</p>
    <p class="critical-subtext">Action Required</p>
  </InfoCard>
</template>

<style scoped>
.critical-header {
  color: var(--cta-red);
  font-weight: bold;
}

.critical-subtext {
  color: var(--text-secondary);
  font-size: 0.9rem;
}
</style>
