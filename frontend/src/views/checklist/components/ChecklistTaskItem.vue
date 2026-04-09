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

function onToggle(event: Event) {
  const checked = (event.target as HTMLInputElement).checked
  emit('toggle', props.task.id, checked)
}
</script>

<template>
  <div
    class="task"
    :class="{
      completed: task.completed,
      high: task.priority === 'high',
    }"
  >
    <!-- LEFT BORDER INDICATOR -->
    <div class="left-border" />

    <!-- checkbox -->
    <div class="checkbox">
      <input type="checkbox" :checked="task.completed" @change="onToggle" />
    </div>

    <!-- content -->
    <div class="content">
      <div class="title-row">
        <h3>{{ task.title }}</h3>
        <span v-if="task.priority === 'high'" class="priority-badge"> HIGH PRIORITY </span>
      </div>

      <p class="description">{{ task.description }}</p>

      <!-- optional comment -->
      <input v-if="!task.completed" class="comment-input" placeholder="Add optional comment..." />
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

.comment-input {
  margin-top: 10px;
  padding: 10px;
  border-radius: 10px;
  border: none;
  background: #f3f4f5;
  width: 100%;
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

@media (max-width: 640px) {
  .task {
    flex-direction: column;
    padding: 16px;
    gap: 14px;
  }

  .title-row {
    flex-wrap: wrap;
  }

  .right {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
