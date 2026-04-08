<script setup lang="ts">
import { Edit2 } from '@lucide/vue'
import { computed, watchEffect } from 'vue'
import { useDeviationStore, type Deviation } from '@/stores/deviation'

const store = useDeviationStore()

const props = defineProps<{
  lockedCategory?: Deviation['category']
  title?: string
}>()

const levels = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'] as const

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
})

const selectedLabel = computed(
  () => categoryLabels[props.lockedCategory ?? store.form.category] ?? 'General',
)
const heading = computed(() => props.title ?? `Report ${selectedLabel.value} Deviation`)
</script>

<template>
  <div class="card">
    <div class="card-head">
      <div class="title-wrap">
        <div class="title-icon">
          <Edit2 :size="20" aria-hidden="true" />
        </div>
        <h3 class="title-text">{{ heading }}</h3>
      </div>
    </div>

    <div class="form-grid">
      <label>
        <span>Deviation title *</span>
        <input v-model="store.form.title" placeholder="e.g., Cold Storage Sensor Failure" />
      </label>

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
        <span>Current status</span>
        <select v-model="store.form.status">
          <option value="OPEN">Open</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="RESOLVED">Resolved</option>
        </select>
      </label>

      <label class="field-wide">
        <span>Severity level</span>
        <div class="severity">
          <button
            v-for="s in levels"
            :key="s"
            type="button"
            :class="['severity-btn', s.toLowerCase(), { active: store.form.severity === s }]"
            @click="store.form.severity = s"
          >
            {{ s }}
          </button>
        </div>
      </label>

      <label class="description field-wide">
        <span>Detailed description</span>
        <textarea
          v-model="store.form.description"
          maxlength="500"
          placeholder="Describe what happened, location, and immediate actions taken..."
        />
      </label>
    </div>

    <div class="footer">
      <small>{{ store.form.description.length }} / 500</small>
      <button class="submit" type="button" @click="store.createDeviation">
        Submit deviation >
      </button>
    </div>
  </div>
</template>

<style scoped>
.card {
  background: var(--bg-secondary);
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

.field-wide {
  grid-column: auto;
}

label {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

label > span {
  text-transform: uppercase;
  letter-spacing: 0.05em;
  font-size: 10px;
  font-weight: 700;
  color: var(--text-secondary);
}

input,
select,
textarea {
  border: 1px solid var(--border);
  background: var(--bg);
  border-radius: 8px;
  color: var(--text);
}

input {
  min-height: 42px;
  padding: 10px 12px;
}

select {
  min-height: 42px;
  padding: 8px 10px;
}

.severity {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 8px;
}

.severity-btn {
  min-height: 34px;
  border: 1px solid var(--border);
  border-radius: 7px;
  background: var(--bg-secondary);
  color: var(--text-secondary);
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.02em;
}

.severity-btn.low.active {
  background: #e9fff2;
  border-color: #23975c;
  color: #1a1c1e;
}

.severity-btn.medium.active {
  background: #fff8e6;
  border-color: #b3891a;
  color: #1a1c1e;
}

.severity-btn.high.active {
  background: #fff0e9;
  border-color: #c96f3a;
  color: #1a1c1e;
}

.severity-btn.critical.active {
  background: var(--cta-red);
  border-color: var(--cta-red);
  color: var(--bg);
}

.description {
  grid-row: auto;
}

textarea {
  width: 100%;
  min-height: 110px;
  resize: vertical;
  padding: 12px;
}

.footer {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 16px;
}

small {
  color: var(--text-secondary);
  font-size: 11px;
}

.submit {
  background: var(--neutral);
  border: 1px solid var(--neutral);
  border-radius: 8px;
  min-height: 42px;
  min-width: 220px;
  text-transform: uppercase;
  font-size: 11px;
  letter-spacing: 0.05em;
  font-weight: 700;
}

.submit:hover {
  background: var(--neutral-hover);
}

@media (max-width: 1020px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .description {
    grid-column: auto;
    grid-row: auto;
  }
}
</style>
