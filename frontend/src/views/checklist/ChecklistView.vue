<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import ViewHeader from '@/components/ui/ViewHeader.vue'
import ChecklistTaskItem from './components/ChecklistTaskItem.vue'
import { getTodayChecklist, updateChecklistItem } from '@/services/checklist'
import { useTenant } from '@/services/useTenant'

interface Task {
  id: number
  title: string
  description: string
  completed: boolean
  priority?: 'high'
  time?: string
}

const tasks = ref<any[]>([]) // DTO fra backend

const tenantId = useTenant().tenantId

onMounted(async () => {
  const data = await getTodayChecklist(tenantId)

  tasks.value = data.flatMap((c: { items: any }) => c.items)
})

async function handleToggle(id: number, completed: boolean) {
  const task = tasks.value.find((t) => t.id === id)
  if (!task) return

  task.completed = completed

  try {
    await updateChecklistItem(id, completed)
  } catch (e) {
    task.completed = !completed
  }
}

const today = new Date().toLocaleDateString('en-GB', {
  weekday: 'long',
  day: 'numeric',
  month: 'long',
})

const sortedTasks = computed(() => {
  return [...tasks.value].sort((a, b) => {
    if (a.completed === b.completed) return 0
    return a.completed ? 1 : -1
  })
})

const completed = computed(() => tasks.value.filter((t) => t.completed).length)

const percentage = computed(() => {
  if (tasks.value.length === 0) return 0
  return Math.round((completed.value / tasks.value.length) * 100)
})
</script>

<template>
  <div>
    <ViewHeader title="Checklists" :options="[]" :routes="[]" />

    <!-- HEADER -->
    <section class="top">
      <!-- LEFT -->
      <div class="top-left">
        <div class="title-row">
          <div>
            <h2>Daily Operations</h2>
            <p class="subtitle">{{ today }} • Kitchen Shift</p>
          </div>

          <div class="percentage-box">
            <span class="percentage-number">{{ percentage }}%</span>
            <span class="percentage-label">Completion</span>
          </div>
        </div>

        <div class="progress">
          <div class="fill" :style="{ width: percentage + '%' }" />
        </div>

        <div class="stats">
          <span class="completed-dot" />
          {{ completed }} completed

          <span class="remaining-dot" />
          {{ tasks.length - completed }} remaining
        </div>
      </div>

      <!-- RIGHT -->
      <div class="top-right">
        <div class="focus-card">
          <div class="focus-icon">IC</div>
          <div>
            <p class="focus-label">Current Focus</p>
            <h3>Kitchen Hygiene</h3>
          </div>
        </div>

        <button class="complete-btn">✓ Complete All</button>
      </div>
    </section>

    <!-- TASK LIST -->
    <transition-group name="list" tag="div" class="task-list">
      <ChecklistTaskItem
        v-for="task in sortedTasks"
        :key="task.id"
        :task="{
          id: task.id,
          title: task.title,
          description: task.description ?? '',
          completed: task.completed,
        }"
        @toggle="handleToggle"
      />
    </transition-group>
  </div>
</template>

<style scoped>
.top {
  padding: 32px;
  border-radius: 24px;
  background-color: #f3f4f5;
  display: flex;
  justify-content: space-between;
  gap: 32px;
  margin-bottom: 24px;
}

/* LEFT SIDE */
.top-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.subtitle {
  color: var(--text-secondary);
  margin-top: 4px;
}

/* percentage box */
.percentage-box {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.percentage-number {
  font-size: 32px;
  font-weight: bold;
}

.percentage-label {
  font-size: 12px;
  color: var(--text-secondary);
  text-transform: uppercase;
}

/* progress */
.progress {
  height: 10px;
  background: #d1d5db;
  border-radius: 999px;
  overflow: hidden;
}

.fill {
  height: 100%;
  background: #0f172a;
  border-radius: inherit;
}

/* stats */
.stats {
  display: flex;
  gap: 16px;
  align-items: center;
  font-size: 14px;
}

.completed-dot,
.remaining-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  display: inline-block;
}

.completed-dot {
  background: #0f172a;
}

.remaining-dot {
  background: #9ca3af;
}

/* RIGHT SIDE */
.top-right {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 220px;
}

/* focus card */
.focus-card {
  background: white;
  padding: 16px;
  border-radius: 16px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.focus-icon {
  background: #e5e7eb;
  padding: 10px;
  border-radius: 10px;
}

.focus-label {
  font-size: 12px;
  color: var(--text-secondary);
  margin: 0;
}

.focus-card h3 {
  margin: 0;
}

/* button */
.complete-btn {
  background: #0f172a;
  color: white;
  border: none;
  padding: 14px;
  border-radius: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: 0.2s;
}

.complete-btn:hover {
  opacity: 0.9;
}

.progress-wrapper {
  width: 200px;
}

.task-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.list-move,
.list-enter-active,
.list-leave-active {
  transition: all 0.4s ease;
}

.list-enter-from,
.list-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.list-leave-active {
  position: absolute;
}
</style>
