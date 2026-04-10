<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import TemplateLibrary from './components/TemplateLibrary.vue'
import PresetItemPicker from './components/PresetItemPicker.vue'
import {
  createLibraryItem,
  createTemplate,
  deleteLibraryItem,
  getLibraryItems,
  getTemplates,
  updateLibraryItem,
  updateTemplate,
} from '@/services/checklist'
import draggable from 'vuedraggable'
import { createLogger } from '@/services/util/logger'

interface Item {
  id: number
  title: string
  description: string
  inUse: boolean
}

// ── Static options ─────────────────────────────────────────────────────────────

const modules = [
  { value: 'IK_FOOD', label: 'IK-Food' },
  { value: 'IK_ALCOHOL', label: 'IK-Alcohol' },
] as const

const frequencies = [
  { value: 'DAILY', label: 'Daily' },
  { value: 'WEEKLY', label: 'Weekly' },
  { value: 'MONTHLY', label: 'Monthly' },
] as const

// ── State ──────────────────────────────────────────────────────────────────────

const checklistName = ref('')
const moduleType = ref<'IK_FOOD' | 'IK_ALCOHOL'>('IK_FOOD')
const frequency = ref<'DAILY' | 'WEEKLY' | 'MONTHLY'>('DAILY')
const activeItems = ref<Item[]>([])
const libraryItems = ref<Item[]>([])
const templates = ref<any[]>([])
const selectedTemplate = ref<any | null>(null)
const search = ref('')
const showPresetPicker = ref(false)
const showModal = ref(false)
const editingItem = ref<Item | null>(null)
const logger = createLogger('checklist-builder-view')

const newItem = ref({
  title: '',
  description: '',
  priority: 'LOW',
  category: 'IK_FOOD',
})

// ── Computed ───────────────────────────────────────────────────────────────────

const isValidChecklist = computed(
  () => checklistName.value.trim().length > 0 && activeItems.value.length > 0,
)

const filteredItems = computed(() =>
  libraryItems.value.filter((item) =>
    item.title.toLowerCase().includes(search.value.toLowerCase()),
  ),
)

const isEditing = computed(() => selectedTemplate.value !== null)

// ── Data loading ───────────────────────────────────────────────────────────────

onMounted(async () => {
  logger.info('builder bootstrap started')
  try {
    const [templatesData, libraryData] = await Promise.all([getTemplates(), getLibraryItems()])
    templates.value = Array.isArray(templatesData) ? templatesData : []
    libraryItems.value = Array.isArray(libraryData) ? libraryData : []
    logger.info('builder bootstrap succeeded', {
      templateCount: templates.value.length,
      libraryItemCount: libraryItems.value.length,
    })
  } catch (error) {
    templates.value = []
    libraryItems.value = []
    logger.error('builder bootstrap failed', error)
  }
})

async function reloadTemplates() {
  logger.info('template reload started')

  try {
    const data = await getTemplates()
    templates.value = Array.isArray(data) ? data : []
    logger.info('template reload succeeded', { templateCount: templates.value.length })
  } catch (error) {
    logger.error('template reload failed', error)
    throw error
  }
}

// ── Template actions ───────────────────────────────────────────────────────────

function loadTemplate(template: any) {
  if (!template || typeof template.id !== 'number') {
    logger.warn('template load skipped because template was invalid')
    return
  }

  selectedTemplate.value = template
  checklistName.value = template.name
  frequency.value = template.frequency
  moduleType.value = template.module ?? 'IK_FOOD'
  const rawItems = Array.isArray(template.items) ? template.items : []
  activeItems.value = rawItems
    .filter((item: any) => item && typeof item.id === 'number')
    .map((item: any) => ({
      id: item.id,
      title: item.title,
      description: item.description,
      inUse: false,
    }))
  logger.info('template loaded into builder', {
    templateId: template.id,
    itemCount: activeItems.value.length,
    module: moduleType.value,
  })
}

