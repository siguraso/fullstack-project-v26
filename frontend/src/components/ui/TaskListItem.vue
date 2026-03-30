<script setup lang="ts">
interface TaskListItemProps {
  taskTitle: string
  taskType: string
  dueDate: Date
  icon: any
}

const props = defineProps<TaskListItemProps>()
</script>

<template>
  <div class="task-item">
    <div class="task-description">
      <component :is="props.icon" />
      <div class="task-header">
        <h3 class="task-title">{{ props.taskTitle }}</h3>
        <p class="task-subtext">Type: {{ props.taskType }}</p>
        <p class="task-subtext">Due: {{ props.dueDate.toDateString() }}</p>
      </div>
    </div>
    <button class="finish-btn" type="button">Mark As Finished</button>
  </div>
</template>

<style scoped>
.task-description {
  display: flex;
  flex-direction: row;
  gap: 10px;
  align-items: center;
}

.task-item {
  padding: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-radius: 8px;
}

.task-item:hover {
  background-color: var(--bg-secondary);
}

.task-header {
  align-self: flex-start;
}

.task-header h3 {
  font-weight: bold;
  font-size: 16px;
  margin: 0;
}

.task-header .task-subtext {
  font-size: 12px;
  margin-top: 0px;
  margin-bottom: 0px;
  color: var(--text-secondary);
}
</style>
