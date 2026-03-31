<script setup lang="ts">
import { ref } from 'vue'
import { AlertTriangle } from '@lucide/vue'
import InfoCard from '@/components/ui/InfoCard.vue'

const numberOfActiveDeviations = ref(3) // TODO replace with actual data fetching
const numberOfWeeklyDeviations = ref(15) // TODO replace with actual data fetching
const numberOfClosedDeviations = ref(12) // TODO replace with actual data fetching
</script>

<template>
  <InfoCard
    class="info-card"
    title="Deviations"
    :icon="AlertTriangle"
    iconBackgroundColor="#feddb5"
  >
    <h3>{{ numberOfActiveDeviations }}</h3>
    <p>Active Deviations</p>
    <p class="deviation-subtext">
      {{ numberOfWeeklyDeviations }} this week, {{ numberOfClosedDeviations }} closed
    </p>
  </InfoCard>
</template>

<style scoped>
.deviation-subtext {
  color: var(--text-secondary);
  font-size: 0.9rem;
}
</style>
