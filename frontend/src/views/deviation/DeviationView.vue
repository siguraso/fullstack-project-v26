
<script setup lang="ts">
import { onMounted } from 'vue'
import DeviationForm from './components/DeviationForm.vue'
import DeviationGuide from './components/DeviationGuide.vue'
import DeviationTable from './components/DeviationTable.vue'
import { useDeviationStore } from '@/stores/deviation'

const store = useDeviationStore()

onMounted(() => {
  store.fetchDeviations()
})
</script>

<template>
  <div class="page">
    <header class="page-header">
      <h1>Deviations</h1>
    </header>

    <p v-if="store.error" class="error-banner">{{ store.error }}</p>

    <div class="top-row">
      <DeviationForm class="form-panel" title="Report Deviation" />
      <DeviationGuide class="guide-panel" />
    </div>

    <DeviationTable />
  </div>
</template>

<style scoped>
.page {
  background: var(--bg);
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 0;
}

.page-header {
  margin-bottom: 0;
}

.page-header h1 {
  color: var(--text);
}

.page-header p {
  margin: 8px 0 0;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  font-weight: 600;
  font-size: 11px;
  color: var(--text-secondary);
}

.error-banner {
  margin: 0 0 14px;
  border: 1px solid var(--cta-red);
  background: #fff0f0;
  color: var(--cta-red);
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 13px;
}

.top-row {
  display: grid;
  grid-template-columns: minmax(0, 600px) minmax(0, 1fr);
  gap: 16px;
  align-items: stretch;
}

.form-panel {
  width: 100%;
}

.guide-panel {
  height: 100%;
}


@media (max-width: 1250px) {
  .top-row {
    grid-template-columns: 1fr;
  }
}
</style>
