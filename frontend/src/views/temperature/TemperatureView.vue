<script setup lang="ts">
import { onMounted } from 'vue'
import DeviationForm from '../deviation/components/DeviationForm.vue'
import DeviationTable from '../deviation/components/DeviationTable.vue'
import { useDeviationStore } from '@/stores/deviation'

const store = useDeviationStore()

function applyTemperatureDefaults() {
  store.form.category = 'TEMPERATURE'
  store.form.module = 'IK_FOOD'
  store.form.severity = 'CRITICAL'
  store.filters.status = 'ALL'
  store.filters.severity = 'ALL'
}

onMounted(() => {
  applyTemperatureDefaults()
  store.fetchDeviations()
})
</script>

<template>
  <div class="page">
    <header class="page-header">
      <h1>Temperature Deviation</h1>
      <p>Food safety monitoring and response</p>
    </header>

    <section class="hero-card">
      <div>
        <p class="section-label">IK-Food</p>
        <h2>Track cold-chain issues and report temperature deviations fast.</h2>
        <p>
          This view keeps the form locked to food temperature incidents and shows only matching
          records in the log.
        </p>
      </div>
    </section>

    <p v-if="store.error" class="error-banner">{{ store.error }}</p>

    <DeviationForm
      title="Report Temperature Deviation"
      locked-category="TEMPERATURE"
      locked-module="IK_FOOD"
    />

    <DeviationTable title="Temperature Deviation Log" category="TEMPERATURE" />
  </div>
</template>

<style scoped>
.page {
  padding: 30px;
  background: var(--bg);
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.page-header {
  margin-bottom: 0;
}

.page-header h1 {
  margin: 0;
  font-size: 36px;
  line-height: 1.1;
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

.hero-card {
  padding: 24px 26px;
  border-radius: 20px;
  border: 1px solid rgba(15, 23, 42, 0.08);
  background: linear-gradient(135deg, #eef7f1 0%, #dcebdd 100%);
}

.hero-card h2 {
  margin: 0 0 10px;
  font-size: 28px;
  line-height: 1.15;
  color: var(--text);
}

.hero-card p:last-child {
  margin: 0;
  max-width: 60ch;
  color: var(--text-secondary);
}

.section-label {
  margin: 0 0 8px;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
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

@media (max-width: 900px) {
  .page {
    padding: 22px;
  }

  .hero-card {
    padding: 20px;
  }

  .hero-card h2 {
    font-size: 24px;
  }
}
</style>
