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
  <div class="dashboard-grid">
    <Card class="tile tile-main-chart">
      <template #card-header> Daily Checklist Completion</template>

      <template #card-content>
        <div v-if="dashboardState === 'loading'" class="placeholder-stack">
          <div class="placeholder placeholder-circle" />
          <div class="placeholder placeholder-line" />
          <div class="placeholder placeholder-line short" />
        </div>

        <div v-else-if="dashboardState === 'error'" class="tile-state">
          <p>Could not load chart data.</p>
          <button class="state-btn" @click="retryLoad">Retry</button>
        </div>

        <PieChart
          v-else
          :greenPiece="primaryChart.greenPiece"
          :orangePiece="primaryChart.orangePiece"
          :greenLabel="primaryChart.greenLabel"
          :orangeLabel="primaryChart.orangeLabel"
        />
      </template>
    </Card>

    <Card class="tile tile-secondary-chart">
      <template #card-header> Schedule Health </template>

      <template #card-content>
        <div v-if="dashboardState === 'loading'" class="placeholder-stack">
          <div class="placeholder placeholder-circle" />
          <div class="placeholder placeholder-line" />
        </div>

        <div v-else-if="dashboardState === 'error'" class="tile-state">
          <p>Could not load schedule data.</p>
          <button class="state-btn" @click="retryLoad">Retry</button>
        </div>

        <PieChart
          v-else
          :greenPiece="secondaryChart.greenPiece"
          :orangePiece="secondaryChart.orangePiece"
          :greenLabel="secondaryChart.greenLabel"
          :orangeLabel="secondaryChart.orangeLabel"
        />
      </template>
    </Card>

    <QuickActions class="tile tile-actions" />

    <MyTasks v-if="dashboardState === 'ready' && hasAssignedTasks" class="tile tile-tasks" />

    <Card v-else-if="dashboardState === 'loading'" class="tile tile-tasks">
      <template #card-header> My Tasks </template>

      <template #card-content>
        <div class="placeholder-stack">
          <div class="placeholder placeholder-line" />
          <div class="placeholder placeholder-line" />
          <div class="placeholder placeholder-line short" />
        </div>
      </template>
    </Card>

    <Card v-else-if="dashboardState === 'error'" class="tile tile-tasks">
      <template #card-header> My Tasks </template>

      <template #card-content>
        <div class="tile-state">
          <p>Could not load tasks.</p>
          <button class="state-btn" @click="retryLoad">Retry</button>
        </div>
      </template>
    </Card>

    <Card v-else class="tile tile-tasks">
      <template #card-header> My Tasks </template>

      <template #card-content>
        <div class="tile-state">
          <p>No assigned tasks right now.</p>
          <p class="state-note">You are all caught up.</p>
        </div>
      </template>
    </Card>
  </div>
</template>

<style scoped>
.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(12, minmax(0, 1fr));
  grid-template-areas:
    'main main main main main main secondary secondary secondary secondary secondary secondary'
    'actions actions actions actions tasks tasks tasks tasks tasks tasks tasks tasks';
  align-items: start;
  gap: 20px;
}

.tile {
  min-width: 0;
  align-self: start;
}

.tile-main-chart {
  grid-area: main;
}

.tile-secondary-chart {
  grid-area: secondary;
}

.tile-actions {
  grid-area: actions;
}

.tile-tasks {
  grid-area: tasks;
}

.tile-state {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.state-note {
  margin: 0;
  opacity: 0.75;
  font-size: 0.95rem;
}

.state-btn {
  width: fit-content;
  border: 1px solid var(--stroke);
  background: var(--bg);
  border-radius: 6px;
  padding: 6px 10px;
  cursor: pointer;
}

.placeholder-stack {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.placeholder {
  background: color-mix(in srgb, var(--stroke) 45%, transparent);
  border-radius: 8px;
}

.placeholder-circle {
  width: 160px;
  height: 160px;
  border-radius: 999px;
}

.placeholder-line {
  width: 100%;
  max-width: 300px;
  height: 14px;
}

.placeholder-line.short {
  max-width: 180px;
}

@media (max-width: 1100px) {
  .dashboard-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    grid-template-areas:
      'main main'
      'secondary actions'
      'tasks tasks';
  }
}

@media (max-width: 820px) {
  .dashboard-grid {
    grid-template-columns: minmax(0, 1fr);
    grid-template-areas:
      'main'
      'secondary'
      'actions'
      'tasks';
  }
}
</style>
