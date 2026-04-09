<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import type {
  DocumentArea,
  DocumentSummary,
  DocumentUpsertPayload,
} from '@/interfaces/Document.interface'
import { getAuthSession } from '@/services/auth'
import {
  createDocument,
  deleteDocument,
  downloadDocumentFile,
  fetchDocuments,
  fetchDocumentFile,
  getDocumentById,
  updateDocument,
} from '@/services/document'

type AreaOption = {
  label: string
  value: DocumentArea | null
}

type FormState = {
  title: string
  description: string
  area: DocumentArea
  tags: string[]
  file: File | null
}

const areaOptions: AreaOption[] = [
  { label: 'All', value: null },
  { label: 'General', value: 'GENERAL' },
  { label: 'IK-food', value: 'IK_FOOD' },
  { label: 'IK-Alcohol', value: 'IK_ALCOHOL' },
]

const documents = ref<DocumentSummary[]>([])
const isLoading = ref(false)
const loadError = ref('')
const successMessage = ref('')
const searchQuery = ref('')
const selectedArea = ref<DocumentArea | null>(null)
const selectedTags = ref<string[]>([])
const filterTagDraft = ref('')
const formTagDraft = ref('')
const isFormOpen = ref(false)
const isEditing = ref(false)
const editingDocumentId = ref<number | null>(null)
const isSaving = ref(false)
const isDeletingId = ref<number | null>(null)
const isLoadingDetail = ref(false)
const formError = ref('')
const activeDownloadId = ref<number | null>(null)
const previewDocument = ref<DocumentSummary | null>(null)
const isPreviewOpen = ref(false)
const isPreviewLoading = ref(false)
const previewError = ref('')
const previewUrl = ref('')
const previewTextContent = ref('')
let searchDebounceHandle: number | null = null

const form = reactive<FormState>({
  title: '',
  description: '',
  area: 'GENERAL',
  tags: [],
  file: null,
})

const canManage = computed(() => {
  const role = getAuthSession()?.role
  return role === 'ADMIN' || role === 'MANAGER'
})

const availableTags = computed(() =>
  Array.from(new Set(documents.value.flatMap((document) => document.tags))).sort((left, right) =>
    left.localeCompare(right),
  ),
)
const previewMimeType = computed(() => previewDocument.value?.mimeType ?? '')
const previewIsPdf = computed(() => previewMimeType.value === 'application/pdf')
const previewIsImage = computed(() => previewMimeType.value.startsWith('image/'))
const previewIsText = computed(
  () =>
    previewMimeType.value.startsWith('text/') ||
    previewDocument.value?.originalFilename.endsWith('.txt'),
)
const canPreviewCurrentDocument = computed(
  () => previewIsPdf.value || previewIsImage.value || previewIsText.value,
)

function resetForm() {
  form.title = ''
  form.description = ''
  form.area = selectedArea.value ?? 'GENERAL'
  form.tags = []
  form.file = null
  formError.value = ''
  formTagDraft.value = ''
}

function revokePreviewUrl() {
  if (!previewUrl.value) {
    return
  }

  window.URL.revokeObjectURL(previewUrl.value)
  previewUrl.value = ''
}

function displayArea(area: DocumentArea) {
  if (area === 'IK_FOOD') {
    return 'IK-food'
  }

  if (area === 'IK_ALCOHOL') {
    return 'IK-Alcohol'
  }

  return 'General'
}

