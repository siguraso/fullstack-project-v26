<script lang="ts" setup>
type DashboardState = 'ready' | 'loading' | 'error'

import ChecklistCompletionCard from './components/info-cards/ChecklistCompletionCard.vue'
import ActiveDeviationCard from './components/info-cards/ActiveDeviationCard.vue'
import PendingChecklists from './components/PendingChecklists.vue'
import QuickActionsCard from './components/info-cards/QuickActionsCard.vue'
import TeamActivity from './components/TeamActivity.vue'
import CriticalAlerts from './components/CriticalAlerts.vue'
</script>

<template>
  <header>
    <ViewHeader title="Dashboard" />
  </header>

  <CriticalAlerts />

  <div class="info-cards">
    <ChecklistCompletionCard />

    <ActiveDeviationCard />

    <QuickActionsCard />
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
  grid-template-columns: 1fr 1fr 2fr;
  gap: 1rem;
  margin-bottom: 2rem;
}

.info-cards > :nth-child(3) {
  grid-column: auto;
}

.second-row {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 1rem;
}

.second-row-component {
  width: 100%;
  align-items: center;
}
</style>
