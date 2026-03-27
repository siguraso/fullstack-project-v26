<script lang="ts" setup>
import { ref } from 'vue';
  import Card from '@/components/ui/Card.vue';

  const temperatureInput = ref('');

  // TODO: Fetch locations from backend and populate the select options
  const locations = ['Freezer', 'Fridge'];
</script>

<template>
  <Card title="Quick Actions">
    <template #card-header>
      Quick Actions
    </template>
    <template #card-content>
      <h3>Create Incident Report</h3>
      <div class="action">
        <input type="text" placeholder="Incident Title" class="incident-input" />
        <button>Create Report</button>
      </div>

      
      <h3>Temperature Logging</h3>
      <div class="action">
        <input type="text" placeholder="" class="temperature-input" v-model="temperatureInput"/>
        <span>°C</span>
        <select placeholder="Select Location">
          <option value="" disabled selected>Select Location</option>
          <option v-for="location in locations" :key="location" :value="location">{{ location }}</option>
        </select>
        <button>Create Log</button>
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
    width: 30%
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
</style>