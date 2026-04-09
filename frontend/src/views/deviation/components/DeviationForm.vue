<script setup lang="ts">
import InfoCard from '@/components/ui/InfoCard.vue'
import { Edit2 } from '@lucide/vue'
import { computed, watchEffect } from 'vue'
import { useDeviationStore } from '@/stores/deviation'
import type { Deviation, DeviationModule, DeviationSeverity } from '@/interfaces/Deviation.interface'

const store = useDeviationStore()

const props = withDefaults(
  defineProps<{
    lockedCategory?: Deviation['category']
    lockedModule?: DeviationModule
    title?: string
    submitLabel?: string
    footerText?: string
    showInternalTracking?: boolean
    useExternalSubmit?: boolean
    isSubmitting?: boolean
    maxCardHeight?: string
  }>(),
  {
    submitLabel: 'Submit Deviation',
    footerText: 'The form saves each deviation field separately for incident follow-up.',
    showInternalTracking: true,
    useExternalSubmit: false,
    isSubmitting: undefined,
    maxCardHeight: 'calc(100dvh - 140px)',
  },
)

const emit = defineEmits<{
  (event: 'submit'): void
}>()

const severityOptions: Array<{ value: Exclude<DeviationSeverity, 'MEDIUM'>; label: string }> = [
  { value: 'LOW', label: 'Minor' },
  { value: 'HIGH', label: 'Major' },
  { value: 'CRITICAL', label: 'Critical' },
]

const categoryLabels: Record<string, string> = {
  TEMPERATURE: 'Temperature',
  HYGIENE: 'Hygiene',
  ALCOHOL: 'Alcohol',
  DOCUMENTATION: 'Documentation',
  OTHER: 'Other',
}

watchEffect(() => {
  if (props.lockedCategory) {
    store.form.category = props.lockedCategory
  }

  if (props.lockedModule) {
    store.form.module = props.lockedModule
  }
})

const selectedLabel = computed(
  () => categoryLabels[props.lockedCategory ?? store.form.category] ?? 'General',
)
const heading = computed(() => props.title ?? `${selectedLabel.value} Deviation Registration`)
const submitDisabled = computed(() => props.isSubmitting ?? store.submitting)

function handleSubmit() {
  if (props.useExternalSubmit) {
    emit('submit')
    return
  }

  void store.createDeviation()
}
</script>

