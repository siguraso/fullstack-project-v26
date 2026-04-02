<script setup lang="ts">
import ViewHeader from '@/components/ui/ViewHeader.vue'
import CreateTemperatureLog from './components/CreateTemperatureLog.vue'
import TemperatureZoneOverview from './components/TemperatureZoneOverview.vue'
import TemperatureLogHistory from './components/TemperatureLogHistory.vue'
</script>

<template>
  <header>
    <ViewHeader title="Temperature Logs" />
  </header>

  <div class="top-row">
    <CreateTemperatureLog class="create-log" />
    <TemperatureZoneOverview class="temperature-zone-overview" />
  </div>

  <TemperatureLogHistory />
</template>

<style scoped>
.top-row {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 1rem;
  margin-bottom: 2rem;
}

.temperature-zone-overview {
  align-self: start;
}

.create-log {
  align-self: start;
}
</style>
