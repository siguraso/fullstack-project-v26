<script setup lang="ts">
const props = defineProps<{
  task: {
    id: number
    title: string
    description: string
    completed: boolean
    priority?: string
    time?: string
  }
}>()

const emit = defineEmits<{
  toggle: [id: number, completed: boolean]
}>()

function toggle() {
  emit('toggle', props.task.id, !props.task.completed)
}
</script>

<template>
  <div
    class="task"
    :class="{
      completed: task.completed,
      high: task.priority === 'high',
    }"
    @click="toggle"
  >
    <!-- LEFT BORDER INDICATOR -->
    <div class="left-border" />

    <!-- checkbox -->
    <div class="checkbox" @click.stop>
      <input type="checkbox" :checked="task.completed" @change="toggle" />
    </div>

    <!-- content -->
    <div class="content">
      <div class="title-row">
        <h3>{{ task.title }}</h3>
        <span v-if="task.priority === 'high'" class="priority-badge"> HIGH PRIORITY </span>
      </div>

      <p class="description">{{ task.description }}</p>
    </div>

    <!-- right side -->
    <div class="right">
      <span v-if="task.time" class="time">{{ task.time }}</span>
    </div>
  </div>
</template>

<style scoped>
.task {
  display: flex;
  align-items: flex-start;
  padding: 20px;
  border-radius: 16px;
  background: #ffffff;
  gap: 16px;
  position: relative;
  cursor: pointer;
  transition: all 0.2s ease;
}

/* 👇 darker when completed */
.task.completed {
  opacity: 0.7;
  background: #f1f5f9;
}

.task:hover {
  background: #f8fafc;
}

.task.completed:hover {
  background: #e2e8f0;
}

.task.completed .left-border {
  background: #22c55e;
}

.task.high .left-border {
  background: #dc2626;
}

.checkbox input {
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.content {
  flex: 1;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.content h3 {
  margin: 0;
  font-size: 18px;
  transition: color 0.35s ease;
}

.content p {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--text-secondary);
}

.task.completed h3 {
  text-decoration: line-through;
  color: #9ca3af;
}

.description {
  margin: 6px 0 0;
  font-size: 14px;
  color: var(--text-secondary);
}

.right {
  display: flex;
  align-items: flex-start;
}

.time {
  background: #dcfce7;
  color: #16a34a;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 999px;
}

.priority-badge {
  font-size: 12px;
  color: #dc2626;
  background: #fee2e2;
  padding: 4px 8px;
  border-radius: 999px;
  font-weight: bold;
}
</style>
