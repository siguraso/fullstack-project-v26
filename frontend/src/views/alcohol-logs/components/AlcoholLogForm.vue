<script setup lang="ts">
import { Edit2 } from '@lucide/vue'
import type { AlcoholLog, AlcoholLogInput } from '@/interfaces/AlcoholLog.interface'
import { createAlcoholLog } from '@/services/alcoholLog'
import { computed, ref, watch } from 'vue'

const emit = defineEmits<{
  (event: 'created', log: AlcoholLog): void
}>()

const ALCOHOL_LOG_TYPES = [
  { value: 'AGE_VERIFICATION', label: 'Age Verification' },
  { value: 'SERVICE_REFUSAL', label: 'Service Refusal' },
  { value: 'CLOSING_STOCK', label: 'Closing Stock' },
  { value: 'TRAINING', label: 'Training' },
  { value: 'INCIDENT', label: 'Incident' },
  { value: 'OTHER', label: 'Other' },
] as const

const title = ref<string>('')
const description = ref<string>('')
const logType = ref<string>('')
const status = ref<string>('')
const idChecked = ref<boolean>(false)
const serviceRefused = ref<boolean>(false)
const estimatedAge = ref<number | null>(null)

const isSubmitting = ref(false)
const error = ref<string>('')

const shouldShowIdChecked = computed(() =>
  ['AGE_VERIFICATION', 'SERVICE_REFUSAL', 'INCIDENT'].includes(logType.value),
)
const shouldShowServiceRefused = computed(() =>
  ['SERVICE_REFUSAL', 'INCIDENT'].includes(logType.value),
)
const shouldShowEstimatedAge = computed(() =>
  ['AGE_VERIFICATION', 'SERVICE_REFUSAL', 'INCIDENT'].includes(logType.value),
)

watch(logType, () => {
  if (!shouldShowIdChecked.value) {
    idChecked.value = false
  }

  if (!shouldShowServiceRefused.value) {
    serviceRefused.value = false
  }

  if (!shouldShowEstimatedAge.value) {
    estimatedAge.value = null
  }
})

async function createLog() {
  error.value = ''

  if (!title.value.trim()) {
    error.value = 'Title is required'
    return
  }

  if (!logType.value) {
    error.value = 'Log type is required'
    return
  }

  const payload: AlcoholLogInput = {
    title: title.value.trim(),
    description: description.value.trim() || null,
    logType: logType.value as any,
    status: status.value ? (status.value as any) : null,
    idChecked: shouldShowIdChecked.value ? (idChecked.value || null) : null,
    serviceRefused: shouldShowServiceRefused.value ? (serviceRefused.value || null) : null,
    estimatedAge: shouldShowEstimatedAge.value ? (estimatedAge.value || null) : null,
  }

  isSubmitting.value = true

  try {
    const createdLog = await createAlcoholLog(payload)
    emit('created', createdLog)

    title.value = ''
    description.value = ''
    logType.value = ''
    status.value = ''
    idChecked.value = false
    serviceRefused.value = false
    estimatedAge.value = null
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Failed to create alcohol log'
    console.error('Failed to create alcohol log', err)
  } finally {
    isSubmitting.value = false
  }
}
</script>

<template>
  <div class="card">
    <div class="card-head">
      <div class="title-wrap">
        <div class="title-icon">
          <Edit2 :size="20" aria-hidden="true" />
        </div>
        <h3 class="title-text">Create Alcohol Log</h3>
      </div>
    </div>

    <form @submit.prevent="createLog" class="form-grid">
      <label>
        <span>Log title *</span>
        <input v-model="title" placeholder="e.g., Age verification at entry" />
      </label>

      <label>
        <span>Log type *</span>
        <select v-model="logType" required>
          <option value="">Select log type...</option>
          <option v-for="type in ALCOHOL_LOG_TYPES" :key="type.value" :value="type.value">
            {{ type.label }}
          </option>
        </select>
      </label>

      <label>
        <span>Status</span>
        <select v-model="status">
          <option value="">No Status</option>
          <option value="OK">OK</option>
          <option value="WARNING">Warning</option>
          <option value="CRITICAL">Critical</option>
        </select>
      </label>

      <label class="field-wide">
        <span>Description</span>
        <textarea
          v-model="description"
          maxlength="500"
          placeholder="Additional details about the alcohol compliance log..."
        />
      </label>

      <label v-if="shouldShowIdChecked || shouldShowServiceRefused" class="field-wide checkboxes">
        <span>Flags</span>
        <div class="checkbox-group">
          <label v-if="shouldShowIdChecked" class="checkbox-label">
            <input v-model="idChecked" type="checkbox" />
            <span>ID Checked</span>
          </label>
          <label v-if="shouldShowServiceRefused" class="checkbox-label">
            <input v-model="serviceRefused" type="checkbox" />
            <span>Service Refused</span>
          </label>
        </div>
      </label>

      <label v-if="shouldShowEstimatedAge">
        <span>Estimated Age</span>
        <input v-model.number="estimatedAge" type="number" min="0" max="150" placeholder="Age" />
      </label>

      <div class="field-wide error-message" v-if="error">
        {{ error }}
      </div>
    </form>

    <div class="footer">
      <button class="submit" type="button" :disabled="isSubmitting" @click="createLog">
        {{ isSubmitting ? 'Creating...' : 'Create log >' }}
      </button>
    </div>
  </div>
</template>

<style scoped>
.card {
  background: var(--surface);
  padding: 22px;
  border-radius: 14px;
  border: 1px solid var(--border);
  box-shadow: var(--shadow-soft);
}

.card-head {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  gap: 16px;
  margin-bottom: 18px;
}

.title-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
}

.title-icon {
  width: 36px;
  height: 36px;
  border-radius: 9px;
  background: var(--neutral);
  color: #ffffff;
  display: grid;
  place-items: center;
}

.title-text {
  margin: 0;
  color: var(--text);
  font-size: 24px;
  line-height: 1.15;
  font-weight: 800;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 14px;
}

label {
  display: grid;
  gap: 6px;
}

label span {
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  color: var(--text-secondary);
}

input,
select {
  font-size: 14px;
  padding: 10px 12px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--bg);
  color: var(--text);
  font-family: inherit;
}

input:focus,
select:focus {
  outline: none;
  border-color: var(--neutral);
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.08);
}

textarea {
  font-size: 14px;
  padding: 10px 12px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--bg);
  color: var(--text);
  font-family: inherit;
  min-height: 80px;
  resize: vertical;
}

textarea:focus {
  outline: none;
  border-color: var(--neutral);
  box-shadow: 0 0 0 2px rgba(0, 0, 0, 0.08);
}

.field-wide {
  grid-column: 1 / -1;
}

.checkboxes span {
  margin-bottom: 8px;
}

.checkbox-group {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: normal;
  text-transform: none;
  letter-spacing: normal;
  color: var(--text);
}

.checkbox-label input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  padding: 0;
}

.error-message {
  color: var(--cta-red);
  font-size: 13px;
  padding: 10px 12px;
  background: #fff0f0;
  border: 1px solid var(--cta-red);
  border-radius: 8px;
  margin-top: 4px;
}

.footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 20px;
  padding-top: 18px;
  border-top: 1px solid var(--border);
}

.submit {
  padding: 10px 16px;
  background: var(--neutral);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.04em;
  cursor: pointer;
  transition: all 0.2s;
}

.submit:hover:not(:disabled) {
  filter: brightness(0.9);
}

.submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>











