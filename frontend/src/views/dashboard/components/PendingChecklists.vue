<script setup lang="ts">
import TaskListItem from '@/components/ui/TaskListItem.vue'
import { BottleWine, BrushCleaning, ForkKnife, ChevronDownIcon } from '@lucide/vue'
import { ref, computed } from 'vue'
import Card from '@/components/ui/Card.vue'

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
  task4: {
    taskTitle: 'Clean the kitchen',
    taskType: 'General Cleanliness',
    dueDate: new Date(),
    icon: BrushCleaning,
  },
}

const showAll = ref(false)

const tasksList = computed(() => Object.values(tasks))

const displayedTasks = computed(() => {
  return showAll.value ? tasksList.value : tasksList.value.slice(0, 3)
})
</script>

<template>
  <Card :headerDisabled="true">
    <template #card-content>
      <div class="tasks">
        <TaskListItem
          v-for="task in displayedTasks"
          :key="task.taskTitle"
          :taskTitle="task.taskTitle"
          :taskType="task.taskType"
          :dueDate="task.dueDate"
          :icon="task.icon"
        />
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
  border: 1px solid var(--stroke);
  border-radius: 10px;
}

.tasks {
  display: flex;
  flex-direction: column;
  gap: 10px;
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
</style>
