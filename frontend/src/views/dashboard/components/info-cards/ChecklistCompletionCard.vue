<script lang="ts" setup>
import InfoCard from './InfoCard.vue'
import { ClipboardCheck } from '@lucide/vue'
import { computed } from 'vue'

// TODO add data fetching
const checklistCompleted = 75
const checklistTotal = 100

function calculateCompletionRate(completed: number, total: number): number {
  if (total === 0) return 100
  return (completed / total) * 100
}

const completionRate = computed(() =>
  Math.min(100, Math.max(0, calculateCompletionRate(checklistCompleted, checklistTotal))),
)

const completionRateLabel = computed(() => `${Math.round(completionRate.value)}% complete`)
</script>

<template>
  <InfoCard
    class="info-card"
    title="Checklist Completion"
    :icon="ClipboardCheck"
    cardColor="var(--secondary)"
  >
    <div class="checklist-label">
      <h3>{{ checklistCompleted }} / {{ checklistTotal }}</h3>
      <p>Tasks Completed</p>
    </div>

    <div
      class="progress"
      role="progressbar"
      :aria-valuenow="Math.round(completionRate)"
      aria-valuemin="0"
      aria-valuemax="100"
      :aria-label="completionRateLabel"
    >
      <div class="progress-fill" :style="{ width: `${completionRate}%` }" />
    </div>
    <p class="progress-text">{{ completionRateLabel }}</p>
  </InfoCard>
</template>

<style scoped>
.checklist-label h3,
.checklist-label p {
  margin: 0;
}

.checklist-label p {
  color: var(--text-secondary);
  font-size: 0.9rem;
  margin-bottom: 12px;
  margin-top: 4px;
}

.progress {
  width: 100%;
  height: 5px;
  border-radius: 999px;
  background-color: color-mix(in srgb, var(--stroke) 65%, white);
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-fill {
  height: 100%;
  background-color: var(--neutral);
  border-radius: inherit;
  transition: width 220ms ease;
}

.progress-text {
  margin: 0;
  font-size: 0.9rem;
  color: var(--text-secondary);
}
</style>
