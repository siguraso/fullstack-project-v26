<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import ChecklistTaskItem from './components/ChecklistTaskItem.vue'
import { getTodayChecklist, updateChecklistItem } from '@/services/checklist'
import { useRouter } from 'vue-router'
import { createLogger } from '@/services/util/logger'

interface Task {
  id: number
  title: string
  description: string
  completed: boolean
}

interface ChecklistGroup {
  id: number
  name: string
  module: string
  status: string
  items: Task[]
}

const checklists = ref<ChecklistGroup[]>([])
const logger = createLogger('checklist-view')

onMounted(async () => {
  logger.info('view mounted')

  try {
    const data = await getTodayChecklist()
    checklists.value = Array.isArray(data) ? data : []
    logger.info('today checklist load succeeded', {
      checklistCount: checklists.value.length,
      taskCount: checklists.value.flatMap((group) => group.items).length,
    })
  } catch (error) {
    checklists.value = []
    logger.error('today checklist load failed', error)
  }
})

const router = useRouter()

const allTasks = computed(() => checklists.value.flatMap((c) => c.items))

const completedCount = computed(() => allTasks.value.filter((t) => t.completed).length)

const percentage = computed(() => {
  if (allTasks.value.length === 0) return 0
  return Math.round((completedCount.value / allTasks.value.length) * 100)
})

const allCompleted = computed(
  () => allTasks.value.length > 0 && allTasks.value.every((t) => t.completed),
)

const today = new Date().toLocaleDateString('en-GB', {
  weekday: 'long',
  day: 'numeric',
  month: 'long',
})

// Module display config
type ModuleKey = 'IK_FOOD' | 'IK_ALCOHOL' | string

const moduleConfig: Record<
  string,
  { label: string; accentColor: string; bgColor: string; abbr: string }
> = {
  IK_FOOD: {
    label: 'IK-Food',
    accentColor: '#16a34a',
    bgColor: '#f0fdf4',
    abbr: 'IKF',
  },
  IK_ALCOHOL: {
    label: 'IK-Alcohol',
    accentColor: '#dc2626',
    bgColor: '#fff5f5',
    abbr: 'IKA',
  },
}

function getConfig(module: ModuleKey) {
  return (
    moduleConfig[module] ?? {
      label: module.replace(/_/g, ' '),
      accentColor: '#475569',
      bgColor: '#f8fafc',
      abbr: module.slice(0, 3),
    }
  )
}

// Group instances by module, preserving order IK_FOOD → IK_ALCOHOL → others
const moduleOrder = ['IK_FOOD', 'IK_ALCOHOL']

const groupedByModule = computed(() => {
  const map = new Map<string, ChecklistGroup[]>()
  for (const cl of checklists.value) {
    const mod = cl.module ?? 'OTHER'
    if (!map.has(mod)) map.set(mod, [])
    map.get(mod)!.push(cl)
  }
  // Sort keys by moduleOrder, then alphabetically for unknown modules
  const sortedKeys = [...map.keys()].sort((a, b) => {
    const ai = moduleOrder.indexOf(a)
    const bi = moduleOrder.indexOf(b)
    if (ai !== -1 && bi !== -1) return ai - bi
    if (ai !== -1) return -1
    if (bi !== -1) return 1
    return a.localeCompare(b)
  })
  return sortedKeys.map((mod) => ({ module: mod, instances: map.get(mod)! }))
})

function groupCompleted(instances: ChecklistGroup[]) {
  return instances.flatMap((c) => c.items).filter((t) => t.completed).length
}

function groupTotal(instances: ChecklistGroup[]) {
  return instances.flatMap((c) => c.items).length
}

function groupPercentage(instances: ChecklistGroup[]) {
  const total = groupTotal(instances)
  if (total === 0) return 0
  return Math.round((groupCompleted(instances) / total) * 100)
}

function findTask(id: number): Task | undefined {
  for (const group of checklists.value) {
    const task = group.items.find((t) => t.id === id)
    if (task) return task
  }
}

function goToBuilder() {
  logger.info('navigating to checklist builder')
  router.push('/checklist-builder')
}