function resetBuilder() {
  checklistName.value = ''
  activeItems.value = []
  selectedTemplate.value = null
  frequency.value = 'DAILY'
  moduleType.value = 'IK_FOOD'
  logger.info('builder reset')
}

async function saveChecklist() {
  if (!isValidChecklist.value) {
    logger.warn('checklist save skipped because builder state was invalid', {
      checklistNameLength: checklistName.value.trim().length,
      itemCount: activeItems.value.length,
    })
    return
  }

  const payload = {
    name: checklistName.value,
    module: moduleType.value,
    frequency: frequency.value,
    itemIds: activeItems.value.map((i) => i.id),
  }

  logger.info('checklist save started', {
    isEditing: Boolean(selectedTemplate.value),
    itemCount: payload.itemIds.length,
    module: payload.module,
    frequency: payload.frequency,
  })

  try {
    if (selectedTemplate.value) {
      await updateTemplate(selectedTemplate.value.id, payload)
    } else {
      await createTemplate(payload)
    }

    await reloadTemplates()
    resetBuilder()
    document.getElementById('cb-builder')?.scrollIntoView({ behavior: 'smooth' })
    logger.info('checklist save succeeded', {
      isEditing: Boolean(selectedTemplate.value),
      itemCount: payload.itemIds.length,
    })
  } catch (error) {
    logger.error('checklist save failed', error, {
      isEditing: Boolean(selectedTemplate.value),
      itemCount: payload.itemIds.length,
    })
    throw error
  }
}

// ── Item actions ───────────────────────────────────────────────────────────────

function addItem(item: Item) {
  if (!activeItems.value.find((i) => i.id === item.id)) {
    activeItems.value.push(item)
    logger.info('builder item added', { itemId: item.id, activeItemCount: activeItems.value.length })
    return
  }

  logger.warn('builder item add skipped because item already exists', { itemId: item.id })
}

function removeItem(id: number) {
  activeItems.value = activeItems.value.filter((i) => i.id !== id)
  logger.info('builder item removed', { itemId: id, activeItemCount: activeItems.value.length })
}

function openEditItem(item: Item) {
  editingItem.value = item
  newItem.value = {
    title: item.title,
    description: item.description,
    priority: 'LOW',
    category: 'IK_FOOD',
  }
  showModal.value = true
  logger.info('library item editor opened', { itemId: item.id })
}

function openNewItem() {
  editingItem.value = null
  newItem.value = { title: '', description: '', priority: 'LOW', category: 'IK_FOOD' }
  showModal.value = true
  logger.info('library item creator opened')
}

async function saveItem() {
  if (!newItem.value.title.trim()) {
    logger.warn('library item save skipped because title was empty')
    return
  }

  const payload = {
    title: newItem.value.title,
    description: newItem.value.description,
    category: newItem.value.category,
    priority: newItem.value.priority,
  }

  logger.info('library item save started', {
    isEditing: Boolean(editingItem.value),
    category: payload.category,
  })

  try {
    if (editingItem.value) {
      const updated = await updateLibraryItem(editingItem.value.id, payload)
      const index = libraryItems.value.findIndex((i) => i.id === updated.id)
      if (index !== -1) {
        libraryItems.value[index] = { ...updated, inUse: libraryItems.value[index]?.inUse ?? false }
      }
    } else {
      const saved = await createLibraryItem(payload)
      libraryItems.value.push({ ...saved, inUse: false })
      activeItems.value.push({ ...saved, inUse: false })
    }

    showModal.value = false
    logger.info('library item save succeeded', {
      isEditing: Boolean(editingItem.value),
      libraryItemCount: libraryItems.value.length,
    })
  } catch (error) {
    logger.error('library item save failed', error, {
      isEditing: Boolean(editingItem.value),
    })
    throw error
  }
}

