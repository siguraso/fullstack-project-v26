<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import TemplateLibrary from './components/TemplateLibrary.vue'
import {
  createLibraryItem,
  createTemplate,
  deleteLibraryItem,
  getLibraryItems,
  getTemplates,
  isLibraryItemInUse,
  updateLibraryItem,
  updateTemplate,
} from '@/services/checklist'
import draggable from 'vuedraggable'
import { useTenant } from '@/services/useTenant'

interface Item {
  id: number
  title: string
  description: string
  inUse: boolean
}

const newItem = ref({
  title: '',
  description: '',
  priority: 'LOW',
  dueDate: '',
})

const checklistName = ref('')

const tenantId = useTenant().tenantId

const templates = ref<any[]>([])

const selectedTemplate = ref<any | null>(null)

const showModal = ref(false)

const search = ref('')

const frequency = ref<'DAILY' | 'WEEKLY' | 'MONTHLY'>('DAILY')

const editingItem = ref<Item | null>(null)

const selectedCategory = ref('ALL')

const filteredItems = computed(() => {
  return libraryItems.value.filter((item) =>
    item.title.toLowerCase().includes(search.value.toLowerCase()),
  )
})

async function reloadTemplates() {
  const data = await getTemplates(tenantId)
  console.log('TEMPLATES:', data)
  templates.value = data
}

onMounted(async () => {
  const templatesData = await getTemplates(tenantId)
  templates.value = templatesData

  const libraryData = await getLibraryItems(tenantId)
  libraryItems.value = libraryData
})

const activeItems = ref<Item[]>([])

const libraryItems = ref<Item[]>([])

function addItem(item: Item) {
  activeItems.value.push(item)
}

function removeItem(id: number) {
  activeItems.value = activeItems.value.filter((i) => i.id !== id)
}

function editItem(item: Item) {
  editingItem.value = item

  newItem.value = {
    title: item.title,
    description: item.description,
    priority: 'LOW',
    dueDate: '',
  }

  showModal.value = true
}

function loadTemplate(template: any) {
  selectedTemplate.value = template

  checklistName.value = template.name
  frequency.value = template.frequency

  activeItems.value = template.items.map((item: any) => ({
    id: item.id,
    title: item.title,
    description: item.description,
  }))
}

async function saveChecklist() {
  const payload = {
    tenantId: tenantId,
    name: checklistName.value,
    module: 'IK_FOOD',
    frequency: frequency.value,
    itemIds: activeItems.value.map((i) => i.id),
  }

  if (selectedTemplate.value) {
    await updateTemplate(selectedTemplate.value.id, payload)
  } else {
    await createTemplate(payload)
  }

  await reloadTemplates()

  selectedTemplate.value = null
  checklistName.value = ''
  activeItems.value = []

  // scroll ned til builder
  document.getElementById('builder')?.scrollIntoView({ behavior: 'smooth' })
}

async function loadLibrary() {
  const data = await getLibraryItems(tenantId)

  const enriched = await Promise.all(
    data.map(async (item: any) => ({
      ...item,
      inUse: await isLibraryItemInUse(item.id),
    })),
  )

  libraryItems.value = enriched
}

async function createCustomItem() {
  const payload = {
    title: newItem.value.title,
    description: newItem.value.description,
    category: 'IK_FOOD',
    priority: newItem.value.priority,
  }

  if (editingItem.value) {
    const updated = await updateLibraryItem(editingItem.value.id, payload)

    const index = libraryItems.value.findIndex((i) => i.id === updated.id)
    if (index !== -1) {
      libraryItems.value[index] = updated
    }

    editingItem.value = null
  } else {
    const saved = await createLibraryItem(tenantId, payload)

    libraryItems.value.push(saved)
    activeItems.value.push(saved)

    loadLibrary()
  }

  showModal.value = false
}
</script>

