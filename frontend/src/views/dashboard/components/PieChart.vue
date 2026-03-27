<script lang="ts" setup>
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    greenPiece?: number
    orangePiece?: number
    greenLabel?: string
    orangeLabel?: string
  }>(),
  {
    greenPiece: 0,
    orangePiece: 0,
    greenLabel: 'Green',
    orangeLabel: 'Orange',
  },
)

const green = computed(() => Math.max(0, props.greenPiece))
const orange = computed(() => Math.max(0, props.orangePiece))
const total = computed(() => green.value + orange.value)

const greenDeg = computed(() => {
  if (total.value === 0) return 0
  return (green.value / total.value) * 360
})

const greenPercent = computed(() => {
  if (total.value === 0) return 0
  return (green.value / total.value) * 100
})

const pieStyle = computed(() => {
  if (total.value === 0) {
    return {
      background: 'conic-gradient(var(--secondary) 0deg 360deg)',
    }
  }

  return {
    background: `conic-gradient(
      var(--secondary) 0deg ${greenDeg.value}deg,
      var(--tertiary) ${greenDeg.value}deg 360deg
    )`,
  }
})
</script>

<template>
  <div class="pie-chart-wrapper">
    <div class="pie-chart">
      <div class="chart-outline" :style="pieStyle" role="img" aria-label="Pie chart" />
      <div class="stack-circle" />
      <p>{{ Math.round(greenPercent) }} %</p>
    </div>
    <div class="legend">
      <div class="legend-row">
        <span class="box box-green" />
        <span class="legend-label">{{ greenLabel }}: {{ green }}</span>
      </div>
      <br />
      <div class="legend-row">
        <span class="box box-orange" />
        <span class="legend-label">{{ orangeLabel }}: {{ orange }}</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.pie-chart-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.pie-chart {
  position: relative;
  width: 160px;
  height: 160px;
}

.chart-outline {
  position: absolute;
  inset: 0;
  border-radius: 50%;
}

.stack-circle {
  position: absolute;
  width: 135px;
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
  font-size: 22px;
  font-weight: bold;
  line-height: 1;
  z-index: 2;
}

.legend {
  display: flex;
  flex-direction: row;
  gap: 0.35rem;
  font-size: 0.95rem;
}

.legend-row {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.legend-row span {
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
}

.box {
  width: 0.8rem;
  height: 0.8rem;
  align-items: center;
}

.box-green {
  background: var(--secondary);
}

.box-orange {
  background: var(--tertiary);
}
</style>
