<script setup lang="ts">
import { deleteTemplate, generateChecklist, toggleTemplate } from '@/services/checklist'

const emit = defineEmits<{
  edit: [template: any]
  deleted: []
}>()

const props = defineProps<{
  templates: any[]
}>()

const moduleColor: Record<string, string> = {
  IK_FOOD: '#16a34a',
  IK_ALCOHOL: '#dc2626',
}

const moduleLabel: Record<string, string> = {
  IK_FOOD: 'IK-Food',
  IK_ALCOHOL: 'IK-Alcohol',
}

function accentFor(module: string) {
  return moduleColor[module] ?? '#475569'
}

async function confirmDelete(id: number) {
  if (!Number.isFinite(id)) return
  if (confirm('Delete this template? This cannot be undone.')) {
    await deleteTemplate(id)
    emit('deleted')
  }
}

async function toggle(id: number) {
  if (!Number.isFinite(id)) return
  await toggleTemplate(id)
  emit('deleted')
}
</script>

<template>
  <section v-if="props.templates.length > 0" class="tl">
    <div class="tl-header">
      <h2 class="tl-title">Templates</h2>
      <span class="tl-count">{{ props.templates.length }}</span>
    </div>

    <div class="tl-grid">
      <div
        v-for="t in props.templates"
        :key="t.id"
        class="tl-card"
        :class="{ 'tl-inactive': !t.active }"
        :style="{ '--accent': accentFor(t.module) }"
      >
        <!-- Top row: module + frequency tags -->
        <div class="tl-tags">
          <span
            class="tl-module-tag"
            :style="{ background: accentFor(t.module) + '18', color: accentFor(t.module) }"
          >
            {{ moduleLabel[t.module] ?? t.module }}
          </span>
          <span class="tl-freq-tag">{{ t.frequency }}</span>
        </div>

        <!-- Name -->
        <h3 class="tl-name">{{ t.name }}</h3>
        <p class="tl-meta">{{ t.items?.length ?? 0 }} items</p>

        <!-- Actions -->
        <div class="tl-actions">
          <button class="tl-btn-edit" @click="emit('edit', t)">Edit</button>
          <button
            class="tl-btn-icon"
            :title="t.active ? 'Deactivate' : 'Activate'"
            @click="toggle(t.id)"
          >
            {{ t.active ? '⏸' : '▶' }}
          </button>
          <button class="tl-btn-icon tl-btn-delete" title="Delete" @click="confirmDelete(t.id)">
            ✕
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.tl {
  margin-bottom: 28px;
}

.tl-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.tl-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}

.tl-count {
  font-size: 12px;
  background: #e5e7eb;
  color: #374151;
  padding: 2px 8px;
  border-radius: 999px;
  font-weight: 600;
}

/* GRID */
.tl-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 14px;
}

/* CARD */
.tl-card {
  background: white;
  border-radius: 16px;
  padding: 16px;
  border: 1.5px solid var(--stroke);
  border-left: 4px solid var(--accent, #475569);
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.tl-inactive {
  opacity: 0.5;
}

/* TAGS */
.tl-tags {
  display: flex;
  gap: 6px;
  align-items: center;
  margin-bottom: 4px;
}

.tl-module-tag {
  font-size: 11px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 999px;
  letter-spacing: 0.03em;
}

.tl-freq-tag {
  font-size: 11px;
  color: #64748b;
  background: #f1f5f9;
  padding: 2px 8px;
  border-radius: 999px;
}

/* TEXT */
.tl-name {
  margin: 0;
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
  line-height: 1.3;
}

.tl-meta {
  margin: 0;
  font-size: 12px;
  color: #94a3b8;
}

/* ACTIONS */
.tl-actions {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-top: 10px;
}

.tl-btn-edit {
  flex: 1;
  padding: 8px 0;
  border: 1.5px solid var(--stroke);
  background: white;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  color: #0f172a;
}

.tl-btn-edit:hover {
  background: #f8fafc;
}

.tl-btn-use {
  flex: 1;
  padding: 8px 0;
  background: #0f172a;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
}

.tl-btn-use:hover {
  background: #1e293b;
}

.tl-btn-icon {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1.5px solid var(--stroke);
  background: white;
  border-radius: 8px;
  font-size: 12px;
  cursor: pointer;
  color: #475569;
  flex-shrink: 0;
}

.tl-btn-icon:hover {
  background: #f1f5f9;
}

.tl-btn-delete:hover {
  background: #fee2e2;
  border-color: #fca5a5;
  color: #dc2626;
}

@media (max-width: 1100px) {
  .cards {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .filters {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .filters button {
    margin-left: 0;
  }

  .cards {
    grid-template-columns: 1fr;
  }

  .actions {
    flex-direction: column;
  }

  .actions button {
    width: 100%;
  }
}
</style>
