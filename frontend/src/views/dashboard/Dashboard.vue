<script lang="ts" setup>
import { ref } from 'vue'
import Card from '@/components/ui/Card.vue'
import PieChart from './components/PieChart.vue'
import QuickActions from './components/QuickActions.vue'
import MyTasks from './components/MyTasks.vue'
import ViewHeader from '@/components/ui/ViewHeader.vue'

type DashboardState = 'ready' | 'loading' | 'error'

const dashboardState = ref<DashboardState>('ready')
const hasAssignedTasks = ref(true)

import { Thermometer, TriangleAlert, ClipboardCheck, BadgeCheck } from '@lucide/vue'
import InfoCard from './components/info-cards/InfoCard.vue'
import ChecklistCompletionCard from './components/info-cards/ChecklistCompletionCard.vue'
import TemperatureOverviewCard from './components/info-cards/TemperatureOverviewCard.vue'
import ActiveDeviationCard from './components/info-cards/ActiveDeviationCard.vue'
import ComplianceHealthCard from './components/info-cards/ComplianceHealthCard.vue'

// placeholders
const primaryChart = {
  greenPiece: 3432,
  orangePiece: 7540,
  greenLabel: 'Checks Completed',
  orangeLabel: 'Checks Not Completed',
}

const secondaryChart = {
  greenPiece: 1850,
  orangePiece: 220,
  greenLabel: 'on schedule',
  orangeLabel: 'overdue',
}

const retryLoad = () => {
  dashboardState.value = 'ready'
}
</script>

<template>
  <header>
    <ViewHeader
      title="Dashboard"
      :options="['Global', 'Food Compliance', 'Alcohol Compliance']"
      :routes="['/dashboard', '/dashboard/food-compliance', '/dashboard/alcohol-compliance']"
    />
  </header>

  <h2>Quick Overview</h2>

  <div class="info-cards">
    <ChecklistCompletionCard />

    <TemperatureOverviewCard />

    <ActiveDeviationCard />

    <ComplianceHealthCard />
  </div>

  <h2>Pending Checklists</h2>
</template>

<style scoped>
.info-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
}

.info-card {
  width: 80%;
  justify-self: center;
}
</style>
