<script setup lang="ts">
import type { Deviation } from '@/interfaces/Deviation.interface'
import InfoCard from '@/components/ui/InfoCard.vue'
import { ScrollText } from '@lucide/vue'

defineProps<{
  deviation: Deviation | null
  isOpen: boolean
}>()

const emit = defineEmits<{
  (event: 'close'): void
}>()

function formatCategory(value: Deviation['category']): string {
  return value
    .split('_')
    .map((word) => word.charAt(0) + word.slice(1).toLowerCase())
    .join(' ')
}

function formatStatus(value: Deviation['status']): string {
  return value
    .split('_')
    .map((word) => word.charAt(0) + word.slice(1).toLowerCase())
    .join(' ')
}

function formatModule(value: Deviation['module']): string {
  return value
    .split('_')
    .map((word) => word.charAt(0) + word.slice(1).toLowerCase())
    .join(' ')
}

function formatDate(date?: string): string {
  if (!date) return 'Not available'

  return new Date(date).toLocaleString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}
</script>

<template>
  <div v-if="isOpen && deviation" class="dialog-overlay" @click.self="emit('close')">
    <InfoCard
      class="dialog-content"
      :title="deviation.title"
      :icon="ScrollText"
      iconBackgroundColor="var(--neutral)"
      iconColor="white"
      :addToHeader="true"
    >
      <template #extra-header-content>
        <button type="button" class="close-btn" @click="emit('close')">Close</button>
      </template>

      <div class="details-grid">
        <div class="detail-item">
          <span class="label">Category</span>
          <span class="value">{{ formatCategory(deviation.category) }}</span>
        </div>

        <div class="detail-item">
          <span class="label">Severity</span>
          <span class="value">{{ deviation.severity }}</span>
        </div>

        <div class="detail-item">
          <span class="label">Module</span>
          <span class="value">{{ formatModule(deviation.module) }}</span>
        </div>

        <div class="detail-item">
          <span class="label">Status</span>
          <span class="value">{{ formatStatus(deviation.status) }}</span>
        </div>

        <div class="detail-item">
          <span class="label">Created At</span>
          <span class="value">{{ formatDate(deviation.createdAt) }}</span>
        </div>
      </div>

      <div v-if="deviation.description" class="description">
        <h4>Description</h4>
        <p>{{ deviation.description }}</p>
      </div>
    </InfoCard>
  </div>
</template>

<style scoped>
.dialog-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: grid;
  place-items: center;
  z-index: 1000;
}

.dialog-content {
  width: min(720px, 92vw);
  max-height: 88vh;
  overflow: auto;
  margin: 0;
}

.close-btn {
  margin-left: auto;
  border: 1px solid var(--border);
  background: var(--bg);
  color: var(--text);
  border-radius: 8px;
  padding: 8px 12px;
  cursor: pointer;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.detail-item {
  background: var(--bg);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.label {
  color: var(--text-secondary);
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.value {
  color: var(--text);
  font-weight: 600;
}

.description {
  margin-top: 14px;
  border-top: 1px solid var(--border);
  padding-top: 12px;
}

.description h4 {
  margin: 0 0 8px;
  color: var(--text);
}

.description p {
  margin: 0;
  color: var(--text-secondary);
  line-height: 1.45;
}

@media (max-width: 740px) {
  .details-grid {
    grid-template-columns: 1fr;
  }
}
</style>
