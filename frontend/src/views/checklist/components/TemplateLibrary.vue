<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface Template {
  id: number
  name: string
  frequency: 'DAILY' | 'WEEKLY' | 'MONTHLY'
  itemsCount: number
  description?: string
}

const emit = defineEmits<{
  edit: [template: any]
}>()

const props = defineProps<{
  templates: any[]
}>()

function getBadgeColor(freq: string) {
  if (freq === 'DAILY') return 'blue'
  if (freq === 'WEEKLY') return 'gray'
  if (freq === 'MONTHLY') return 'red'
  return 'gray'
}
</script>

<template>
  <section class="template-library">
    <div class="header">
      <h2>Template Library</h2>

      <div class="filters">
        <button class="active">All</button>
        <button>Kitchen</button>
        <button>Safety</button>
      </div>
    </div>

    <div class="cards">
      <div v-for="template in props.templates" :key="template.id" class="card">
        <!-- icon -->
        <div class="icon" :class="getBadgeColor(template.frequency)" />

        <!-- badge -->
        <span class="badge">{{ template.frequency }}</span>

        <!-- title -->
        <h3>{{ template.name }}</h3>
        <p>{{ template.module }}</p>

        <!-- meta -->
        <div class="meta">
          <span>{{ template.items?.length || 0 }} items</span>
          <span>•</span>
          <span>{{ template.frequency }}</span>
        </div>

        <!-- actions -->
        <div class="actions">
          <button class="primary">Use Template</button>
          <button class="secondary" @click="emit('edit', template)">Edit</button>
        </div>
      </div>
    </div>
  </section>
</template>

<style>
.template-library {
  margin-bottom: 32px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.filters button {
  border: none;
  background: transparent;
  margin-left: 10px;
  cursor: pointer;
}

.filters .active {
  background: #e5e7eb;
  padding: 6px 12px;
  border-radius: 999px;
}

/* GRID */
.cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

/* CARD */
.card {
  background: white;
  border-radius: 16px;
  padding: 18px;
  border: 1px solid var(--stroke);
  position: relative;
  box-shadow: 0 4px 0 rgba(0, 0, 0, 0.05);
}

.icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  margin-bottom: 12px;
}

.icon.blue {
  background: #dbeafe;
}
.icon.gray {
  background: #e5e7eb;
}
.icon.red {
  background: #fee2e2;
}

/* BADGE */
.badge {
  position: absolute;
  top: 16px;
  right: 16px;
  font-size: 11px;
  background: #e5e7eb;
  padding: 4px 8px;
  border-radius: 999px;
}

/* TEXT */
.card h3 {
  margin: 0;
  font-size: 16px;
}

.card p {
  margin: 6px 0 12px;
  font-size: 13px;
  color: var(--text-secondary);
}

/* META */
.meta {
  font-size: 12px;
  color: var(--text-secondary);
  display: flex;
  gap: 6px;
  margin-bottom: 12px;
}

/* ACTIONS */
.actions {
  display: flex;
  gap: 10px;
}

.primary {
  flex: 1;
  background: var(--neutral);
  color: white;
  border: none;
  padding: 10px;
  border-radius: 10px;
}

.secondary {
  color: black;
  border: 1px solid var(--stroke);
  background: transparent;
  padding: 10px;
  border-radius: 10px;
}

.secondary:hover {
  background-color: #f8fafc;
}
</style>
