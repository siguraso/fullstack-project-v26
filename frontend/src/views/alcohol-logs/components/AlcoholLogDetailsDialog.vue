<script setup lang="ts">
import type { AlcoholLog } from '@/interfaces/AlcoholLog.interface'
import InfoCard from '@/components/ui/InfoCard.vue'
import { ScrollText } from '@lucide/vue'

defineProps<{
  log: AlcoholLog | null
  isOpen: boolean
}>()

const emit = defineEmits<{
  (event: 'close'): void
}>()

function formatLogType(type: string): string {
  return type
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
  <Teleport to="body">
    <div v-if="isOpen && log" class="dialog-overlay" @click.self="emit('close')">
      <InfoCard
        class="dialog-content"
        :title="log.title"
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
            <span class="label">Type</span>
            <span class="value">{{ formatLogType(log.logType) }}</span>
          </div>

          <div class="detail-item">
            <span class="label">Status</span>
            <span class="value">{{ log.status ?? 'Not set' }}</span>
          </div>

          <div class="detail-item">
            <span class="label">Recorded At</span>
            <span class="value">{{ formatDate(log.recordedAt ?? log.createdAt) }}</span>
          </div>

          <div class="detail-item">
            <span class="label">Recorded By</span>
            <span class="value">{{
              log.recordedByName ?? (log.recordedById ? `User #${log.recordedById}` : 'Unknown')
            }}</span>
          </div>

          <div class="detail-item">
            <span class="label">ID Checked</span>
            <span class="value">{{
              log.idChecked === null || log.idChecked === undefined
                ? 'Not set'
                : log.idChecked
                  ? 'Yes'
                  : 'No'
            }}</span>
          </div>

          <div class="detail-item">
            <span class="label">Service Refused</span>
            <span class="value">{{
              log.serviceRefused === null || log.serviceRefused === undefined
                ? 'Not set'
                : log.serviceRefused
                  ? 'Yes'
                  : 'No'
            }}</span>
          </div>

          <div class="detail-item">
            <span class="label">Estimated Age</span>
            <span class="value">{{ log.estimatedAge ?? 'Not set' }}</span>
          </div>
        </div>

        <div class="description" v-if="log.description">
          <h4>Description</h4>
          <p>{{ log.description }}</p>
        </div>
      </InfoCard>
    </div>
  </Teleport>
</template>

<style scoped>
.dialog-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: grid;
  place-items: center;
  z-index: 1000;
  padding: 16px;
  overflow-y: auto;
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
  .dialog-overlay {
    align-items: flex-start;
    overflow-y: auto;
    padding: 12px;
  }

  .details-grid {
    grid-template-columns: 1fr;
  }
}
</style>
