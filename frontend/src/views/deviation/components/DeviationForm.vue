<script setup lang="ts">
import { computed } from 'vue'
import { useDeviationStore } from '@/stores/deviation'

const store = useDeviationStore()

const levels = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'] as const

const categoryLabels: Record<string, string> = {
  TEMPERATURE: 'Temperature',
  HYGIENE: 'Hygiene',
  ALCOHOL: 'Alcohol',
  DOCUMENTATION: 'Documentation',
  OTHER: 'Other',
}

const selectedLabel = computed(() => categoryLabels[store.form.category] ?? 'General')
</script>

<template>
  <div class="card">
    <div class="card-head">
      <div class="title-wrap">
        <h3>Report {{ selectedLabel }} Deviation</h3>
      </div>

      <div class="module-toggle" role="group" aria-label="Compliance module">
        <button
            type="button"
            :class="['module-option', { active: store.form.module === 'IK_FOOD' }]"
            @click="store.form.module = 'IK_FOOD'"
        >
          IK-FOOD
        </button>
        <button
            type="button"
            :class="['module-option', { active: store.form.module === 'IK_ALCOHOL' }]"
            @click="store.form.module = 'IK_ALCOHOL'"
        >
          IK-ALCOHOL
        </button>
      </div>
    </div>

    <div class="form-grid">
      <label>
        <span>Deviation title *</span>
        <input v-model="store.form.title" placeholder="e.g., Cold Storage Sensor Failure" />
      </label>

      <label>
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

      <label>
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

      <label class="description">
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
      <button class="submit" type="button" @click="store.createDeviation">Submit deviation ></button>
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
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 18px;
}

.title-wrap {
  display: flex;
  align-items: center;
  gap: 10px;
}

h3 {
  margin: 0;
  color: var(--text);
  font-size: 30px;
}

.module-toggle {
  display: flex;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--surface-muted);
  overflow: hidden;
}

.module-option {
  border: 0;
  min-height: 36px;
  min-width: 118px;
  font-size: 11px;
  font-weight: 700;
  color: var(--text-secondary);
  border-radius: 0;
  text-transform: uppercase;
  background: transparent;
}

.module-option.active {
  background: var(--neutral);
  color: var(--bg);
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(240px, 1fr));
  gap: 14px;
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
  background: var(--surface-muted);
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
  min-height: 40px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--surface);
  color: var(--text-secondary);
  font-size: 11px;
  font-weight: 700;
}

.severity-btn.low.active {
  background: #e9fff2;
  border-color: var(--secondary);
  color: var(--neutral);
}

.severity-btn.medium.active {
  background: #fff8e6;
  border-color: var(--tertiary);
  color: var(--neutral);
}

.severity-btn.high.active {
  background: #fff0e9;
  border-color: var(--tertiary);
  color: var(--neutral);
}

.severity-btn.critical.active {
  background: var(--cta-red);
  border-color: var(--cta-red);
  color: var(--bg);
}

.description {
  grid-column: 2;
  grid-row: 2 / span 2;
}

textarea {
  width: 100%;
  min-height: 120px;
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
  color: var(--text-muted);
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

  .module-option {
    min-width: 100px;
  }
}
</style>