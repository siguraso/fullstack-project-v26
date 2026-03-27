<script lang="ts" setup>
import { ref } from 'vue'
import Card from '@/components/ui/Card.vue'

const temperatureInput = ref('')

// TODO: Fetch locations from backend and populate the select options
const locations = ['Freezer', 'Fridge']

// TODO: fetch logs from backend, placeholders for now
const recentLogTitles = [
  { id: 1, title: '2 liter machine broke' },
  { id: 2, title: 'freezer too warm' },
  { id: 3, title: 'drunk guy broke entire chair' },
]
</script>

<template>
  <Card title="Quick Actions">
    <template #card-header> Quick Actions </template>
    <template #card-content>
      <h3>Create Incident Report</h3>
      <div class="action">
        <input type="text" placeholder="Incident Title" class="incident-input" />
        <button>Create Report</button>
      </div>

      <br />

      <h3>Temperature Logging</h3>
      <div class="action">
        <input type="text" placeholder="" class="temperature-input" v-model="temperatureInput" />
        <span>°C</span>
        <select placeholder="Select Location">
          <option value="" disabled selected>Select Location</option>
          <option v-for="location in locations" :key="location" :value="location">
            {{ location }}
          </option>
        </select>
        <button>Create Temperature Log</button>
      </div>

      <br />

      <h3>Recent Logs</h3>
      <div class="log-container">
        <ul class="recent-logs">
          <li v-for="log in recentLogTitles" :key="log.id">{{ log.title }}</li>
        </ul>
      </div>
    </template>
  </Card>
</template>

<style scoped>
h3 {
  font-size: 16px;
  margin-bottom: 0px;
}

.action .incident-input {
  width: 30%;
}

.action .temperature-input {
  width: 5%;
}

.action {
  display: flex;
  flex-direction: row;
  gap: 10px;
  margin-top: 10px;
  align-items: center;
}

.log-container {
  margin-top: 10px;
}

.recent-logs {
  list-style: none;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.recent-logs li {
  padding: 10px 12px;
  border: 1px solid var(--stroke);
  border-radius: 10px;
  background-color: var(--bg);
  cursor: pointer;
  transition:
    background-color 0.15s ease,
    border-color 0.15s ease;
}

.recent-logs li:hover {
  background-color: rgba(0, 102, 255, 0.08);
  border-color: var(--primary);
}

.recent-logs li:active {
  background-color: rgba(0, 102, 255, 0.14);
}
</style>
