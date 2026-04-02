<script setup lang="ts">
import { computed } from 'vue'
import { AlarmClock, CircleCheckBig, FolderKanban, Sparkles } from '@lucide/vue'

type TaskStatus = 'Today' | 'Scheduled' | 'Blocked'

interface TaskItem {
  id: number
  title: string
  owner: string
  due: string
  status: TaskStatus
}

const tasks: TaskItem[] = [
  {
    id: 1,
    title: 'Review cold storage temperatures',
    owner: 'Sigurd',
    due: '08:30',
    status: 'Today',
  },
  {
    id: 2,
    title: 'Follow up on cleaning supplier delivery',
    owner: 'Amalie',
    due: '10:15',
    status: 'Scheduled',
  },
  {
    id: 3,
    title: 'Update opening shift handover notes',
    owner: 'Linnea',
    due: '13:00',
    status: 'Today',
  },
  {
    id: 4,
    title: 'Replace damaged dry goods labels',
    owner: 'Marcus',
    due: 'Awaiting stock',
    status: 'Blocked',
  },
]

const taskSummary = computed(() => {
  const total = tasks.length
  const today = tasks.filter((task) => task.status === 'Today').length
  const scheduled = tasks.filter((task) => task.status === 'Scheduled').length
  const blocked = tasks.filter((task) => task.status === 'Blocked').length

  return { total, today, scheduled, blocked }
})

function statusClass(status: TaskStatus) {
  return `status-pill status-${status.toLowerCase()}`
}
</script>

<template>
  <div class="tasks-view">
    <h1>Tasks</h1>

    <section class="hero-card">
      <div>
        <p class="eyebrow">Operations Planner</p>
        <h2>Track priorities without overbuilding the page.</h2>
        <p class="hero-copy">
          This starter view gives you a simple structure for assigning work, spotting blockers, and
          replacing mock data with API results later.
        </p>
      </div>

      <div class="hero-note">
        <Sparkles :size="18" aria-hidden="true" />
        <span>Replace the arrays in the script block first when connecting backend data.</span>
      </div>
    </section>

    <section class="stat-grid">
      <article class="stat-card">
        <div class="stat-icon">
          <FolderKanban :size="18" aria-hidden="true" />
        </div>
        <strong>{{ taskSummary.total }}</strong>
        <span>Total tasks</span>
      </article>

      <article class="stat-card">
        <div class="stat-icon">
          <AlarmClock :size="18" aria-hidden="true" />
        </div>
        <strong>{{ taskSummary.today }}</strong>
        <span>Due today</span>
      </article>

      <article class="stat-card">
        <div class="stat-icon">
          <CircleCheckBig :size="18" aria-hidden="true" />
        </div>
        <strong>{{ taskSummary.scheduled }}</strong>
        <span>Scheduled next</span>
      </article>
    </section>

    <section class="content-grid">
      <article class="panel">
        <div class="panel-heading">
          <div>
            <p class="section-label">Task Queue</p>
            <h3>Today and upcoming</h3>
          </div>
          <span class="muted">{{ taskSummary.blocked }} blocked</span>
        </div>

        <ul class="task-list">
          <li v-for="task in tasks" :key="task.id" class="task-row">
            <div>
              <strong>{{ task.title }}</strong>
              <p>{{ task.owner }} • {{ task.due }}</p>
            </div>
            <span :class="statusClass(task.status)">
              {{ task.status }}
            </span>
          </li>
        </ul>
      </article>

      <article class="panel side-panel">
        <p class="section-label">Editor Notes</p>
        <h3>Suggested next upgrades</h3>
        <ul class="notes-list">
          <li>Connect this list to a Pinia store or task service.</li>
          <li>Add assignee, due date, and module filters above the queue.</li>
          <li>Swap the mock counts for computed API-backed metrics.</li>
        </ul>
      </article>
    </section>
  </div>
</template>

<style scoped>
.tasks-view {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.hero-card {
  display: flex;
  justify-content: space-between;
  gap: 24px;
  padding: 28px;
  border-radius: 28px;
  background: linear-gradient(135deg, #f8f2ea 0%, #ede7df 100%);
  border: 1px solid rgba(15, 23, 42, 0.08);
}

.eyebrow,
.section-label {
  margin: 0 0 8px;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--text-secondary);
}

.hero-card h2,
.panel h3 {
  margin: 0;
}

.hero-copy {
  max-width: 62ch;
  margin: 12px 0 0;
  color: var(--text-secondary);
}

.hero-note {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 220px;
  padding: 14px 16px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.75);
  color: #1f2937;
}

.stat-grid,
.content-grid {
  display: grid;
  gap: 16px;
}

.stat-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.content-grid {
  grid-template-columns: 2fr 1fr;
}

.stat-card,
.panel {
  padding: 22px;
  border-radius: 22px;
  background: #f7f7f6;
  border: 1px solid rgba(15, 23, 42, 0.08);
}

.stat-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-card strong {
  font-size: 30px;
}

.stat-card span {
  color: var(--text-secondary);
}

.stat-icon {
  width: 38px;
  height: 38px;
  display: grid;
  place-items: center;
  border-radius: 12px;
  background: #e7e3dc;
}

.panel-heading {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
  margin-bottom: 18px;
}

.muted,
.task-row p,
.notes-list {
  color: var(--text-secondary);
}

.task-list,
.notes-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.task-row {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  padding: 14px 16px;
  border-radius: 16px;
  background: #ffffff;
}

.task-row strong {
  display: block;
  margin-bottom: 4px;
}

.task-row p {
  margin: 0;
}

.status-pill {
  padding: 8px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.status-today {
  background: #dbeafe;
  color: #1d4ed8;
}

.status-scheduled {
  background: #e7e5e4;
  color: #44403c;
}

.status-blocked {
  background: #fee2e2;
  color: #b91c1c;
}

.notes-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

@media (max-width: 960px) {
  .hero-card,
  .content-grid,
  .stat-grid {
    grid-template-columns: 1fr;
    display: grid;
  }
}
</style>
