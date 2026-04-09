<script setup lang="ts">
import type {
  DocumentArea,
  DocumentDetail,
  DocumentUpsertPayload,
} from '@/interfaces/Document.interface'
import { reactive, ref, watch } from 'vue'
import {
  createDocumentFormValues,
  createEmptyDocumentForm,
  normalizeDocumentTag,
} from '../documents.helpers'

const props = defineProps<{
  isOpen: boolean
  isEditing: boolean
  isLoadingDetail: boolean
  isSaving: boolean
  errorMessage: string
  defaultArea: DocumentArea | null
  document: DocumentDetail | null
}>()

const emit = defineEmits<{
  (event: 'close'): void
  (event: 'submit', payload: DocumentUpsertPayload): void
}>()

const form = reactive(createEmptyDocumentForm())
const formTagDraft = ref('')

function applyFormState() {
  const values = props.isEditing
    ? createDocumentFormValues(props.document, props.defaultArea)
    : createEmptyDocumentForm(props.defaultArea)

  form.title = values.title
  form.description = values.description
  form.area = values.area
  form.tags = [...values.tags]
  form.file = values.file
  formTagDraft.value = ''
}

function addFormTag(tag: string) {
  const normalized = normalizeDocumentTag(tag)

  if (!normalized || form.tags.includes(normalized)) {
    formTagDraft.value = ''
    return
  }

  form.tags = [...form.tags, normalized]
  formTagDraft.value = ''
}

function removeFormTag(tag: string) {
  form.tags = form.tags.filter((entry) => entry !== tag)
}

function handleFormTagKeydown(event: KeyboardEvent) {
  if (event.key === 'Enter' || event.key === ',') {
    event.preventDefault()
    addFormTag(formTagDraft.value)
  }
}

function onFileSelected(event: Event) {
  const input = event.target as HTMLInputElement
  form.file = input.files?.[0] ?? null
}

function submitForm() {
  const payload: DocumentUpsertPayload = {
    title: form.title.trim(),
    description: form.description.trim(),
    area: form.area,
    tags: form.tags,
    file: form.file,
  }

  if (!payload.title) {
    return
  }

  if (!props.isEditing && !payload.file) {
    return
  }

  emit('submit', payload)
}

watch(
  [() => props.isOpen, () => props.isEditing, () => props.document, () => props.defaultArea],
  ([isOpen]) => {
    if (!isOpen) {
      return
    }

    applyFormState()
  },
  { immediate: true },
)
</script>

<template>
  <Teleport to="body">
    <div v-if="isOpen" class="overlay-backdrop" @click.self="emit('close')">
      <section class="document-form-card">
        <header class="document-form-header">
          <div>
            <h2>{{ isEditing ? 'Edit document' : 'Upload document' }}</h2>
            <p>
              {{
                isEditing ? 'Replace the file only if needed.' : 'Add a new file to the library.'
              }}
            </p>
          </div>
          <button type="button" class="close-button" @click="emit('close')">Close</button>
        </header>

        <p v-if="errorMessage" class="status error">{{ errorMessage }}</p>
        <p v-if="isLoadingDetail" class="status">Loading document details...</p>

        <form v-else class="document-form" @submit.prevent="submitForm">
          <label class="field">
            <span>Title</span>
            <input v-model="form.title" type="text" maxlength="255" required />
          </label>

          <label class="field">
            <span>Description</span>
            <textarea v-model="form.description" rows="4" maxlength="2000"></textarea>
          </label>

          <label class="field">
            <span>Bucket</span>
            <select v-model="form.area">
              <option value="GENERAL">General</option>
              <option value="IK_FOOD">IK-food</option>
              <option value="IK_ALCOHOL">IK-Alcohol</option>
            </select>
          </label>

          <label class="field">
            <span>{{ isEditing ? 'Replace file (optional)' : 'File' }}</span>
            <input type="file" @change="onFileSelected" />
          </label>

          <label class="field">
            <span>Tags</span>
            <input
              v-model="formTagDraft"
              type="text"
              placeholder="Press Enter to add a tag"
              @keydown="handleFormTagKeydown"
            />
          </label>

          <div v-if="form.tags.length > 0" class="tag-row">
            <button
              v-for="tag in form.tags"
              :key="tag"
              type="button"
              class="tag-chip"
              @click="removeFormTag(tag)"
            >
              {{ tag }} ×
            </button>
          </div>

          <p v-if="!form.title.trim()" class="status error">Title is required.</p>
          <p v-else-if="!isEditing && !form.file" class="status error">
            Select a file before uploading.
          </p>

          <div class="form-actions">
            <button type="button" class="secondary-action" @click="emit('close')">Cancel</button>
            <button
              type="submit"
              class="primary-action"
              :disabled="isSaving || !form.title.trim() || (!isEditing && !form.file)"
            >
              {{ isSaving ? 'Saving…' : isEditing ? 'Save changes' : 'Upload document' }}
            </button>
          </div>
        </form>
      </section>
    </div>
  </Teleport>