async function removeLibraryItem(id: number) {
  logger.info('library item delete started', { itemId: id })

  try {
    await deleteLibraryItem(id)
    libraryItems.value = libraryItems.value.filter((i) => i.id !== id)
    activeItems.value = activeItems.value.filter((i) => i.id !== id)
    logger.info('library item delete succeeded', { itemId: id, libraryItemCount: libraryItems.value.length })
  } catch (error) {
    logger.error('library item delete failed', error, { itemId: id })
    throw error
  }
}

async function addPresets(
  items: { title: string; description: string; category: string; priority: string }[],
) {
  showPresetPicker.value = false
  logger.info('preset import started', { presetCount: items.length })

  try {
    for (const preset of items) {
      const saved = await createLibraryItem(preset)
      libraryItems.value.push({ ...saved, inUse: false })
      activeItems.value.push({ ...saved, inUse: false })
    }

    logger.info('preset import succeeded', {
      presetCount: items.length,
      libraryItemCount: libraryItems.value.length,
      activeItemCount: activeItems.value.length,
    })
  } catch (error) {
    logger.error('preset import failed', error, { presetCount: items.length })
    throw error
  }
}
</script>

<template>
  <div class="cb-page">
    <!-- Page intro -->
    <div class="cb-intro">
      <div>
        <h1>Checklist Builder</h1>
        <p class="cb-intro-sub">Build compliance templates and manage your item library.</p>
      </div>
    </div>

    <!-- Template library -->
    <TemplateLibrary :templates="templates" @edit="loadTemplate" @deleted="reloadTemplates" />

    <!-- Builder + Library -->
    <div id="cb-builder" class="cb-grid">
      <!-- ── LEFT: Builder ── -->
      <div class="cb-builder-card">
        <!-- Header row -->
        <div class="cb-builder-top">
          <div class="cb-builder-title-wrap">
            <span v-if="isEditing" class="cb-editing-badge">Editing</span>
            <h2 class="cb-builder-heading">
              {{ isEditing ? selectedTemplate.name : 'New Template' }}
            </h2>
          </div>
          <div class="cb-builder-actions">
            <button class="cb-btn-discard" @click="resetBuilder">Discard</button>
            <button class="cb-btn-save" :disabled="!isValidChecklist" @click="saveChecklist">
              Save & Publish
            </button>
          </div>
        </div>

        <!-- Name -->
        <div class="cb-field-group">
          <label class="cb-label">TEMPLATE NAME</label>
          <input
            v-model="checklistName"
            placeholder="e.g., Morning Food Safety Checklist"
            class="cb-name-input"
          />
        </div>

        <!-- Module + Frequency pills -->
        <div class="cb-settings-row">
          <div class="cb-field-group">
            <label class="cb-label">MODULE</label>
            <div class="cb-pills">
              <button
                v-for="m in modules"
                :key="m.value"
                class="cb-pill"
                :class="{ 'cb-pill-active': moduleType === m.value }"
                @click="moduleType = m.value"
              >
                {{ m.label }}
              </button>
            </div>
          </div>

          <div class="cb-field-group">
            <label class="cb-label">FREQUENCY</label>
            <div class="cb-pills">
              <button
                v-for="f in frequencies"
                :key="f.value"
                class="cb-pill"
                :class="{ 'cb-pill-active': frequency === f.value }"
                @click="frequency = f.value"
              >
                {{ f.label }}
              </button>
            </div>
          </div>
        </div>

        <!-- Items list -->
        <div class="cb-items-section">
          <div class="cb-items-header">
            <span class="cb-label">
              ITEMS
              <span v-if="activeItems.length > 0" class="cb-items-count">{{
                activeItems.length
              }}</span>
            </span>
            <span v-if="activeItems.length > 1" class="cb-drag-hint">Drag to reorder</span>
          </div>

          <!-- Empty state -->
          <div v-if="activeItems.length === 0" class="cb-empty-state">
            <span>No items yet — add from the library</span>
            <span class="cb-empty-arrow">→</span>
          </div>

          <draggable v-model="activeItems" item-key="id" class="cb-items-list">
            <template #item="{ element }">
              <div class="cb-item-row">
                <span class="cb-drag-handle" title="Drag to reorder">⠿</span>
                <span class="cb-item-title">{{ element.title }}</span>
                <button class="cb-item-remove" title="Remove" @click="removeItem(element.id)">
                  ✕
                </button>
              </div>
            </template>
          </draggable>
        </div>
      </div>

      <!-- ── RIGHT: Library ── -->
      <div class="cb-library-card">
        <h2 class="cb-library-heading">Item Library</h2>

        <!-- Search -->
        <div class="cb-search-wrap">
          <input v-model="search" placeholder="Search items…" class="cb-search" />
        </div>

        <!-- Add actions -->
        <div class="cb-add-actions">
          <button class="cb-btn-presets" @click="showPresetPicker = true">
            Browse Compliance Presets
          </button>
          <button class="cb-btn-custom" @click="openNewItem">+ Custom Item</button>
        </div>

        <!-- Library items -->
        <div v-if="filteredItems.length === 0 && !search" class="cb-lib-empty">
          Your library is empty. Add presets or a custom item above.
        </div>
        <div v-else-if="filteredItems.length === 0" class="cb-lib-empty">
          No items match "{{ search }}".
        </div>

        <div class="cb-lib-list">
          <div v-for="item in filteredItems" :key="item.id" class="cb-lib-row">
            <span class="cb-lib-title">{{ item.title }}</span>
            <div class="cb-lib-controls">
              <span v-if="item.inUse" class="cb-inuse-tag">In use</span>
              <button
                class="cb-lib-icon"
                :disabled="item.inUse"
                title="Edit"
                @click="openEditItem(item)"
              >
                ✏
              </button>
              <button class="cb-lib-add" title="Add to template" @click="addItem(item)">+</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Preset picker -->
  <PresetItemPicker v-if="showPresetPicker" @close="showPresetPicker = false" @add="addPresets" />

  <!-- Custom item modal -->
  <transition name="cb-modal">
    <div v-if="showModal" class="cb-modal-overlay" @click.self="showModal = false">
      <div class="cb-modal">
        <div class="cb-modal-header">
          <h2>{{ editingItem ? 'Edit Item' : 'New Custom Item' }}</h2>
          <button class="cb-modal-close" @click="showModal = false">✕</button>
        </div>

        <div class="cb-modal-body">
          <div class="cb-form-group">
            <label>TITLE</label>
            <input v-model="newItem.title" placeholder="e.g., Check CO2 Levels" />
          </div>

          <div class="cb-form-group">
            <label>DESCRIPTION</label>
            <textarea v-model="newItem.description" placeholder="Describe what needs to be done…" />
          </div>

          <div class="cb-form-row">
            <div class="cb-form-group">
              <label>CATEGORY</label>
              <select v-model="newItem.category">
                <option value="IK_FOOD">IK-Food</option>
                <option value="IK_ALCOHOL">IK-Alcohol</option>
              </select>
            </div>
            <div class="cb-form-group">
              <label>PRIORITY</label>
              <select v-model="newItem.priority">
                <option value="LOW">Low</option>
                <option value="HIGH">High</option>
              </select>
            </div>
          </div>
        </div>

        <div class="cb-modal-footer">
          <button class="cb-btn-cancel" @click="showModal = false">Cancel</button>
          <button class="cb-btn-confirm" :disabled="!newItem.title.trim()" @click="saveItem">
            {{ editingItem ? 'Save Changes' : 'Add Item' }}
          </button>
        </div>
      </div>
    </div>
  </transition>