function formatDate(value: string | null) {
  if (!value) {
    return 'Unknown'
  }

  return new Intl.DateTimeFormat('en-GB', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
}

function formatBytes(sizeBytes: number) {
  if (sizeBytes < 1024) {
    return `${sizeBytes} B`
  }

  if (sizeBytes < 1024 * 1024) {
    return `${(sizeBytes / 1024).toFixed(1)} KB`
  }

  return `${(sizeBytes / (1024 * 1024)).toFixed(1)} MB`
}

function buildFilters() {
  return {
    area: selectedArea.value,
    query: searchQuery.value.trim(),
    tags: selectedTags.value,
  }
}

async function loadDocuments() {
  isLoading.value = true
  loadError.value = ''

  try {
    documents.value = await fetchDocuments(buildFilters())
  } catch (error) {
    loadError.value = error instanceof Error ? error.message : 'Failed to load documents.'
  } finally {
    isLoading.value = false
  }
}

function scheduleReload() {
  if (searchDebounceHandle !== null) {
    window.clearTimeout(searchDebounceHandle)
  }

  searchDebounceHandle = window.setTimeout(() => {
    void loadDocuments()
  }, 220)
}

function addFilterTag(tag: string) {
  const normalized = tag.trim().toLowerCase()
  if (!normalized || selectedTags.value.includes(normalized)) {
    filterTagDraft.value = ''
    return
  }

  selectedTags.value = [...selectedTags.value, normalized]
  filterTagDraft.value = ''
}

function removeFilterTag(tag: string) {
  selectedTags.value = selectedTags.value.filter((entry) => entry !== tag)
}

function addFormTag(tag: string) {
  const normalized = tag.trim().toLowerCase()
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

function openCreateForm() {
  isEditing.value = false
  editingDocumentId.value = null
  resetForm()
  isFormOpen.value = true
}

async function openEditForm(documentId: number) {
  isEditing.value = true
  editingDocumentId.value = documentId
  isFormOpen.value = true
  formError.value = ''
  isLoadingDetail.value = true

  try {
    const document = await getDocumentById(documentId)
    form.title = document.title
    form.description = document.description ?? ''
    form.area = document.area
    form.tags = [...document.tags]
    form.file = null
    formTagDraft.value = ''
  } catch (error) {
    formError.value = error instanceof Error ? error.message : 'Failed to load document.'
  } finally {
    isLoadingDetail.value = false
  }
}

function closeForm() {
  isFormOpen.value = false
  resetForm()
}

function buildPayload(): DocumentUpsertPayload {
  return {
    title: form.title.trim(),
    description: form.description.trim(),
    area: form.area,
    tags: form.tags,
    file: form.file,
  }
}

async function saveForm() {
  if (!canManage.value) {
    return
  }

  successMessage.value = ''
  formError.value = ''

  const payload = buildPayload()
  if (!payload.title) {
    formError.value = 'Title is required.'
    return
  }

  if (!isEditing.value && !payload.file) {
    formError.value = 'Select a file before uploading.'
    return
  }

  isSaving.value = true

  try {
    if (isEditing.value && editingDocumentId.value !== null) {
      await updateDocument(editingDocumentId.value, payload)
      successMessage.value = 'Document updated.'
    } else {
      await createDocument(payload)
      successMessage.value = 'Document uploaded.'
    }

    closeForm()
    await loadDocuments()
  } catch (error) {
    formError.value = error instanceof Error ? error.message : 'Failed to save document.'
  } finally {
    isSaving.value = false
  }
}

async function deleteSelectedDocument(document: DocumentSummary) {
  if (!canManage.value) {
    return
  }

  const confirmed = window.confirm(`Delete "${document.title}"?`)
  if (!confirmed) {
    return
  }

  successMessage.value = ''
  loadError.value = ''
  isDeletingId.value = document.id

  try {
    await deleteDocument(document.id)
    successMessage.value = 'Document deleted.'
    documents.value = documents.value.filter((entry) => entry.id !== document.id)
  } catch (error) {
    loadError.value = error instanceof Error ? error.message : 'Failed to delete document.'
  } finally {
    isDeletingId.value = null
  }
}

async function downloadDocument(document: DocumentSummary) {
  activeDownloadId.value = document.id
  loadError.value = ''

  try {
    await downloadDocumentFile(document)
  } catch (error) {
    loadError.value = error instanceof Error ? error.message : 'Failed to download document.'
  } finally {
    activeDownloadId.value = null
  }
}

function canPreviewDocument(document: Pick<DocumentSummary, 'mimeType' | 'originalFilename'>) {
  const mimeType = document.mimeType.toLowerCase()
  const filename = document.originalFilename.toLowerCase()

  return (
    mimeType === 'application/pdf' ||
    mimeType.startsWith('image/') ||
    mimeType.startsWith('text/') ||
    filename.endsWith('.txt')
  )
}

async function openPreview(document: DocumentSummary) {
  previewDocument.value = document
  previewError.value = ''
  previewTextContent.value = ''
  revokePreviewUrl()
  isPreviewOpen.value = true

  if (!canPreviewDocument(document)) {
    previewError.value =
      'This file type cannot be previewed in the browser yet. Download it to inspect the contents.'
    return
  }

  isPreviewLoading.value = true

  try {
    const file = await fetchDocumentFile(document)

    if (
      file.mimeType.startsWith('text/') ||
      document.originalFilename.toLowerCase().endsWith('.txt')
    ) {
      previewTextContent.value = await file.blob.text()
    }

    previewUrl.value = window.URL.createObjectURL(file.blob)
  } catch (error) {
    previewError.value = error instanceof Error ? error.message : 'Failed to load preview.'
  } finally {
    isPreviewLoading.value = false
  }
}

function closePreview() {
  isPreviewOpen.value = false
  previewDocument.value = null
  previewError.value = ''
  previewTextContent.value = ''
  revokePreviewUrl()
}

function handleFilterTagKeydown(event: KeyboardEvent) {
  if (event.key === 'Enter' || event.key === ',') {
    event.preventDefault()
    addFilterTag(filterTagDraft.value)
  }
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

watch([selectedArea, searchQuery, selectedTags], () => {
  scheduleReload()
})

onBeforeUnmount(() => {
  if (searchDebounceHandle !== null) {
    window.clearTimeout(searchDebounceHandle)
  }

  revokePreviewUrl()
})

onMounted(() => {
  void loadDocuments()
})
</script>

<template>
  <section class="documents-view">
    <header class="page-header">
      <div>
        <h1>Document Library</h1>
      </div>
      <button v-if="canManage" type="button" class="primary-action" @click="openCreateForm">
        Upload document
      </button>
    </header>

    <section class="toolbar-card">
      <div class="area-toggle" aria-label="Document area filter">
        <button
          v-for="option in areaOptions"
          :key="option.label"
          type="button"
          class="area-button"
          :class="{ 'area-button-active': selectedArea === option.value }"
          @click="selectedArea = option.value"
        >
          {{ option.label }}
        </button>
      </div>

      <div class="toolbar-grid">
        <label class="field">
          <span>Keyword search</span>
          <input
            v-model="searchQuery"
            type="search"
            placeholder="Title, filename, description, or tag"
          />
        </label>

        <label class="field">
          <span>Filter by tag</span>
          <input
            v-model="filterTagDraft"
            type="text"
            placeholder="Press Enter to add a tag"
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
          @click="removeFilterTag(tag)"
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
          @click="addFilterTag(tag)"
        >
          {{ tag }}
        </button>
      </div>
    </section>

    <p v-if="successMessage" class="status success">{{ successMessage }}</p>
    <p v-if="loadError" class="status error">{{ loadError }}</p>
    <p v-if="isLoading" class="status">Loading documents...</p>

    <section v-else class="results-card">
      <div class="results-header">
        <strong>{{ documents.length }} documents</strong>
      </div>

      <div v-if="documents.length === 0" class="empty-state">
        <h2>No documents found</h2>
        <p>Try a different keyword, area, or tag filter.</p>
      </div>

      <div v-else class="table-shell">
        <table class="documents-table">
          <thead>
            <tr>
              <th>Document</th>
              <th>Bucket</th>
              <th>Tags</th>
              <th>Updated</th>
              <th>File</th>
              <th>Uploaded by</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="document in documents" :key="document.id">
              <td>
                <div class="document-title">{{ document.title }}</div>
                <p v-if="document.description" class="document-description">
                  {{ document.description }}
                </p>
              </td>
              <td>{{ displayArea(document.area) }}</td>
              <td>
                <div class="table-tags">
                  <span v-for="tag in document.tags" :key="tag" class="table-tag">{{ tag }}</span>
                  <span v-if="document.tags.length === 0" class="muted">No tags</span>
                </div>
              </td>
              <td>{{ formatDate(document.updatedAt ?? document.createdAt) }}</td>
              <td>
                <div>{{ document.originalFilename }}</div>
                <div class="muted">{{ formatBytes(document.sizeBytes) }}</div>
              </td>
              <td>{{ document.uploadedByName ?? 'Unknown' }}</td>
              <td>
                <div class="actions">
                  <button
                    type="button"
                    class="secondary-action"
                    :disabled="isPreviewLoading && previewDocument?.id === document.id"
                    @click="openPreview(document)"
                  >
                    View
                  </button>
                  <button
                    type="button"
                    class="secondary-action"
                    :disabled="activeDownloadId === document.id"
                    @click="downloadDocument(document)"
                  >
                    {{ activeDownloadId === document.id ? 'Downloading…' : 'Download' }}
                  </button>
                  <button
                    v-if="canManage"
                    type="button"
                    class="secondary-action"
                    @click="openEditForm(document.id)"
                  >
                    Edit
                  </button>
                  <button
                    v-if="canManage"
                    type="button"
                    class="danger-action"
                    :disabled="isDeletingId === document.id"
                    @click="deleteSelectedDocument(document)"
                  >
                    {{ isDeletingId === document.id ? 'Deleting…' : 'Delete' }}
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <div v-if="isFormOpen" class="overlay-backdrop" @click.self="closeForm">
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
          <button type="button" class="close-button" @click="closeForm">Close</button>
        </header>

        <p v-if="formError" class="status error">{{ formError }}</p>
        <p v-if="isLoadingDetail" class="status">Loading document details...</p>

        <form v-else class="document-form" @submit.prevent="saveForm">
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

          <div class="form-actions">
            <button type="button" class="secondary-action" @click="closeForm">Cancel</button>
            <button type="submit" class="primary-action" :disabled="isSaving">
              {{ isSaving ? 'Saving…' : isEditing ? 'Save changes' : 'Upload document' }}
            </button>
          </div>
        </form>
      </section>
    </div>

    <div
      v-if="isPreviewOpen && previewDocument"
      class="overlay-backdrop"
      @click.self="closePreview"
    >
      <section class="preview-card">
        <header class="document-form-header">
          <div>
            <h2>{{ previewDocument.title }}</h2>
            <p>
              {{ previewDocument.originalFilename }} · {{ formatBytes(previewDocument.sizeBytes) }}
            </p>
          </div>
          <div class="preview-header-actions">
            <button
              type="button"
              class="secondary-action"
              @click="downloadDocument(previewDocument)"
            >
              Download
            </button>
            <button type="button" class="close-button" @click="closePreview">Close</button>
          </div>
        </header>

        <p v-if="previewError" class="status error">{{ previewError }}</p>
        <p v-else-if="isPreviewLoading" class="status">Loading preview...</p>

        <div v-else-if="canPreviewCurrentDocument" class="preview-body">
          <iframe
            v-if="previewIsPdf && previewUrl"
            :src="previewUrl"
            title="Document preview"
            class="preview-frame"
          ></iframe>
          <img
            v-else-if="previewIsImage && previewUrl"
            :src="previewUrl"
            :alt="previewDocument.title"
            class="preview-image"
          />
          <pre v-else-if="previewIsText" class="preview-text">{{ previewTextContent }}</pre>
        </div>
      </section>
    </div>
  </section>
</template>

<style scoped>
.documents-view {
  display: grid;
  gap: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
}

.page-subtitle {
  margin: 8px 0 0;
  color: #475569;
  max-width: 64ch;
}

.toolbar-card,
.results-card,
.document-form-card {
  border: 1px solid rgba(148, 163, 184, 0.28);
  border-radius: 20px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.98)),
    radial-gradient(circle at top right, rgba(254, 240, 138, 0.22), transparent 34%);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.08);
}

