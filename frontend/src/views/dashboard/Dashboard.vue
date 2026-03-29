<script lang="ts" setup>
import { ref } from 'vue'
import ViewHeader from '@/components/ui/ViewHeader.vue'

type DashboardState = 'ready' | 'loading' | 'error'

const dashboardState = ref<DashboardState>('ready')
const hasAssignedTasks = ref(true)

import ChecklistCompletionCard from './components/info-cards/ChecklistCompletionCard.vue'
import TemperatureOverviewCard from './components/info-cards/TemperatureOverviewCard.vue'
import ActiveDeviationCard from './components/info-cards/ActiveDeviationCard.vue'
import PendingChecklists from './components/PendingChecklists.vue'
import QuickActionsCard from './components/info-cards/QuickActionsCard.vue'

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

  <!-- <h2>Quick Overview</h2> -->

  <div class="info-cards">
    <QuickActionsCard />

    <ChecklistCompletionCard />

    <TemperatureOverviewCard />

    <ActiveDeviationCard />
  </div>

  <h2>Pending Checklists</h2>

  <PendingChecklists />
</template>

<style scoped>
.info-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  overflow-x: scroll;
}

.info-card {
  width: 80%;
  justify-self: center;
  margin-bottom: 20px;
}
</style>
