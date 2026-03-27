<script lang="ts" setup>
import Card from '@/components/ui/Card.vue'

// TODO: database connectivity

const tasks = [
  { id: 1, title: 'Complete safety checklist', dueDate: new Date().toISOString().split('T')[0] },
  { id: 2, title: 'Review incident report', dueDate: new Date().toISOString().split('T')[0] },
  { id: 3, title: 'Update temperature logs', dueDate: new Date().toISOString().split('T')[0] },
]
</script>

<template>
  <Card class="tasks">
    <template #card-header> My Tasks </template>

    <template #card-content>
      <div v-if="tasks.length > 0" class="task-list">
        <div v-for="task in tasks" :key="task.id" class="task-item">
          <div class="task-header">
            <h3 class="task-title">{{ task.title }}</h3>
            <p class="task-due">Due: {{ task.dueDate }}</p>
          </div>
          <button class="finish-btn" type="button">Mark As Finished</button>
        </div>
      </div>
      <div v-else class="tile-state">
        <p>No assigned tasks right now.</p>
        <p class="state-note">You are all caught up.</p>
      </div>
    </template>
  </Card>
</template>

<style scoped>
.task-item {
  border: 1px solid var(--stroke);
  padding-left: 13px;
  padding-right: 13px;
  margin-bottom: 20px;
  border-radius: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-header {
  align-self: flex-start;
}

.task-header h3 {
  margin-bottom: 0;
  font-size: 16px;
}

.task-header p {
  font-size: 14px;
  margin-top: 0px;
  color: var(--text-secondary);
}
</style>
