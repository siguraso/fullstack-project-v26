<script setup lang="ts">
import { onMounted, ref } from 'vue'
import DeviationForm from './components/DeviationForm.vue'
import DeviationTable from './components/DeviationTable.vue'
import DeviationDetailsDialog from './components/DeviationDetailsDialog.vue'
import { useDeviationStore } from '@/stores/deviation'
import type { Deviation } from '@/interfaces/Deviation.interface'

const store = useDeviationStore()
const isDetailsOpen = ref(false)
const selectedDeviation = ref<Deviation | null>(null)

onMounted(() => {
  store.fetchDeviations()
})

function openDetails(deviation: Deviation) {
  selectedDeviation.value = deviation
  isDetailsOpen.value = true
}

function closeDetails() {
  isDetailsOpen.value = false
  selectedDeviation.value = null
}

async function handleResolved() {
  await store.fetchDeviations()
  closeDetails()
}
</script>

<template>
  <div class="page">
    <header class="page-header">
      <h1>Deviations</h1>
    </header>

    <p v-if="store.error" class="error-banner">{{ store.error }}</p>

    <DeviationForm class="form-panel" title="Report Deviation" />

    <DeviationTable @view-requested="openDetails" />

    <DeviationDetailsDialog
      :deviation="selectedDeviation"
      :isOpen="isDetailsOpen"
      @close="closeDetails"
      @resolved="handleResolved"
    />
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

.form-panel {
  width: 100%;
}
</style>