<template>
  <div>
    <ViewHeader title="Checklist Management" :options="[]" :routes="[]" />

    <!-- TOP -->
    <section class="top">
      <div>
        <h1>Checklist Management & Templates</h1>
        <p>
          Standardize operations and maintain regulatory compliance by managing your core
          operational templates.
        </p>
      </div>

      <button class="primary-btn">+ Create New Checklist</button>
    </section>

    <TemplateLibrary :templates="templates" @edit="loadTemplate" @deleted="reloadTemplates" />

    <!-- MAIN GRID -->
    <div class="grid">
      <!-- LEFT -->
      <div id="builder" class="builder-card">
        <div class="builder-header">
          <h2>Checklist Builder</h2>
          <div class="actions">
            <button class="discard">Discard</button>
            <button class="primary" @click="saveChecklist">Save & Publish</button>
          </div>
        </div>

        <!-- TITLE -->
        <div class="section">
          <div class="section">
            <label>FREQUENCY</label>

            <select v-model="frequency" class="select">
              <option value="DAILY">Daily</option>
              <option value="WEEKLY">Weekly</option>
              <option value="MONTHLY">Monthly</option>
            </select>
          </div>
          <label>CHECKLIST TITLE</label>
          <input
            v-model="checklistName"
            placeholder="Enter checklist name..."
            class="title-input"
          />
        </div>

        <!-- ACTIVE ITEMS -->
        <div class="section">
          <div class="section-header">
            <span>ACTIVE ITEMS ({{ activeItems.length }})</span>
            <span class="hint">Drag to reorder</span>
          </div>

          <draggable v-model="activeItems" item-key="id" class="items">
            <template #item="{ element }">
              <div class="item-row">
                <div class="drag">⋮⋮</div>

                <div class="item-content">
                  <h3>{{ element.title }}</h3>
                  <p>{{ element.description }}</p>
                </div>

                <button class="remove" @click="removeItem(element.id)">✕</button>
              </div>
            </template>
          </draggable>
        </div>
      </div>

      <!-- RIGHT -->
      <div class="library-card">
        <h2>Item Library</h2>
        <button class="add-custom" @click="showModal = true">+ Add Custom Item</button>
        <p class="subtext">Quickly add pre-approved compliance items to your checklist.</p>

        <input v-model="search" placeholder="Search standard items..." class="search" />

        <div class="library-list">
          <div v-for="item in filteredItems" :key="item.id" class="library-row">
            <div class="library-content">
              <h3>{{ item.title }}</h3>
              <p>{{ item.description }}</p>
            </div>

            <div class="add-remove">
              <span v-if="item.inUse" class="tag">In use</span>
              <button class="edit-item" :disabled="item.inUse" @click="editItem(item)">✏️</button>
              <button class="remove-item" @click="deleteLibraryItem(item.id)">x</button>
              <button class="add" @click="addItem(item)">+</button>
            </div>
          </div>
        </div>

        <!-- PRO TIP -->
        <div class="pro-tip">
          <strong>Pro Tip</strong>
          <p>
            Standard items are automatically mapped to regulatory reporting. Using them ensures
            faster audit approvals.
          </p>
        </div>
      </div>
    </div>
  </div>

  <transition name="modal">
    <div v-if="showModal" class="modal-overlay">
      <div class="modal">
        <!-- HEADER -->
        <div class="modal-header">
          <h2>
            {{ editingItem ? 'Edit Checklist Item' : 'Add New Checklist Item' }}
          </h2>
          <button class="close-btn" @click="showModal = false">✕</button>
        </div>

        <!-- BODY -->
        <div class="modal-body">
          <div class="form-group">
            <label>ITEM TITLE</label>
            <input v-model="newItem.title" placeholder="e.g., Check CO2 Levels" />
          </div>

          <div class="form-group">
            <label>INSTRUCTIONS / DESCRIPTION</label>
            <textarea
              v-model="newItem.description"
              placeholder="e.g., Ensure the gauge is within the green zone..."
            />
          </div>

          <div class="form-group">
            <label>COMPLIANCE CATEGORY</label>
            <select>
              <option>IK-FOOD</option>
              <option>IK-ALCOHOL</option>
            </select>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>PRIORITY</label>
              <select v-model="newItem.priority">
                <option>Low</option>
                <option>High</option>
              </select>
            </div>

            <div class="form-group">
              <label>DUE DATE / TIME</label>
              <input type="datetime-local" v-model="newItem.dueDate" />
            </div>
          </div>
        </div>

        <!-- FOOTER -->
        <div class="modal-footer">
          <button class="cancel" @click="showModal = false">Cancel</button>
          <button class="primary" @click="createCustomItem">Add to Checklist</button>
        </div>
      </div>
    </div>
  </transition>
</template>

<style>
.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.primary-btn {
  background: var(--neutral);
  color: white;
  padding: 12px 20px;
  border-radius: 12px;
  border: none;
}

.grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

/* LEFT CARD */
.builder-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  border: 1px solid var(--stroke);
}

.builder-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.actions {
  display: flex;
  gap: 12px;
}

.discard {
  background: transparent;
  border: none;
  color: var(--text-secondary);
}

.discard:hover {
  background-color: #f3f4f6;
}

.primary {
  background: var(--neutral);
  color: white;
  border: none;
  padding: 10px 18px;
  border-radius: 10px;
}

.primary-btn {
  background: #0f172a;
  color: white;
  border: none;
  padding: 14px;
  border-radius: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: 0.2s;
}

/* SECTIONS */
.section {
  margin-bottom: 24px;
}

.section label {
  font-size: 12px;
  color: var(--text-secondary);
  letter-spacing: 1px;
}