</template>

<style scoped>
/* ── Page ─────────────────────────────────────────────────────────────────── */

.cb-page {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.cb-intro {
  margin-bottom: 24px;
}

.cb-intro h1 {
  margin: 0 0 4px;
}

.cb-intro-sub {
  font-size: 14px;
  color: var(--text-secondary);
  margin: 0;
}

/* ── Grid ─────────────────────────────────────────────────────────────────── */

.cb-grid {
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 20px;
  align-items: start;
}

/* ── Builder card ─────────────────────────────────────────────────────────── */

.cb-builder-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  border: 1.5px solid var(--stroke);
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.cb-builder-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.cb-builder-title-wrap {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.cb-editing-badge {
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #2563eb;
  background: #eff6ff;
  padding: 2px 8px;
  border-radius: 999px;
  width: fit-content;
}

.cb-builder-heading {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  color: #0f172a;
}

.cb-builder-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.cb-btn-discard {
  background: transparent;
  border: none;
  font-size: 14px;
  color: #64748b;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
}

.cb-btn-discard:hover {
  background: #f1f5f9;
}

.cb-btn-save {
  background: #0f172a;
  color: white;
  border: none;
  padding: 9px 18px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.cb-btn-save:hover {
  background: #1e293b;
}

.cb-btn-save:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* ── Field groups ─────────────────────────────────────────────────────────── */

.cb-field-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cb-label {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.08em;
  color: #94a3b8;
  text-transform: uppercase;
}

.cb-name-input {
  width: 100%;
  height: 44px;
  padding: 0 12px;
  font-size: 16px;
  border: none;
  border-bottom: 2px solid #e2e8f0;
  background: transparent;
  outline: none;
  color: #0f172a;
  box-sizing: border-box;
}

.cb-name-input::placeholder {
  color: #cbd5e1;
}

.cb-name-input:focus {
  border-bottom-color: #0f172a;
}

/* ── Settings row ─────────────────────────────────────────────────────────── */

.cb-settings-row {
  display: flex;
  gap: 32px;
}

/* ── Pills ────────────────────────────────────────────────────────────────── */

.cb-pills {
  display: flex;
  gap: 6px;
}

.cb-pill {
  padding: 6px 14px;
  border-radius: 999px;
  border: 1.5px solid #e2e8f0;
  background: white;
  font-size: 13px;
  font-weight: 500;
  color: #64748b;
  cursor: pointer;
  transition: all 0.15s;
}

.cb-pill:hover {
  border-color: #94a3b8;
  color: #0f172a;
}

.cb-pill-active {
  background: #0f172a;
  color: white;
  border-color: #0f172a;
}

/* ── Items section ────────────────────────────────────────────────────────── */

.cb-items-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.cb-items-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cb-items-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  background: #0f172a;
  color: white;
  border-radius: 50%;
  font-size: 11px;
  font-weight: 700;
  margin-left: 6px;
}

.cb-drag-hint {
  font-size: 12px;
  color: #94a3b8;
}

.cb-empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 28px;
  border: 1.5px dashed #e2e8f0;
  border-radius: 14px;
  font-size: 13px;
  color: #94a3b8;
}

.cb-empty-arrow {
  font-size: 16px;
}

.cb-items-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cb-item-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
}

