<script setup lang="ts">
import ViewHeader from '@/components/ui/ViewHeader.vue'
import CreateTemperatureLog from './components/CreateTemperatureLog.vue'
import StorageUnitOverview from './components/StorageUnitOverview.vue'
</script>

<template>
  <header>
    <ViewHeader title="Temperature Logs" />
  </header>

  <div class="top-row">
    <CreateTemperatureLog />
    <StorageUnitOverview />
  </div>
</template>

<style scoped>
.top-row {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 1rem;
  margin-bottom: 2rem;
}
</style>