<template>
  <InfoCard
    class="card"
    :title="heading"
    :icon="Edit2"
    iconBackgroundColor="var(--icon-bg-purple)"
    iconColor="var(--icon-stroke-purple)"
  >
    <form class="form-shell" @submit.prevent="handleSubmit">
      <div class="sheet-grid">
        <label class="field-wide">
          <span>Deviation form for *</span>
          <input
            v-model="store.form.title"
            placeholder="e.g., Cold storage, dishwashing area, bar service"
          />
        </label>

        <div class="field-wide assessment-card">
          <span>This deviation is assessed as</span>
          <div class="severity">
            <button
              v-for="option in severityOptions"
              :key="option.value"
              type="button"
              :class="[
                'severity-btn',
                option.value.toLowerCase(),
                { active: store.form.severity === option.value },
              ]"
              @click="store.form.severity = option.value"
            >
              {{ option.label }}
            </button>
          </div>
        </div>

        <label>
          <span>Date</span>
          <input v-model="store.form.reportedDate" type="date" />
        </label>

        <label>
          <span>Who discovered the issue</span>
          <input v-model="store.form.discoveredBy" placeholder="Name" />
        </label>

        <label>
          <span>Who the issue was reported to</span>
          <input v-model="store.form.reportedTo" placeholder="Name / role" />
        </label>

        <label>
          <span>Who should handle the deviation</span>
          <input v-model="store.form.assignedTo" placeholder="Responsible person" />
        </label>
      </div>

      <section class="section-card">
        <div class="section-header">
          <h3>Describe the issue / what went wrong</h3>
        </div>
        <textarea
          v-model="store.form.issueDescription"
          rows="5"
          placeholder="Describe the deviation, where it happened, and the immediate impact."
        />
      </section>

      <section class="section-card">
        <div class="section-header">
          <h3>Immediate handling / what did you do right away</h3>
        </div>
        <textarea
          v-model="store.form.immediateAction"
          rows="4"
          placeholder="Document the first actions taken to contain or reduce the issue."
        />
      </section>

      <section class="section-card">
        <div class="section-header">
          <h3>What could be the reason / cause of the issue</h3>
        </div>
        <textarea
          v-model="store.form.rootCause"
          rows="4"
          placeholder="Explain the likely cause or chain of events behind the deviation."
        />
      </section>

      <section class="section-card">
        <div class="section-header">
          <h3>Corrective action / how to prevent the same issue from happening again</h3>
        </div>
        <textarea
          v-model="store.form.correctiveAction"
          rows="4"
          placeholder="Describe the corrective action and prevention plan."
        />
      </section>

      <section class="section-card">
        <div class="section-header">
          <h3>Corrective action has been completed</h3>
        </div>
        <textarea
          v-model="store.form.completionNotes"
          rows="3"
          placeholder="Confirm what has been completed and any remaining follow-up."
        />
      </section>

      <section v-if="showInternalTracking" class="system-section">
        <h3>Internal tracking</h3>
        <div class="system-grid">
          <label v-if="!lockedCategory">
            <span>Category</span>
            <select v-model="store.form.category">
              <option value="TEMPERATURE">Temperature</option>
              <option value="HYGIENE">Hygiene</option>
              <option value="ALCOHOL">Alcohol</option>
              <option value="DOCUMENTATION">Documentation</option>
              <option value="OTHER">Other</option>
            </select>
          </label>

          <label>
            <span>Status</span>
            <select v-model="store.form.status">
              <option value="OPEN">Open</option>
              <option value="IN_PROGRESS">In Progress</option>
              <option value="RESOLVED">Resolved</option>
            </select>
          </label>

          <label v-if="!lockedModule">
            <span>Module</span>
            <select v-model="store.form.module">
              <option value="IK_FOOD">IK Food</option>
              <option value="IK_ALCOHOL">IK Alcohol</option>
            </select>
          </label>
        </div>
      </section>

      <p v-if="store.error" class="error-message">{{ store.error }}</p>

      <div class="footer">
        <small>{{ footerText }}</small>
        <button class="submit" type="submit" :disabled="submitDisabled">
          {{ submitDisabled ? 'Submitting...' : submitLabel }}
        </button>
      </div>
    </form>
  </InfoCard>
</template>

<style scoped>
.card {
  margin: 0;
}

.form-shell {
  display: grid;
  gap: 18px;
}

.sheet-grid,
.system-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.field-wide {
  grid-column: 1 / -1;
}

label,
.assessment-card {
  display: grid;
  gap: 8px;
}

label > span,
.assessment-card > span {
  font-size: 0.9em;
  color: var(--text-secondary);
  margin-bottom: 2px;
}

.assessment-card,
.section-card,
.system-section {
  border: 1px solid var(--border);
  border-radius: 12px;
  background: linear-gradient(180deg, #fdfdfd 0%, #f8fafc 100%);
  padding: 16px;
}

.severity {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
}

.severity-btn {
  min-height: 38px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: #ffffff;
  color: var(--text-secondary);
  letter-spacing: 0.02em;
}

.severity-btn.low.active {
  background: #e9fff2;
  border-color: #23975c;
  color: #1a1c1e;
}

.severity-btn.high.active {
  background: #fff0e9;
  border-color: #c96f3a;
  color: #1a1c1e;
}

.severity-btn.critical.active {
  background: var(--cta-red-btn);
  border-color: var(--cta-red-btn);
  color: var(--bg);
}

.section-card {
  display: grid;
  gap: 12px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.section-header h3,
.system-section h3 {
  margin: 0;
  font-size: 15px;
  color: var(--text);
}

textarea {
  width: 100%;
  resize: vertical;
  min-height: 100px;
  padding: 12px;
}

.system-section {
  display: grid;
  gap: 12px;
}

.error-message {
  margin: 0;
  border: 1px solid var(--cta-red);
  background: #fff0f0;
  color: var(--cta-red);
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 13px;
}

.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

small {
  color: var(--text-secondary);
  font-size: 11px;
}

.submit {
  min-height: 42px;
  min-width: 200px;
}

@media (max-width: 1020px) {
  .sheet-grid,
  .system-grid {
    grid-template-columns: 1fr;
  }

  .section-header,
  .footer {
    flex-direction: column;
    align-items: stretch;
  }


  .severity {
    grid-template-columns: 1fr;
  }
}

</style>
