<script lang="ts" setup>
import ChecklistCompletionCard from './components/info-cards/ChecklistCompletionCard.vue'
import ActiveDeviationCard from './components/info-cards/ActiveDeviationCard.vue'
import PendingChecklists from './components/PendingChecklists.vue'
import QuickActionsCard from './components/info-cards/QuickActionsCard.vue'
import TeamActivity from './components/TeamActivity.vue'
import CriticalAlerts from './components/CriticalAlerts.vue'
import { onMounted, ref } from 'vue'
import { getDashboardOverview } from '@/services/dashboard'

const dashboard = ref<any>(null)
const loading = ref(true)

onMounted(async () => {
  try {
    dashboard.value = await getDashboardOverview()
  } finally {
    loading.value = false
  }
})

async function reloadDashboard() {
  setTimeout(async () => {
    dashboard.value = await getDashboardOverview()
  }, 250)
}
</script>

<template>
  <header>
    <h1>Dashboard</h1>
  </header>

  <TransitionGroup name="layout" tag="div" class="dashboard-content">
    <Transition name="fade-slide">
      <CriticalAlerts
        v-if="(dashboard?.criticalAlerts || []).length > 0"
        :key="'alerts'"
        :alerts="dashboard?.criticalAlerts || []"
        @resolved="reloadDashboard"
      />
    </Transition>

    <div class="info-cards" :key="'info'">
      <ChecklistCompletionCard
        :completed="dashboard?.checklistsToday.completedItems || 0"
        :total="dashboard?.checklistsToday.totalItems || 0"
      />

      <ActiveDeviationCard
        :active="dashboard?.activeDeviations.length || 0"
        :weekly="dashboard?.activeDeviations.length || 0"
        :closed="0"
      />
      <!--TODO: FIX-->

      <QuickActionsCard />
    </div>

    <div class="second-row" :key="'second'">
      <div class="second-row-component">
        <h2>Pending Checklists</h2>

        <PendingChecklists :checklists="dashboard?.pendingChecklists" @updated="reloadDashboard" />
      </div>

      <div class="second-row-component">
        <h2>Team Activity</h2>

        <TeamActivity :activities="dashboard?.teamActivity" />
      </div>
    </div>
  </TransitionGroup>
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

/* ANIMATION */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.35s ease;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.dashboard-content > * {
  position: relative;
}
</style>