.cb-drag-handle {
  cursor: grab;
  color: #94a3b8;
  font-size: 16px;
  flex-shrink: 0;
  user-select: none;
}

.cb-item-title {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
  color: #0f172a;
}

.cb-item-remove {
  background: transparent;
  border: none;
  cursor: pointer;
  color: #94a3b8;
  font-size: 13px;
  padding: 4px 6px;
  border-radius: 6px;
  flex-shrink: 0;
}

.cb-item-remove:hover {
  background: #fee2e2;
  color: #dc2626;
}

/* ── Library card ─────────────────────────────────────────────────────────── */

.cb-library-card {
  background: white;
  border-radius: 20px;
  padding: 20px;
  border: 1.5px solid var(--stroke);
  display: flex;
  flex-direction: column;
  gap: 12px;
  position: sticky;
  top: 20px;
}

.cb-library-heading {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  color: #0f172a;
}

/* Search */
.cb-search-wrap {
  position: relative;
}

.cb-search {
  width: 100%;
  padding: 9px 12px;
  border: 1.5px solid #e2e8f0;
  border-radius: 10px;
  font-size: 14px;
  outline: none;
  background: #f8fafc;
  color: #0f172a;
  box-sizing: border-box;
}

.cb-search:focus {
  border-color: #94a3b8;
  background: white;
}

