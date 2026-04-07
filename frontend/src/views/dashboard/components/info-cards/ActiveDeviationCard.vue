<script setup lang="ts">
import { ref } from 'vue'
import { AlertTriangle } from '@lucide/vue'
import InfoCard from '@/components/ui/InfoCard.vue'

const props = defineProps<{
  active: number
  weekly: number
  closed: number
}>()
</script>

<template>
  <InfoCard
    class="info-card"
    title="Deviations"
    :icon="AlertTriangle"
    iconBackgroundColor="#feddb5"
  >
    <h3>{{ props.active }}</h3>
    <p>Active Deviations</p>
    <p class="deviation-subtext">{{ props.weekly }} this week, {{ props.closed }} closed</p>
  </InfoCard>
</template>

<style scoped>
.deviation-subtext {
  color: var(--text-secondary);
  font-size: 0.9rem;
}
</style>
