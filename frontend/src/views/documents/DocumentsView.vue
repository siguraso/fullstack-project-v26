<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import type {
  DocumentArea,
  DocumentDetail,
  DocumentSummary,
  DocumentUpsertPayload,
} from '@/interfaces/Document.interface'
import { getAuthSession } from '@/services/auth'
import {
  createDocument,
  deleteDocument,
  downloadDocumentFile,
  fetchDocuments,
  getDocumentById,
  updateDocument,
} from '@/services/document'
import DocumentFormDialog from './components/DocumentFormDialog.vue'
import DocumentPreviewDialog from './components/DocumentPreviewDialog.vue'
import DocumentsTable from './components/DocumentsTable.vue'
import DocumentsToolbar from './components/DocumentsToolbar.vue'
import { documentAreaOptions, normalizeDocumentTag } from './documents.helpers'

const documents = ref<DocumentSummary[]>([])
const isLoading = ref(false)
const loadError = ref('')
const successMessage = ref('')
const searchQuery = ref('')
const selectedArea = ref<DocumentArea | null>(null)
const selectedTags = ref<string[]>([])
const filterTagDraft = ref('')
const isFormOpen = ref(false)
const isEditing = ref(false)
const editingDocumentId = ref<number | null>(null)
const editingDocument = ref<DocumentDetail | null>(null)
const isSaving = ref(false)
const isDeletingId = ref<number | null>(null)
const isLoadingDetail = ref(false)
const formError = ref('')
const activeDownloadId = ref<number | null>(null)
const previewDocument = ref<DocumentSummary | null>(null)
const isPreviewOpen = ref(false)
let searchDebounceHandle: number | null = null

const canManage = computed(() => {
  const role = getAuthSession()?.role
  return role === 'ADMIN' || role === 'MANAGER'
})

const availableTags = computed(() =>
  Array.from(new Set(documents.value.flatMap((document) => document.tags))).sort((left, right) =>
    left.localeCompare(right),
  ),
)

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
  const normalized = normalizeDocumentTag(tag)

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

function openCreateForm() {
  isEditing.value = false
  editingDocumentId.value = null
  editingDocument.value = null
  formError.value = ''
  isLoadingDetail.value = false
  isFormOpen.value = true
}

async function openEditForm(documentId: number) {
  isEditing.value = true
  editingDocumentId.value = documentId
  editingDocument.value = null
  isFormOpen.value = true
  formError.value = ''
  isLoadingDetail.value = true

  try {
    editingDocument.value = await getDocumentById(documentId)
  } catch (error) {
    formError.value = error instanceof Error ? error.message : 'Failed to load document.'
  } finally {
    isLoadingDetail.value = false
  }
}

function closeForm() {
  isFormOpen.value = false
  isLoadingDetail.value = false
  formError.value = ''
  editingDocumentId.value = null
  editingDocument.value = null
}

async function saveForm(payload: DocumentUpsertPayload) {
  if (!canManage.value) {
    return
  }

  successMessage.value = ''
  formError.value = ''
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

function openPreview(document: DocumentSummary) {
  previewDocument.value = document
  isPreviewOpen.value = true
}

function closePreview() {
  isPreviewOpen.value = false
  previewDocument.value = null
}

watch([selectedArea, searchQuery, selectedTags], () => {
  scheduleReload()
})

onBeforeUnmount(() => {
  if (searchDebounceHandle !== null) {
    window.clearTimeout(searchDebounceHandle)
  }
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

    <DocumentsToolbar
      :area-options="documentAreaOptions"
      :selected-area="selectedArea"
      :search-query="searchQuery"
      :filter-tag-draft="filterTagDraft"
      :selected-tags="selectedTags"
      :available-tags="availableTags"
      @update:selected-area="selectedArea = $event"
      @update:search-query="searchQuery = $event"
      @update:filter-tag-draft="filterTagDraft = $event"
      @add-filter-tag="addFilterTag"
      @remove-filter-tag="removeFilterTag"
    />

    <p v-if="successMessage" class="status success">{{ successMessage }}</p>
    <p v-if="loadError" class="status error">{{ loadError }}</p>
    <p v-if="isLoading" class="status">Loading documents...</p>

    <DocumentsTable
      v-else
      :documents="documents"
      :can-manage="canManage"
      :active-download-id="activeDownloadId"
      :is-deleting-id="isDeletingId"
      @preview="openPreview"
      @download="downloadDocument"
      @edit="openEditForm"
      @delete="deleteSelectedDocument"
    />

    <DocumentFormDialog
      :is-open="isFormOpen"
      :is-editing="isEditing"
      :is-loading-detail="isLoadingDetail"
      :is-saving="isSaving"
      :error-message="formError"
      :default-area="selectedArea"
      :document="editingDocument"
      @close="closeForm"
      @submit="saveForm"
    />

    <DocumentPreviewDialog
      :is-open="isPreviewOpen"
      :document="previewDocument"
      :active-download-id="activeDownloadId"
      @close="closePreview"
      @download="downloadDocument"
    />
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

.primary-action {
  padding: 12px 16px;
  border-radius: 999px;
  border: 1px solid transparent;
  cursor: pointer;
  transition:
    transform 140ms ease,
    background-color 140ms ease,
    border-color 140ms ease;
  color: black;
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

.primary-action:hover {
  transform: translateY(-1px);
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

@media (max-width: 900px) {
  .page-header {
    flex-direction: column;
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
}
</style>
