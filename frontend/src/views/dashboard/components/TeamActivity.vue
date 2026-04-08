<script setup lang="ts">
import Card from '@/components/ui/Card.vue'
import { User2Icon, ChevronDownIcon } from '@lucide/vue'
import { ref, computed } from 'vue'

interface Activity {
  username: string
  task: string
  dateFinished: Date
}

const props = defineProps<{
  activities: any[]
}>()

const activity = computed(() => props.activities || [])

const showAll = ref(false)

// Display only first 3 activities or all if showAll is true
const displayedActivities = computed(() => {
  return showAll.value ? activity.value : activity.value.slice(0, 3)
})

// Format relative time
const formatTimeAgo = (date: Date): string => {
  const now = new Date()
  const diffMs = now.getTime() - date.getTime()
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)

  if (diffMins < 1) return 'just now'
  if (diffMins < 60) return `${diffMins} minute${diffMins > 1 ? 's' : ''} ago`
  if (diffHours < 24) return `${diffHours} hour${diffHours > 1 ? 's' : ''} ago`
  return `${diffDays} day${diffDays > 1 ? 's' : ''} ago`
}
</script>

<template>
  <div v-if="activity.length > 0">
    <div v-for="(item, index) in displayedActivities" :key="index" class="activity-item">
      <div class="activity-icon">
        <User2Icon :size="32" />
      </div>
      <div class="activity-content">
        <div class="activity-header">
          <p class="username">{{ item.actor }}</p>
          <span class="timestamp">{{ formatTimeAgo(new Date(item.occurredAt)) }}</span>
        </div>
        <p class="activity-task">{{ item.title }}</p>
      </div>
    </div>
    <button v-if="activity.length > 3" @click="showAll = !showAll" class="show-more-btn">
      <span>{{ showAll ? 'Show Less' : 'Show More' }}</span>
      <ChevronDownIcon :size="16" :class="{ rotated: showAll }" />
    </button>
  </div>
  <div v-else class="no-items-container">
    <p class="no-items">There seems there has been no recent activity...</p>
  </div>
</template>

<style scoped>
.activity-container {
  display: flex;
  flex-direction: column;
  outline: 1px solid var(--border);
  border-radius: 10px;
  gap: 5px;
  padding: 10px;
}

.activity-title {
  margin: 0;
  font-size: 1.125rem;
  font-weight: 600;
  color: #1f2937;
}

.activity-list {
  display: flex;
  flex-direction: column;
}

.activity-item {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
  padding: 0.75rem;
  transition: background-color 0.2s ease;
  border-radius: 8px;
}

.activity-item:hover {
  background-color: var(--bg-secondary);
}

.activity-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background-color: var(--border);
  border-radius: 50%;
  flex-shrink: 0;
  color: black;
}

.activity-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.activity-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.username {
  margin: 0;
}

.timestamp {
  font-size: 0.8rem;
  color: var(--text-secondary);
  white-space: nowrap;
  flex-shrink: 0;
}

.activity-task {
  margin: 0;
  color: #6b7280;
  font-size: 0.9rem;
  word-wrap: break-word;
}

.no-items {
  align-self: center;
}

.no-items-container {
  display: flex;
  align-items: center;
  justify-content: center;
}

.header {
  display: none;
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
  background-color: var(--bg-secondary);
}

.show-more-btn svg {
  transition: transform 0.2s ease;
}

.show-more-btn svg.rotated {
  transform: rotate(180deg);
}
</style>
