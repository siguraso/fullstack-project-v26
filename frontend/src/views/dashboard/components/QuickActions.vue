<script lang="ts" setup>
import Card from '@/components/ui/Card.vue'
import { ArrowRight, Thermometer, TriangleAlert, ClipboardCheck, BadgeCheck } from '@lucide/vue'

const quickActions = [
  {
    label: 'New Temperature Log',
    action: () => console.log('New Temperature Log'),
    icon: Thermometer,
  },
  {
    label: 'New Incident Report',
    action: () => console.log('New Incident Report'),
    icon: TriangleAlert,
  },
  {
    label: 'Compliance Checklists',
    action: () => console.log('Compliance Checklists'),
    icon: ClipboardCheck,
  },
  {
    label: 'Log ID Verification',
    action: () => console.log('Log ID Verification'),
    icon: BadgeCheck,
  },
]
</script>

<template>
  <Card class="quick-actions">
    <template #card-header> Quick Actions </template>

    <template #card-content>
      <div class="action-list">
        <button
          v-for="action in quickActions"
          :key="action.label"
          class="action-button"
          @click="action.action"
        >
          <span class="button-label">
            <component :is="action.icon" :size="16" aria-hidden="true" />
            {{ action.label }}
            <ArrowRight :size="15" class="arrow-icon" />
          </span>
        </button>
      </div>
    </template>
  </Card>
</template>

<style scoped>
.action-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-button {
  width: 100%;
  padding: 10px;
  background-color: var(--primary);
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  text-align: left;
  transition:
    background-color 220ms ease,
    color 220ms ease;
}

.action-button:hover {
  background-color: var(--primary-hover);
}

.action-button:active {
  background-color: var(--primary-active);
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
