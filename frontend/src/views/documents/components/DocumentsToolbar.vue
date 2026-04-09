<script setup lang="ts">
import type { DocumentArea } from '@/interfaces/Document.interface'
import type { AreaOption } from '../documents.helpers'

const props = defineProps<{
  areaOptions: AreaOption[]
  selectedArea: DocumentArea | null
  searchQuery: string
  filterTagDraft: string
  selectedTags: string[]
  availableTags: string[]
}>()

const emit = defineEmits<{
  (event: 'update:selectedArea', value: DocumentArea | null): void
  (event: 'update:searchQuery', value: string): void
  (event: 'update:filterTagDraft', value: string): void
  (event: 'add-filter-tag', value: string): void
  (event: 'remove-filter-tag', value: string): void
}>()

function handleFilterTagKeydown(event: KeyboardEvent) {
  if (event.key === 'Enter' || event.key === ',') {
    event.preventDefault()
    emit('add-filter-tag', props.filterTagDraft)
  }
}
</script>

<template>
  <section class="toolbar-card">
    <div class="area-toggle" aria-label="Document area filter">
      <button
        v-for="option in areaOptions"
        :key="option.label"
        type="button"
        class="area-button"
        :class="{ 'area-button-active': selectedArea === option.value }"
        @click="emit('update:selectedArea', option.value)"
      >
        {{ option.label }}
      </button>
    </div>

    <div class="toolbar-grid">
      <label class="field">
        <span>Keyword search</span>
        <input
          :value="searchQuery"
          type="search"
          placeholder="Title, filename, description, or tag"
          @input="emit('update:searchQuery', ($event.target as HTMLInputElement).value)"
        />
      </label>

      <label class="field">
        <span>Filter by tag</span>
        <input
          :value="filterTagDraft"
          type="text"
          placeholder="Press Enter to add a tag"
          @input="emit('update:filterTagDraft', ($event.target as HTMLInputElement).value)"
          @keydown="handleFilterTagKeydown"
        />
      </label>
    </div>

    <div v-if="selectedTags.length > 0" class="tag-row">
      <button
        v-for="tag in selectedTags"
        :key="tag"
        type="button"
        class="tag-chip"
        @click="emit('remove-filter-tag', tag)"
      >
        {{ tag }} ×
      </button>
    </div>

    <div v-if="availableTags.length > 0" class="suggestion-row">
      <span class="suggestion-label">Available tags</span>
      <button
        v-for="tag in availableTags"
        :key="tag"
        type="button"
        class="suggestion-chip"
        :disabled="selectedTags.includes(tag)"
        @click="emit('add-filter-tag', tag)"
      >
        {{ tag }}
      </button>
    </div>
  </section>
</template>

<style scoped>
.toolbar-card {
  padding: 20px;
  border: 1px solid rgba(148, 163, 184, 0.28);
  border-radius: 20px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.98)),
    radial-gradient(circle at top right, rgba(254, 240, 138, 0.22), transparent 34%);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.08);
}

.area-toggle {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.area-button,
.tag-chip,
.suggestion-chip {
  border-radius: 999px;
  border: 1px solid transparent;
  cursor: pointer;
  transition:
    transform 140ms ease,
    background-color 140ms ease,
    border-color 140ms ease;
}

.area-button {
  padding: 10px 14px;
  background: #e2e8f0;
  color: #0f172a;
}

.area-button-active {
  background: #0f172a;
  color: #fff;
}

.toolbar-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-top: 18px;
}

.field {
  display: grid;
  gap: 8px;
  color: #0f172a;
  font-weight: 600;
}

.field input {
  width: 100%;
  border: 1px solid #cbd5e1;
  border-radius: 14px;
  padding: 12px 14px;
  font: inherit;
  background: #fff;
}

.tag-row,
.suggestion-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
  margin-top: 16px;
}

.tag-chip {
  padding: 8px 12px;
  background: #dbeafe;
  color: #1d4ed8;
}

.suggestion-label {
  color: #64748b;
  font-size: 0.95rem;
}

.suggestion-chip {
  padding: 8px 12px;
  background: #f8fafc;
  border-color: #cbd5e1;
}

.area-button:hover,
.tag-chip:hover,
.suggestion-chip:hover {
  transform: translateY(-1px);
}

.suggestion-chip:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 900px) {
  .toolbar-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .toolbar-card {
    padding: 14px;
    border-radius: 16px;
    overflow: hidden;
  }

  .area-toggle,
  .tag-row,
  .suggestion-row {
    gap: 8px;
  }

  .area-button,
  .tag-chip,
  .suggestion-chip {
    width: 100%;
    justify-content: center;
  }

  .toolbar-grid {
    gap: 12px;
    margin-top: 14px;
  }

  .field input {
    min-width: 0;
  }
}
</style>