</template>

<style scoped>
.overlay-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.42);
  backdrop-filter: blur(4px);
  display: grid;
  place-items: center;
  z-index: 1200;
  padding: 24px;
}

.document-form-card {
  width: min(100%, 680px);
  padding: 24px;
  border: 1px solid rgba(148, 163, 184, 0.28);
  border-radius: 20px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.98)),
    radial-gradient(circle at top right, rgba(254, 240, 138, 0.22), transparent 34%);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.08);
}

.document-form-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 16px;
}

.document-form-header h2 {
  margin: 0;
}

.document-form-header p {
  margin: 8px 0 0;
  color: #64748b;
}

.close-button,
.secondary-action,
.primary-action,
.tag-chip {
  border-radius: 999px;
  border: 1px solid transparent;
  cursor: pointer;
  transition:
    transform 140ms ease,
    background-color 140ms ease,
    border-color 140ms ease;
}

.close-button {
  padding: 9px 14px;
  background: #fff;
  border-color: #cbd5e1;
}

.document-form {
  display: grid;
  gap: 16px;
}

.field {
  display: grid;
  gap: 8px;
  color: #0f172a;
  font-weight: 600;
}

.field input,
.field textarea,
.field select {
  width: 100%;
  border: 1px solid #cbd5e1;
  border-radius: 14px;
  padding: 12px 14px;
  font: inherit;
  background: #fff;
}

.tag-row,
.form-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.tag-chip {
  padding: 8px 12px;
  background: #dbeafe;
  color: #1d4ed8;
}

.status {
  margin: 0;
  padding: 12px 14px;
  border-radius: 14px;
  background: #f8fafc;
  color: #334155;
}

.status.error {
  background: #fff1f2;
  color: #be123c;
}

.form-actions {
  justify-content: flex-end;
  margin-top: 8px;
}

.primary-action {
  padding: 12px 16px;
  background: linear-gradient(135deg, #0f172a, #1e293b);
  color: #fff;
}

.secondary-action {
  padding: 9px 14px;
  background: #fff;
  border-color: #cbd5e1;
  color: #0f172a;
}

.primary-action:hover,
.secondary-action:hover,
.close-button:hover,
.tag-chip:hover {
  transform: translateY(-1px);
}

.primary-action:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 640px) {
  .overlay-backdrop {
    padding: 12px;
    align-items: stretch;
  }

  .document-form-card {
    width: 100%;
    max-height: calc(100vh - 24px);
    overflow: auto;
    padding: 14px;
    border-radius: 16px;
  }

  .document-form-header {
    flex-direction: column;
  }

  .tag-row,
  .form-actions {
    gap: 8px;
  }

  .close-button,
  .secondary-action,
  .primary-action,
  .tag-chip {
    width: 100%;
    justify-content: center;
  }
}
</style>