/* Add actions */
.cb-add-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.cb-btn-presets {
  width: 100%;
  padding: 11px;
  background: #0f172a;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
}

.cb-btn-presets:hover {
  background: #1e293b;
}

.cb-btn-custom {
  width: 100%;
  padding: 10px;
  background: transparent;
  color: #475569;
  border: 1.5px dashed #cbd5e1;
  border-radius: 10px;
  font-size: 13px;
  cursor: pointer;
}

.cb-btn-custom:hover {
  background: #f8fafc;
  border-color: #94a3b8;
  color: #0f172a;
}

/* Library list */
.cb-lib-empty {
  font-size: 13px;
  color: #94a3b8;
  text-align: center;
  padding: 16px 0;
}

.cb-lib-list {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-height: 400px;
  overflow-y: auto;
}

.cb-lib-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 10px;
  border-radius: 10px;
  background: #f8fafc;
}

.cb-lib-row:hover {
  background: #f1f5f9;
}

.cb-lib-title {
  flex: 1;
  font-size: 13px;
  font-weight: 500;
  color: #0f172a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.cb-lib-controls {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}

.cb-inuse-tag {
  font-size: 10px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 999px;
  background: #e5e7eb;
  color: #374151;
}

.cb-lib-icon {
  width: 26px;
  height: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  border-radius: 6px;
  font-size: 12px;
  color: #94a3b8;
  cursor: pointer;
}

.cb-lib-icon:hover:not(:disabled) {
  background: #e2e8f0;
  color: #475569;
}

.cb-lib-icon-danger:hover:not(:disabled) {
  background: #fee2e2;
  color: #dc2626;
}

.cb-lib-icon:disabled {
  opacity: 0.3;
  cursor: default;
}

.cb-lib-add {
  width: 26px;
  height: 26px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #0f172a;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  flex-shrink: 0;
}

.cb-lib-add:hover {
  background: #1e293b;
}

/* ── Modal ────────────────────────────────────────────────────────────────── */

.cb-modal-enter-active,
.cb-modal-leave-active {
  transition: all 0.18s ease;
}

.cb-modal-enter-from,
.cb-modal-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

.cb-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.cb-modal {
  width: 520px;
  max-width: 95vw;
  background: white;
  border-radius: 18px;
  padding: 28px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.cb-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 22px;
}

.cb-modal-header h2 {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
  color: #0f172a;
}

.cb-modal-close {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  color: #94a3b8;
  padding: 6px;
  border-radius: 6px;
}

.cb-modal-close:hover {
  background: #f1f5f9;
  color: #475569;
}

.cb-modal-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.cb-form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.cb-form-group label {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.06em;
  color: #94a3b8;
  text-transform: uppercase;
}

.cb-form-group input,
.cb-form-group select {
  height: 42px;
  padding: 0 12px;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  background: #f8fafc;
  font-size: 14px;
  color: #0f172a;
  outline: none;
}

.cb-form-group input:focus,
.cb-form-group select:focus {
  border-color: #94a3b8;
  background: white;
}

.cb-form-group textarea {
  padding: 10px 12px;
  border: 1.5px solid #e2e8f0;
  border-radius: 8px;
  background: #f8fafc;
  font-size: 14px;
  color: #0f172a;
  outline: none;
  resize: none;
  min-height: 80px;
  font-family: inherit;
}

.cb-form-group textarea:focus {
  border-color: #94a3b8;
  background: white;
}

.cb-form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.cb-modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
}

.cb-btn-cancel {
  background: none;
  border: none;
  font-size: 14px;
  color: #64748b;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 8px;
}

.cb-btn-cancel:hover {
  background: #f1f5f9;
}

.cb-btn-confirm {
  background: #0f172a;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.cb-btn-confirm:hover {
  background: #1e293b;
}

.cb-btn-confirm:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
</style>
