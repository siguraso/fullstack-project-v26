<script lang="ts" setup>
import {
  ChevronRight,
  Thermometer,
  TriangleAlert,
  ClipboardCheck,
  BadgeCheck,
  Activity,
} from '@lucide/vue'
import InfoCard from '@/components/ui/InfoCard.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const quickActions = [
  {
    label: 'New Temperature Log',
    route: { name: 'temperature-logs' },
    icon: Thermometer,
  },
  {
    label: 'New Incident Report',
    route: { name: 'deviation' },
    icon: TriangleAlert,
  },
  {
    label: 'Compliance Checklists',
    route: { name: 'checklist' },
    icon: ClipboardCheck,
  },
  {
    label: 'Log ID Verification',
    route: { name: 'logs' },
    icon: BadgeCheck,
  },
]
</script>

<template>
  <InfoCard
    class="info-card"
    title="Quick Actions"
    :icon="Activity"
    :iconBackgroundColor="'#b5fed1'"
  >
    <div class="action-list">
      <button
        v-for="action in quickActions"
        :key="action.label"
        class="action-button"
        @click="router.push(action.route)"
      >
        <span class="button-label">
          <component :is="action.icon" :size="20" aria-hidden="true" />
          {{ action.label }}
          <ChevronRight :size="16" aria-hidden="true" class="arrow-icon" />
        </span>
      </button>
    </div>
  </InfoCard>
</template>

<style scoped>
.title {
  color: var(--quaternary);
}

.action-list {
  display: grid;
  grid-template-columns: auto auto;
  gap: 10px;
}

.action-button {
  width: 100%;
  padding: 10px;
  color: var(--neutral);
  border: none;
  border-radius: 5px;
  cursor: pointer;
  text-align: left;
  font-size: 12px;
  background-color: var(--bg);
  transition:
    background-color 220ms ease,
    color 220ms ease;
}

.action-button:hover {
  background-color: var(--bg-hover);
}

.action-button:active {
  background-color: var(--bg-active);
}

.button-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.arrow-icon {
  margin-left: auto;
}
</style>