.toolbar-card,
.results-card {
  padding: 20px;
}

.area-toggle {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.area-button,
.secondary-action,
.danger-action,
.primary-action,
.close-button,
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
.suggestion-row,
.actions,
.form-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.tag-row,
.suggestion-row {
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

.results-header {
  margin-bottom: 12px;
  color: #334155;
}

.table-shell {
  overflow-x: auto;
}

.documents-table {
  width: 100%;
  border-collapse: collapse;
}

.documents-table th,
.documents-table td {
  text-align: left;
  vertical-align: top;
  padding: 14px 10px;
  border-top: 1px solid rgba(226, 232, 240, 0.9);
}

.documents-table th {
  color: #475569;
  font-size: 0.92rem;
  font-weight: 700;
  border-top: 0;
}

.document-title {
  font-weight: 700;
  color: #0f172a;
}

.document-description {
  margin: 6px 0 0;
  color: #475569;
  max-width: 36ch;
}

.table-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.table-tag {
  display: inline-flex;
  align-items: center;
  padding: 4px 10px;
  border-radius: 999px;
  background: #f1f5f9;
  color: #334155;
  font-size: 0.9rem;
}

.muted {
  color: #64748b;
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

.danger-action {
  padding: 9px 14px;
  background: #fff1f2;
  border-color: #fecdd3;
  color: #be123c;
}

.primary-action:hover,
.secondary-action:hover,
.danger-action:hover,
.close-button:hover,
.area-button:hover,
.tag-chip:hover,
.suggestion-chip:hover {
  transform: translateY(-1px);
}

.primary-action:disabled,
.secondary-action:disabled,
.danger-action:disabled,
.suggestion-chip:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
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

.status.success {
  background: #ecfdf5;
  color: #047857;
}

.empty-state {
  padding: 28px 8px 10px;
  text-align: center;
  color: #475569;
}

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
}

.preview-card {
  width: min(100%, 980px);
  max-height: min(88vh, 920px);
  overflow: hidden;
  padding: 24px;
  border: 1px solid rgba(148, 163, 184, 0.28);
  border-radius: 20px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.99)),
    radial-gradient(circle at top right, rgba(125, 211, 252, 0.2), transparent 30%);
  box-shadow: 0 28px 70px rgba(15, 23, 42, 0.16);
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

.close-button {
  padding: 9px 14px;
  background: #fff;
  border-color: #cbd5e1;
}

.document-form {
  display: grid;
  gap: 16px;
}

.preview-header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.preview-body {
  min-height: 420px;
  max-height: calc(88vh - 140px);
  overflow: auto;
  border-radius: 16px;
  background: #e2e8f0;
}

.preview-frame {
  width: 100%;
  min-height: 70vh;
  border: 0;
  background: #fff;
}

.preview-image {
  display: block;
  max-width: 100%;
  max-height: 70vh;
  margin: 0 auto;
  object-fit: contain;
  background: #fff;
}

.preview-text {
  margin: 0;
  padding: 18px;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: 'IBM Plex Mono', 'SFMono-Regular', Consolas, monospace;
  font-size: 0.95rem;
  line-height: 1.55;
  color: #0f172a;
  background: #f8fafc;
}

.form-actions {
  justify-content: flex-end;
  margin-top: 8px;
}

@media (max-width: 900px) {
  .page-header {
    flex-direction: column;
  }

  .toolbar-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .documents-view {
    gap: 16px;
  }

  .page-header {
    gap: 12px;
  }

  .page-header h1 {
    font-size: 1.8rem;
  }

  .page-header button {
    width: 100%;
  }

  .toolbar-card,
  .results-card,
  .document-form-card,
  .preview-card {
    padding: 14px;
    border-radius: 16px;
  }

  .toolbar-card,
  .results-card {
    overflow: hidden;
  }

  .area-toggle,
  .tag-row,
  .suggestion-row,
  .actions,
  .form-actions {
    gap: 8px;
  }

  .area-button,
  .secondary-action,
  .danger-action,
  .primary-action,
  .close-button,
  .tag-chip,
  .suggestion-chip {
    width: 100%;
    justify-content: center;
  }

  .toolbar-grid {
    gap: 12px;
    margin-top: 14px;
  }

  .field input,
  .field textarea,
  .field select {
    min-width: 0;
  }

  .table-shell {
    overflow: visible;
  }

  .documents-table,
  .documents-table thead,
  .documents-table tbody,
  .documents-table tr,
  .documents-table td,
  .documents-table th {
    display: block;
    width: 100%;
  }

  .documents-table thead {
    display: none;
  }

  .documents-table tr {
    margin-bottom: 12px;
    padding: 14px;
    border: 1px solid rgba(148, 163, 184, 0.2);
    border-radius: 16px;
    background: rgba(255, 255, 255, 0.92);
  }

  .documents-table td {
    padding: 10px 0;
    border-top: 0;
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 12px;
  }

  .documents-table td::before {
    flex: 0 0 7.5rem;
    color: #64748b;
    font-size: 0.72rem;
    font-weight: 700;
    letter-spacing: 0.06em;
    text-transform: uppercase;
  }

  .documents-table td:nth-child(1)::before {
    content: 'Document';
  }

  .documents-table td:nth-child(2)::before {
    content: 'Bucket';
  }

  .documents-table td:nth-child(3)::before {
    content: 'Tags';
  }

  .documents-table td:nth-child(4)::before {
    content: 'Updated';
  }

  .documents-table td:nth-child(5)::before {
    content: 'File';
  }

  .documents-table td:nth-child(6)::before {
    content: 'Uploaded by';
  }

  .documents-table td:nth-child(7)::before {
    content: 'Actions';
  }

  .document-title,
  .document-description,
  .muted,
  .table-tags,
  .actions {
    min-width: 0;
  }

  .document-title,
  .document-description,
  .muted,
  .table-tag {
    overflow-wrap: anywhere;
    word-break: break-word;
  }

  .table-tags {
    justify-content: flex-end;
  }

  .actions {
    width: 100%;
    justify-content: flex-end;
  }

  .actions .secondary-action,
  .actions .danger-action {
    width: auto;
    flex: 1 1 0;
  }

  .overlay-backdrop {
    padding: 12px;
    align-items: stretch;
  }

  .document-form-card,
  .preview-card {
    width: 100%;
    max-height: calc(100vh - 24px);
    overflow: auto;
  }

  .document-form-header,
  .preview-card .document-form-header {
    flex-direction: column;
  }

  .preview-header-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .preview-header-actions .secondary-action,
  .preview-header-actions .close-button {
    flex: 1 1 0;
  }

  .preview-body {
    min-height: 0;
    max-height: none;
  }

  .preview-frame {
    min-height: 58vh;
  }

  .documents-table th:nth-child(3),
  .documents-table td:nth-child(3),
  .documents-table th:nth-child(6),
  .documents-table td:nth-child(6) {
    display: none;
  }
}
</style>
