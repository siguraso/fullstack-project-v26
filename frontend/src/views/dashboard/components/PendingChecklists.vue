<script setup lang="ts">
import TaskListItem from '@/components/ui/TaskListItem.vue'
import { BottleWine, BrushCleaning, ForkKnife, ChevronDownIcon } from '@lucide/vue'
import { ref, computed, watchEffect } from 'vue'
import Card from '@/components/ui/Card.vue'
import { updateChecklistItem } from '@/services/checklist'

const props = defineProps<{
  checklists: any[]
}>()

const tasks = ref<any[]>([])

const showAll = ref(false)

const emit = defineEmits(['updated'])

const tasksList = watchEffect(() => {
  tasks.value = (props.checklists || []).flatMap((c: any) =>
    (c.items || [])
      .filter((item: any) => !item.completed)
      .map((item: any) => ({
        id: item.id,
        taskTitle: item.title,
        taskType: c.name,
        completed: item.completed,
        dueDate: item.dueDate,
      })),
  )
})

const displayedTasks = computed(() => {
  return showAll.value ? tasks.value : tasks.value.slice(0, 3)
})

async function toggleTask(id: number, completed: boolean) {
  const task = tasks.value.find((t) => t.id === id)
  if (task) task.completed = completed

  try {
    await updateChecklistItem(id, completed)
    if (completed) {
      emit('updated')
    }
  } catch (e) {
    if (task) task.completed = !completed
  }
}
</script>

<template>
  <Card :headerDisabled="true">
    <template #card-content>
      <div class="task-wrapper">
        <TransitionGroup name="task" tag="div" class="tasks">
          <TaskListItem
            v-for="task in displayedTasks"
            :key="task.id"
            :id="task.id"
            :taskTitle="task.taskTitle"
            :taskType="task.taskType"
            :dueDate="task.dueDate"
            :icon="task.icon"
            :completed="task.completed"
            @toggle="toggleTask"
          />
        </TransitionGroup>
        <div v-if="tasks.length === 0" class="empty-state">
          <p>All tasks completed 🎉</p>
        </div>
      </div>
      <button v-if="tasksList.length > 3" @click="showAll = !showAll" class="show-more-btn">
        <span>{{ showAll ? 'Show Less' : 'Show More' }}</span>
        <ChevronDownIcon :size="16" :class="{ rotated: showAll }" />
      </button>
    </template>
  </Card>
</template>

<style scoped>
.tasks-container {
  display: flex;
  flex-direction: column;
  padding: 10px;
  border: 1px solid var(--border);
  border-radius: 10px;
}

.task-wrapper {
  position: relative;
  min-height: 180px;
}

.tasks {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.empty-state {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}

.show-more-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  margin-top: 0.5rem;
  background-color: transparent;
  border-radius: 0.5rem;
  cursor: pointer;
  font-weight: 500;
  color: var(--primary);
  transition: all 0.2s ease;
}

.show-more-btn:hover {
  background-color: #f3f4f6;
  color: #1f2937;
}

.show-more-btn svg {
  transition: transform 0.2s ease;
}

.show-more-btn svg.rotated {
  transform: rotate(180deg);
}

/* ANIMATION */

.task-enter-active {
  transition: all 0.25s ease;
}

.task-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.task-leave-active {
  transition: all 0.3s ease;
}

.task-leave-from {
  opacity: 1;
  transform: translateY(0);
}

.task-leave-to {
  opacity: 0;
  transform: translateY(20px);
}

.task-move {
  transition: transform 0.3s ease;
}
</style>
