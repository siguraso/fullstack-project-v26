<script setup lang="ts">
import Card from '@/components/ui/Card.vue'
import { resolveDeviation } from '@/services/deviation'
import { TriangleAlert } from '@lucide/vue'

const props = defineProps<{
  alerts: any[]
}>()

const emit = defineEmits(['resolved'])

async function handleResolve(id: number) {
  try {
    await resolveDeviation(id)
    emit('resolved')
  } catch (e) {
    console.error('Failed to resolve alert', e)
  }
}
</script>

<template>
  <Card class="critical-container">
    <template #card-header>
      <div class="header-row">
        <div class="header-label">
          <TriangleAlert :size="30" aria-hidden="true" />
          <h3>Critical Alerts ({{ alerts.length }})</h3>
        </div>
      </div>
    </template>

    <template #card-content>
      <TransitionGroup name="alert" tag="ul" class="alerts-list">
        <li v-for="alert in alerts" :key="alert.id" class="alert-item">
          <span class="alert-icon-wrapper">
            <TriangleAlert :size="24" class="alert-icon" />
          </span>

          <div class="alert-copy">
            <p class="alert-title">{{ alert.title }}</p>
            <p class="alert-details">{{ alert.description }}</p>
          </div>

          <button class="alert-action" @click="handleResolve(alert.id)">Resolve</button>
        </li>
      </TransitionGroup>
    </template>
  </Card>
</template>

<style scoped>
.critical-container {
  background-color: #f5dcdc;
  border-left: 4px solid #c92a2a;
  margin-bottom: 20px;
}

.header-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.header-label {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  color: #9b111e;
  margin: 0;
}

.header-label h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 800;
}

.action-required {
  border: 2px solid #c92a2a;
  background: transparent;
  color: #c92a2a;
  font-size: 14px;
  font-weight: 800;
  letter-spacing: 0.02em;
  padding: 10px 16px;
  border-radius: 4px;
  line-height: 1;
}

.alerts-list {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 18px;
  background-color: #f3e8e8;
  border-radius: 6px;
  padding: 18px 20px;
}

.alert-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 4px;
  background-color: #ead5d5;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.alert-icon {
  color: #c92a2a;
}

.alert-copy {
  min-width: 0;
}

.alert-title {
  margin: 0;
  font-size: 18px;
  font-weight: 800;
  color: #242424;
  line-height: 1.2;
}

.alert-details {
  margin: 4px 0 0;
  color: #4b4b4b;
  font-size: 14px;
  line-height: 1.3;
}

.alert-action {
  margin-left: auto;
  border: 0;
  background: transparent;
  color: #c92a2a;
  font-size: 14px;
  font-weight: 800;
  letter-spacing: 0.01em;
  text-decoration: underline;
  text-underline-offset: 3px;
  white-space: nowrap;
}

@media (max-width: 900px) {
  .header-row {
    flex-direction: column;
    align-items: flex-start;
  }

  .alert-item {
    flex-wrap: wrap;
    align-items: flex-start;
  }

  .alert-action {
    margin-left: 66px;
  }
}

/* ANIMATION */
.alert-leave-active {
  transition: all 0.25s ease;
}

.alert-leave-to {
  opacity: 0;
  transform: translateX(20px);
}

.alert-move {
  transition: transform 0.25s ease;
}
</style>
