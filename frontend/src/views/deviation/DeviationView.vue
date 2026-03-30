
<script setup lang="ts">
import { onMounted } from 'vue'
import QuickActionBar from '@/views/deviation/components/QuickActionBar.vue'
import DeviationForm from '@/views/deviation/components/DeviationForm.vue'
import DeviationTable from '@/views/deviation/components/DeviationTable.vue'
import { useDeviationStore, type Deviation } from '@/stores/deviation'

const store = useDeviationStore()

function onCategorySelect(category: Deviation['category']) {
  store.form.category = category

  if (category === 'TEMPERATURE') {
    store.form.severity = 'CRITICAL'
    return
  }

  if (category === 'HYGIENE') {
    store.form.severity = 'HIGH'
  }
}

onMounted(() => {
  store.fetchDeviations()
})
</script>

<template>
  <div class="page">
    <header class="page-header">
      <h1>Deviations</h1>
      <p>Reporting and management</p>
    </header>

    <p v-if="store.error" class="error-banner">{{ store.error }}</p>

    <QuickActionBar :active="store.form.category" @select="onCategorySelect" />

    <DeviationForm />

    <DeviationTable />
  </div>
</template>

<style scoped>
.page {
  padding: 30px;
  background: var(--bg);
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
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

.error-banner {
  margin: 0 0 14px;
  border: 1px solid var(--cta-red);
  background: #fff0f0;
  color: var(--cta-red);
  border-radius: 8px;
  padding: 10px 12px;
  font-size: 13px;
}
</style>
