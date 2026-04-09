<script setup lang="ts">
import type { DocumentSummary } from '@/interfaces/Document.interface'
import { displayDocumentArea, formatDocumentBytes, formatDocumentDate } from '../documents.helpers'

defineProps<{
  documents: DocumentSummary[]
  canManage: boolean
  activeDownloadId: number | null
  isDeletingId: number | null
}>()

const emit = defineEmits<{
  (event: 'preview', document: DocumentSummary): void
  (event: 'download', document: DocumentSummary): void
  (event: 'edit', documentId: number): void
  (event: 'delete', document: DocumentSummary): void
}>()
</script>

<template>
  <section class="results-card">
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
            <td>{{ displayDocumentArea(document.area) }}</td>
            <td>
              <div class="table-tags">
                <span v-for="tag in document.tags" :key="tag" class="table-tag">{{ tag }}</span>
                <span v-if="document.tags.length === 0" class="muted">No tags</span>
              </div>
            </td>
            <td>{{ formatDocumentDate(document.updatedAt ?? document.createdAt) }}</td>
            <td>
              <div>{{ document.originalFilename }}</div>
              <div class="muted">{{ formatDocumentBytes(document.sizeBytes) }}</div>
            </td>
            <td>{{ document.uploadedByName ?? 'Unknown' }}</td>
            <td>
              <div class="actions">
                <button type="button" class="secondary-action" @click="emit('preview', document)">
                  View
                </button>
                <button
                  type="button"
                  class="secondary-action"
                  :disabled="activeDownloadId === document.id"
                  @click="emit('download', document)"
                >
                  {{ activeDownloadId === document.id ? 'Downloading…' : 'Download' }}
                </button>
                <button
                  v-if="canManage"
                  type="button"
                  class="secondary-action"
                  @click="emit('edit', document.id)"
                >
                  Edit
                </button>
                <button
                  v-if="canManage"
                  type="button"
                  class="danger-action"
                  :disabled="isDeletingId === document.id"
                  @click="emit('delete', document)"
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
</template>

<style scoped>
.results-card {
  padding: 20px;
  border: 1px solid rgba(148, 163, 184, 0.28);
  border-radius: 20px;
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.96), rgba(248, 250, 252, 0.98)),
    radial-gradient(circle at top right, rgba(254, 240, 138, 0.22), transparent 34%);
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.08);
}

.results-header {
  margin-bottom: 12px;
  color: #334155;
}

.empty-state {
  padding: 28px 8px 10px;
  text-align: center;
  color: #475569;
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

.actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}

.secondary-action,
.danger-action {
  border-radius: 999px;
  border: 1px solid transparent;
  cursor: pointer;
  transition:
    transform 140ms ease,
    background-color 140ms ease,
    border-color 140ms ease;
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

.secondary-action:hover,
.danger-action:hover {
  transform: translateY(-1px);
}

.secondary-action:disabled,
.danger-action:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 640px) {
  .results-card {
    padding: 14px;
    border-radius: 16px;
    overflow: hidden;
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
    gap: 8px;
  }

  .actions .secondary-action,
  .actions .danger-action {
    width: auto;
    flex: 1 1 0;
  }

  .documents-table th:nth-child(3),
  .documents-table td:nth-child(3),
  .documents-table th:nth-child(6),
  .documents-table td:nth-child(6) {
    display: none;
  }
}
</style>
