<script setup lang="ts">
import TaskListItem from '@/components/ui/TaskListItem.vue'
import { BottleWine, BrushCleaning, ForkKnife } from '@lucide/vue'

// placeholder
// TODO: make acrually database intergration
const tasks = {
  task1: {
    taskTitle: 'Clean the washroom',
    taskType: 'General Cleanliness',
    dueDate: new Date(),
    icon: BrushCleaning,
  },
  task2: {
    taskTitle: 'Clean the bar',
    taskType: 'Alcohol Compliance',
    dueDate: new Date(),
    icon: BottleWine,
  },
  task3: {
    taskTitle: 'Prepare for weekly food inspection',
    taskType: 'Food Compliance',
    dueDate: new Date(),
    icon: ForkKnife,
  },
}
</script>

<template>
  <div class="tasks">
    <TaskListItem
      v-for="task in tasks"
      :taskTitle="task.taskTitle"
      :taskType="task.taskType"
      :dueDate="task.dueDate"
      :icon="task.icon"
    />
  </div>
</template>

<style>
.tasks {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px;
  border: 1px solid var(--stroke);
  border-radius: 10px;
}
</style>