async function handleToggle(id: number, completed: boolean) {
  const task = findTask(id)
  if (!task) {
    logger.warn('task toggle skipped because task was not found', { id, completed })
    return
  }

  logger.info('task toggle started', { id, completed })
  task.completed = completed
  try {
    await updateChecklistItem(id, completed)
    logger.info('task toggle succeeded', { id, completed })
  } catch (error) {
    task.completed = !completed
    logger.error('task toggle failed', error, { id, completed })
  }
}

async function completeAll() {
  const incomplete = allTasks.value.filter((t) => !t.completed)
  logger.info('complete all started', { taskCount: incomplete.length })
  incomplete.forEach((t) => (t.completed = true))
  try {
    await Promise.all(incomplete.map((t) => updateChecklistItem(t.id, true)))
    logger.info('complete all succeeded', { taskCount: incomplete.length })
  } catch (error) {
    incomplete.forEach((t) => (t.completed = false))
    logger.error('complete all failed', error, { taskCount: incomplete.length })
  }
}

async function unCompleteAll() {
  const complete = allTasks.value.filter((t) => t.completed)
  logger.info('reset all started', { taskCount: complete.length })
  complete.forEach((t) => (t.completed = false))
  try {
    await Promise.all(complete.map((t) => updateChecklistItem(t.id, false)))
    logger.info('reset all succeeded', { taskCount: complete.length })
  } catch (error) {
    complete.forEach((t) => (t.completed = true))
    logger.error('reset all failed', error, { taskCount: complete.length })
  }
}
</script>

<template>
  <div class="checklist-page">
    <div class="page-header">
      <h1>Checklists</h1>
      <button class="create-btn" @click="goToBuilder">+ New Checklist</button>
    </div>

    <!-- OVERALL HEADER -->
    <section class="summary-bar">
      <div class="summary-left">
        <div class="summary-date">
          <h2>Daily Operations</h2>
          <p class="subtitle">{{ today }}</p>
        </div>
        <div class="summary-progress">
          <div class="progress-track">
            <div class="progress-fill" :style="{ width: percentage + '%' }" />
          </div>
          <div class="progress-stats">
            <span class="stat">
              <span class="dot dot-done" />
              {{ completedCount }} completed
            </span>
            <span class="stat">
              <span class="dot dot-left" />
              {{ allTasks.length - completedCount }} remaining
            </span>
          </div>
        </div>
      </div>

      <div class="summary-right">
        <div class="pct-box">
          <span class="pct-num">{{ percentage }}%</span>
          <span class="pct-label">Overall</span>
        </div>
        <button class="complete-btn" @click="allCompleted ? unCompleteAll() : completeAll()">
          {{ allCompleted ? '↺ Reset All' : '✓ Complete All' }}
        </button>
      </div>
    </section>

    <!-- EMPTY STATE -->
    <div v-if="checklists.length === 0" class="empty">
      <p>No checklists scheduled for today.</p>
      <p class="empty-sub">Generate a checklist from the Checklist Builder to get started.</p>
    </div>

    <!-- MODULE SECTIONS -->
    <div
      v-for="moduleGroup in groupedByModule"
      :key="moduleGroup.module"
      class="module-section"
      :style="{ '--accent': getConfig(moduleGroup.module).accentColor }"
    >
      <!-- MODULE HEADER -->
      <div class="module-header" :style="{ background: getConfig(moduleGroup.module).bgColor }">
        <div class="module-identity">
          <div
            class="module-abbr"
            :style="{
              background: getConfig(moduleGroup.module).accentColor,
            }"
          >
            {{ getConfig(moduleGroup.module).abbr }}
          </div>
          <div>
            <h2 class="module-name" :style="{ color: getConfig(moduleGroup.module).accentColor }">
              {{ getConfig(moduleGroup.module).label }}
            </h2>
            <p class="module-meta">
              {{ groupCompleted(moduleGroup.instances) }} /
              {{ groupTotal(moduleGroup.instances) }} tasks completed
            </p>
          </div>
        </div>

        <div class="module-pct-wrap">
          <svg class="ring" viewBox="0 0 36 36">
            <circle class="ring-bg" cx="18" cy="18" r="15.9" />
            <circle
              class="ring-fill"
              cx="18"
              cy="18"
              r="15.9"
              :style="{
                strokeDasharray: `${groupPercentage(moduleGroup.instances)} 100`,
                stroke: getConfig(moduleGroup.module).accentColor,
              }"
            />
          </svg>
          <span class="module-pct">{{ groupPercentage(moduleGroup.instances) }}%</span>
        </div>
      </div>

      <!-- CHECKLIST INSTANCES WITHIN THIS MODULE -->
      <div v-for="instance in moduleGroup.instances" :key="instance.id" class="instance-block">
        <div v-if="moduleGroup.instances.length > 1" class="instance-label">
          {{ instance.name }}
          <span class="instance-status" :class="instance.status.toLowerCase()">{{
            instance.status.replace('_', ' ')
          }}</span>
        </div>

        <div class="task-list">
          <ChecklistTaskItem
            v-for="task in instance.items"
            :key="task.id"
            :task="{
              id: task.id,
              title: task.title,
              description: task.description ?? '',
              completed: task.completed,
            }"
            @toggle="handleToggle"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.create-btn {
  background: #0f172a;
  color: white;
  border: none;
  padding: 10px 16px;
  border-radius: 12px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.15s ease;
}

