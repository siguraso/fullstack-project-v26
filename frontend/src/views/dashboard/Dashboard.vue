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
import TeamActivity from './components/TeamActivity.vue'

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

  <div class="info-cards">
    <QuickActionsCard />

    <ChecklistCompletionCard />

    <TemperatureOverviewCard />

    <ActiveDeviationCard />
  </div>

  <div class="second-row">
    <div class="second-row-component">
      <h2>Pending Checklists</h2>

      <PendingChecklists />
    </div>

    <div class="second-row-component">
      <h2>Team Activity</h2>

      <TeamActivity />
    </div>
  </div>
</template>

<style scoped>
.info-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.info-card {
  width: 85%;
  justify-self: center;
  margin-bottom: 20px;
}

.second-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 1rem;
}

.second-row-component {
  width: 100%;
  align-items: center;
}
</style>