.title-input {
  height: 44px;
  width: 100%;
  padding: 12px 0;
  border: none;
  border-radius: 0;
  border-bottom: 2px solid var(--stroke);
  font-size: 18px;
}

.title-input:focus {
  outline: none;
}

/* ACTIVE ITEMS */
.section-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 13px;
}

.hint {
  color: var(--text-secondary);
}

.items {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* ITEM */
.item-row {
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f3f4f6;
  padding: 14px;
  border-radius: 14px;
}

.drag {
  cursor: grab;
  opacity: 0.5;
}

.item-content {
  flex: 1;
}

.item-content h3 {
  margin: 0;
  font-size: 15px;
}

.item-content p {
  margin: 4px 0 0;
  font-size: 13px;
  color: var(--text-secondary);
}

.remove {
  background: transparent;
  border: none;
  cursor: pointer;
  color: black;
  padding-left: 15px;
  padding-right: 15px;
  background-color: #f3f4f6;
}

.remove:hover {
  background-color: #f3f4f6;
}

/* ADD CUSTOM */
.add-custom {
  color: black;
  margin-top: 12px;
  width: 100%;
  padding: 14px;
  border-radius: 12px;
  border: 2px dashed var(--stroke);
  background: transparent;
  cursor: pointer;
}

.add-custom:hover {
  background-color: #f3f4f6;
}

/* RIGHT CARD */
.library-card {
  background: white;
  border-radius: 20px;
  padding: 20px;
  border: 1px solid var(--stroke);
}

.subtext {
  font-size: 13px;
  color: var(--text-secondary);
}

.select:focus {
  outline: none;
}

.search {
  width: 100%;
  padding: 10px;
  border-radius: 10px;
  margin: 12px 0;
  border-bottom: 2px solid var(--stroke);
}

.search:focus {
  outline: none;
}
/* LIBRARY ITEMS */
.library-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #f3f4f6;
  padding: 14px;
  border-radius: 14px;
  margin-bottom: 10px;
}

.library-content h3 {
  margin: 0;
  font-size: 14px;
}

.library-content p {
  margin: 4px 0 0;
  font-size: 12px;
  color: var(--text-secondary);
}

.remove-item {
  color: black;
  padding: 10px;
  background: transparent;
  border: none;
  font-size: 18px;
  cursor: pointer;
}

.remove-item:hover {
  background-color: #e2e8f0;
}

.add {
  color: black;
  padding: 10px;
  background: transparent;
  border: none;
  font-size: 18px;
  cursor: pointer;
}

.add:hover {
  background-color: #e2e8f0;
}

/* PRO TIP */
.pro-tip {
  margin-top: 16px;
  padding: 16px;
  border-radius: 14px;
  background: #dbeafe;
  font-size: 13px;
}

/* OVERLAY / MODAL */
/* ANIMATION */
/* ENTER */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.2s ease;
}

/* start state */
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
  transform: translateY(10px);
}

/* end state */
.modal-enter-to,
.modal-leave-from {
  opacity: 1;
  transform: translateY(0);
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  width: 640px;
  background: #ffffff;
  border-radius: 16px;
  padding: 28px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
  font-family: system-ui, sans-serif;
}

/* HEADER */
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.modal-header h2 {
  font-size: 22px;
  font-weight: 600;
  color: #0f172a;
}

.close-btn {
  padding: 15px;
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  color: #64748b;
}

.close-btn:hover {
  background-color: #f3f4f6;
}

/* BODY */
.modal-body {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-group label {
  display: block;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.06em;
  color: #64748b;
  margin-bottom: 6px;
}

/* INPUTS */
.form-group input,
.form-group select {
  height: 44px;
  padding: 0 14px;
  border: none;
  border-bottom: 2px solid #e2e8f0;
  background: #f8fafc;
  border-radius: 6px;
  font-size: 14px;
}

.form-group textarea {
  width: 100%;
  padding: 12px 14px;
  border: none;
  border-bottom: 2px solid #e2e8f0;
  font-size: 14px;
  outline: none;
  background: #f8fafc;
  border-radius: 6px;
}

.form-group textarea {
  min-height: 90px;
  resize: none;
}

/* ROW */
.form-row {
  display: flex;
  gap: 16px;
}

.form-row .form-group {
  flex: 1;
}

/* FOOTER */
.modal-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 14px;
  margin-top: 26px;
}

/* BUTTONS */
.cancel {
  background: none;
  border: none;
  font-size: 14px;
  color: #475569;
  cursor: pointer;
}

.primary {
  background: #0f172a;
  color: white;
  padding: 12px 18px;
  border-radius: 10px;
  border: none;
  font-weight: 600;
  cursor: pointer;
  transition: 0.2s;
}

.primary:hover {
  background: #1e293b;
}
</style>