.create-btn:hover {
  background: #1e293b;
}

.checklist-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* SUMMARY BAR */
.summary-bar {
  padding: 28px 32px;
  border-radius: 20px;
  background: #f3f4f5;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 32px;
}

.summary-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.summary-date h2 {
  margin: 0;
}

.subtitle {
  margin: 4px 0 0;
  color: var(--text-secondary);
  font-size: 14px;
}

.summary-progress {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.progress-track {
  height: 10px;
  background: #d1d5db;
  border-radius: 999px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #0f172a;
  border-radius: inherit;
  transition: width 0.4s ease;
}

.progress-stats {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: var(--text-secondary);
}

.stat {
  display: flex;
  align-items: center;
  gap: 6px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
  flex-shrink: 0;
}

.dot-done {
  background: #0f172a;
}

.dot-left {
  background: #9ca3af;
}

/* SUMMARY RIGHT */
.summary-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
  min-width: 180px;
}

.pct-box {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.pct-num {
  font-size: 36px;
  font-weight: 700;
  line-height: 1;
}

.pct-label {
  font-size: 11px;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.complete-btn {
  background: #0f172a;
  color: white;
  border: none;
  padding: 12px 20px;
  border-radius: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s;
  white-space: nowrap;
}

.complete-btn:hover {
  opacity: 0.85;
}

/* EMPTY */
.empty {
  text-align: center;
  padding: 48px 0;
  color: var(--text-secondary);
}

.empty-sub {
  font-size: 13px;
  margin-top: 6px;
}

/* MODULE SECTION */
.module-section {
  border-radius: 20px;
  border: 1.5px solid var(--stroke);
  overflow: hidden;
}

/* MODULE HEADER */
.module-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 24px;
  border-bottom: 1.5px solid var(--stroke);
}

.module-identity {
  display: flex;
  align-items: center;
  gap: 14px;
}

.module-abbr {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  color: white;
  font-size: 12px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  letter-spacing: 0.04em;
  flex-shrink: 0;
}

.module-name {
  font-size: 17px;
  font-weight: 700;
  margin: 0 0 2px;
}

.module-meta {
  font-size: 12px;
  color: var(--text-secondary);
  margin: 0;
}

/* RING CHART */
.module-pct-wrap {
  position: relative;
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ring {
  position: absolute;
  inset: 0;
  transform: rotate(-90deg);
}

.ring-bg {
  fill: none;
  stroke: #e5e7eb;
  stroke-width: 3.5;
}

.ring-fill {
  fill: none;
  stroke-width: 3.5;
  stroke-linecap: round;
  stroke-dasharray: 0 100;
  transition: stroke-dasharray 0.5s ease;
}

.module-pct {
  font-size: 12px;
  font-weight: 700;
  color: #0f172a;
  position: relative;
  z-index: 1;
}

/* INSTANCE BLOCK */
.instance-block {
  padding: 16px 24px;
  background: white;
}

.instance-block + .instance-block {
  border-top: 1px solid var(--stroke);
}

.instance-label {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.06em;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.instance-status {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 999px;
  font-weight: 600;
  letter-spacing: 0.05em;
  text-transform: uppercase;
}

.instance-status.pending {
  background: #e5e7eb;
  color: #374151;
}

.instance-status.in_progress {
  background: #fef9c3;
  color: #854d0e;
}

.instance-status.completed {
  background: #dcfce7;
  color: #14532d;
}

/* TASK LIST */
.task-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>
