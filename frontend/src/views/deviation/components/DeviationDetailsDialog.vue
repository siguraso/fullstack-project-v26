<script setup lang="ts">
import type { Deviation } from '@/interfaces/Deviation.interface'
import InfoCard from '@/components/ui/InfoCard.vue'
import { ScrollText } from '@lucide/vue'
import { ref } from 'vue'
import { resolveDeviation } from '@/services/deviation'

const props = defineProps<{
  deviation: Deviation | null
  isOpen: boolean
}>()

const emit = defineEmits<{
  (event: 'close'): void
  (event: 'resolved'): void
}>()

const isResolving = ref(false)

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

function formatShortDate(date?: string): string {
  if (!date) return 'Not available'

  const parsed = new Date(date)
  if (Number.isNaN(parsed.getTime())) {
    return date
  }

  return parsed.toLocaleDateString('en-GB')
}

function formatText(value?: string): string {
  return value && value.trim() ? value.trim() : 'Not available'
}

async function handleResolve() {
  if (!props.deviation?.id || isResolving.value) {
    return
  }

  isResolving.value = true

  try {
    await resolveDeviation(props.deviation.id)
    emit('resolved')
  } catch (error) {
    console.error('Failed to resolve deviation', error)
  } finally {
    isResolving.value = false
  }
}

const summaryFields = [
  { label: 'Category', getValue: (deviation: Deviation) => formatCategory(deviation.category) },
  { label: 'Severity', getValue: (deviation: Deviation) => deviation.severity },
  { label: 'Module', getValue: (deviation: Deviation) => formatModule(deviation.module) },
  { label: 'Status', getValue: (deviation: Deviation) => formatStatus(deviation.status) },
  { label: 'Created At', getValue: (deviation: Deviation) => formatDate(deviation.createdAt) },
]

const detailFields = [
  {
    label: 'Reported Date',
    getValue: (deviation: Deviation) => formatShortDate(deviation.reportedDate),
  },
  {
    label: 'Discovered By',
    getValue: (deviation: Deviation) => formatText(deviation.discoveredBy),
  },
  { label: 'Reported To', getValue: (deviation: Deviation) => formatText(deviation.reportedTo) },
  { label: 'Assigned To', getValue: (deviation: Deviation) => formatText(deviation.assignedTo) },
  {
    label: 'Issue Description',
    getValue: (deviation: Deviation) => formatText(deviation.issueDescription),
    multiline: true,
  },
  {
    label: 'Immediate Action',
    getValue: (deviation: Deviation) => formatText(deviation.immediateAction),
    multiline: true,
  },
  {
    label: 'Root Cause',
    getValue: (deviation: Deviation) => formatText(deviation.rootCause),
    multiline: true,
  },
  {
    label: 'Corrective Action',
    getValue: (deviation: Deviation) => formatText(deviation.correctiveAction),
    multiline: true,
  },
  {
    label: 'Completion Notes',
    getValue: (deviation: Deviation) => formatText(deviation.completionNotes),
    multiline: true,
  },
]
</script>

<template>
  <Teleport to="body">
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
          <div class="header-actions">
            <button
              v-if="props.deviation?.status !== 'RESOLVED'"
              type="button"
              class="resolve-btn"
              :disabled="isResolving"
              @click="handleResolve"
            >
              {{ isResolving ? 'Resolving...' : 'Resolve' }}
            </button>
            <button type="button" class="close-btn" @click="emit('close')">Close</button>
          </div>
        </template>

        <div class="dialog-body">
          <section class="summary-grid" aria-label="Deviation summary">
            <article v-for="field in summaryFields" :key="field.label" class="summary-card">
              <span class="field-label">{{ field.label }}</span>
              <span class="summary-value">{{ field.getValue(deviation) }}</span>
            </article>
          </section>

          <section class="field-grid" aria-label="Deviation details">
            <article
              v-for="field in detailFields"
              :key="field.label"
              :class="['field-card', { 'field-card--multiline': field.multiline }]"
            >
              <span class="field-label">{{ field.label }}</span>
              <p class="field-value">{{ field.getValue(deviation) }}</p>
            </article>
          </section>
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
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  overflow-y: auto;
  z-index: 1000;
}

.dialog-content {
  width: min(720px, calc(100vw - 48px));
  max-height: calc(100vh - 48px);
  overflow: auto;
  margin: 0;
}

.dialog-body {
  display: grid;
  gap: 18px;
}

.header-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.close-btn {
  border: 1px solid color-mix(in srgb, var(--text) 14%, transparent);
  background: color-mix(in srgb, var(--bg) 84%, white);
  color: var(--text);
  border-radius: 999px;
  padding: 8px 14px;
  cursor: pointer;
  transition:
    transform 0.15s ease,
    box-shadow 0.15s ease,
    background 0.15s ease;
}

.close-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 16px rgba(15, 23, 42, 0.08);
  background: #fff;
}

.resolve-btn {
  border: 1px solid #c96f3a;
  background: #fff4ec;
  color: #8f4b1d;
  border-radius: 999px;
  padding: 8px 14px;
  cursor: pointer;
  transition:
    transform 0.15s ease,
    box-shadow 0.15s ease,
    background 0.15s ease;
}

.resolve-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 16px rgba(15, 23, 42, 0.08);
  background: #ffe9da;
}

.resolve-btn:disabled {
  cursor: wait;
  opacity: 0.7;
  transform: none;
  box-shadow: none;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 12px;
}

.summary-card,
.field-card {
  border: 1px solid var(--border);
  border-radius: 14px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.06);
}

.summary-card {
  padding: 12px 14px;
  display: grid;
  gap: 4px;
}

.field-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.field-card {
  padding: 14px;
  display: grid;
  gap: 8px;
  min-height: 96px;
}

.field-card--multiline {
  grid-column: 1 / -1;
  min-height: 120px;
}

.field-label {
  color: var(--text-secondary);
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

.summary-value {
  color: var(--text);
  font-weight: 700;
  line-height: 1.35;
}

.field-value {
  margin: 0;
  color: var(--text);
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
}

.field-value:empty::before {
  content: 'Not available';
  color: var(--text-secondary);
}

@media (max-width: 740px) {
  .summary-grid,
  .field-grid {
    grid-template-columns: 1fr;
  }

  .field-card--multiline {
    grid-column: auto;
  }
}
</style>
