<script lang="ts" setup>
import { computed } from 'vue'
import { CheckCircle2 } from '@lucide/vue'
import InfoCard from './InfoCard.vue'

// TODO add data fetching
const compliantItems = 87
const nonCompliantItems = 13

const total = computed(() => compliantItems + nonCompliantItems)

const complianceRate = computed(() => {
  if (total.value === 0) return 0
  return (compliantItems / total.value) * 100
})

const complianceStatus = computed(() => {
  const rate = complianceRate.value
  if (rate >= 85) return 'Excellent'
  if (rate >= 70) return 'Good'
  if (rate >= 50) return 'Fair'
  return 'Poor'
})

const statusColor = computed(() => {
  const rate = complianceRate.value
  if (rate >= 85) return 'var(--secondary)'
  if (rate >= 70) return 'var(--quinary)'
  if (rate >= 50) return 'var(--tertiary)'
  return 'var(--cta-red)'
})

const complianceDeg = computed(() => {
  return (compliantItems / total.value) * 360
})

const pieStyle = computed(() => {
  return {
    background: `conic-gradient(
      var(--secondary) 0deg ${complianceDeg.value}deg,
      var(--tertiary) ${complianceDeg.value}deg 360deg
    )`,
  }
})
</script>

<template>
  <InfoCard
    class="info-card"
    title="Compliance Health"
    :icon="CheckCircle2"
    :cardColor="statusColor"
  >
    <div class="compliance-wrapper">
      <div class="pie-container">
        <div class="pie-chart">
          <div
            class="chart-outline"
            :style="pieStyle"
            role="img"
            aria-label="Compliance pie chart"
          />
          <div class="stack-circle" />
          <p>{{ Math.round(complianceRate) }}%</p>
        </div>
      </div>

      <div class="status-section">
        <h3 class="status-title">{{ complianceStatus }}</h3>
        <div class="metrics">
          <div class="metric">
            <span class="box box-compliant" />
            <span class="metric-label">Compliant: {{ compliantItems }}</span>
          </div>
          <div class="metric">
            <span class="box box-non-compliant" />
            <span class="metric-label">Non-Compliant: {{ nonCompliantItems }}</span>
          </div>
        </div>
      </div>
    </div>
  </InfoCard>
</template>

<style scoped>
.compliance-wrapper {
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.pie-container {
  flex-shrink: 0;
}

.pie-chart {
  position: relative;
  width: 120px;
  height: 120px;
}

.chart-outline {
  position: absolute;
  inset: 0;
  border-radius: 50%;
}

.stack-circle {
  position: absolute;
  width: 100px;
  aspect-ratio: 1 / 1;
  border-radius: 50%;
  background: var(--bg);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

.pie-chart p {
  position: absolute;
  inset: 0;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
  line-height: 1;
  z-index: 2;
}

.status-section {
  flex: 1;
}

.status-title {
  margin: 0 0 8px 0;
  font-size: 1.1rem;
  font-weight: bold;
}

.metrics {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.metric {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
}

.box {
  width: 0.75rem;
  height: 0.75rem;
  border-radius: 2px;
  flex-shrink: 0;
}

.box-compliant {
  background: var(--secondary);
}

.box-non-compliant {
  background: var(--tertiary);
}

.metric-label {
  color: var(--text-secondary);
}

@media (max-width: 480px) {
  .compliance-wrapper {
    flex-direction: column;
    gap: 1rem;
  }

  .pie-chart {
    width: 100px;
    height: 100px;
  }

  .stack-circle {
    width: 84px;
  }

  .pie-chart p {
    font-size: 16px;
  }
}
</style>
