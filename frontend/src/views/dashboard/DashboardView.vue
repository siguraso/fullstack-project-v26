<script lang="ts" setup>
import ChecklistCompletionCard from './components/info-cards/ChecklistCompletionCard.vue'
import ActiveDeviationCard from './components/info-cards/ActiveDeviationCard.vue'
import PendingChecklists from './components/PendingChecklists.vue'
import QuickActionsCard from './components/info-cards/QuickActionsCard.vue'
import TeamActivity from './components/TeamActivity.vue'
import CriticalAlerts from './components/CriticalAlerts.vue'
import { onMounted, ref } from 'vue'
import { getDashboardOverview } from '@/services/dashboard'
import { createLogger } from '@/services/util/logger'

const dashboard = ref<any>(null)
const loading = ref(true)
const logger = createLogger('dashboard-view')

async function loadDashboard(source: 'mount' | 'reload') {
  logger.info('dashboard load started', { source })

  try {
    dashboard.value = await getDashboardOverview()
    logger.info('dashboard load succeeded', {
      source,
      criticalAlerts: dashboard.value?.criticalAlerts?.length ?? 0,
      pendingChecklists: dashboard.value?.pendingChecklists?.length ?? 0,
      teamActivity: dashboard.value?.teamActivity?.length ?? 0,
    })
  } catch (error) {
    dashboard.value = null
    logger.error('dashboard load failed', error, { source })
  } finally {
    loading.value = false
    logger.log('dashboard loading state updated', {
      source,
      loading: loading.value,
      hasDashboard: Boolean(dashboard.value),
    })
  }
}

onMounted(() => {
  logger.info('view mounted')
  void loadDashboard('mount')
})

async function reloadDashboard() {
  logger.info('dashboard reload scheduled', { delayMs: 250 })
  setTimeout(async () => {
    await loadDashboard('reload')
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

@media (max-width: 1100px) {
  .info-cards,
  .second-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .info-cards,
  .second-row {
    gap: 0.85rem;
  }
}
</style>
