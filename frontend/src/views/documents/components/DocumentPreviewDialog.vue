<script setup lang="ts">
import { onBeforeUnmount, ref, watch, computed } from 'vue'
import type { DocumentSummary } from '@/interfaces/Document.interface'
import { fetchDocumentFile } from '@/services/document'
import { canPreviewDocument, formatDocumentBytes } from '../documents.helpers'

const props = defineProps<{
  isOpen: boolean
  document: DocumentSummary | null
  activeDownloadId: number | null
}>()

const emit = defineEmits<{
  (event: 'close'): void
  (event: 'download', document: DocumentSummary): void
}>()

const isPreviewLoading = ref(false)
const previewError = ref('')
const previewUrl = ref('')
const previewTextContent = ref('')
let activeRequestId = 0

const previewMimeType = computed(() => props.document?.mimeType ?? '')
const previewIsPdf = computed(() => previewMimeType.value === 'application/pdf')
const previewIsImage = computed(() => previewMimeType.value.startsWith('image/'))
const previewIsText = computed(
  () =>
    previewMimeType.value.startsWith('text/') ||
    props.document?.originalFilename.toLowerCase().endsWith('.txt'),
)
const canPreviewCurrentDocument = computed(
  () => !!props.document && canPreviewDocument(props.document),
)

function revokePreviewUrl() {
  if (!previewUrl.value) {
    return
  }

  window.URL.revokeObjectURL(previewUrl.value)
  previewUrl.value = ''
}

function resetPreviewState() {
  isPreviewLoading.value = false
  previewError.value = ''
  previewTextContent.value = ''
  revokePreviewUrl()
}

async function loadPreview(document: DocumentSummary) {
  previewError.value = ''
  previewTextContent.value = ''
  revokePreviewUrl()

  if (!canPreviewDocument(document)) {
    previewError.value =
      'This file type cannot be previewed in the browser yet. Download it to inspect the contents.'
    return
  }

  const requestId = ++activeRequestId
  isPreviewLoading.value = true

  try {
    const file = await fetchDocumentFile(document)

    if (requestId !== activeRequestId) {
      return
    }

    if (
      file.mimeType.startsWith('text/') ||
      document.originalFilename.toLowerCase().endsWith('.txt')
    ) {
      previewTextContent.value = await file.blob.text()
    }

    if (requestId !== activeRequestId) {
      return
    }

    previewUrl.value = window.URL.createObjectURL(file.blob)
  } catch (error) {
    if (requestId !== activeRequestId) {
      return
    }

    previewError.value = error instanceof Error ? error.message : 'Failed to load preview.'
  } finally {
    if (requestId === activeRequestId) {
      isPreviewLoading.value = false
    }
  }
}

watch(
  [() => props.isOpen, () => props.document?.id],
  ([isOpen]) => {
    activeRequestId += 1
    resetPreviewState()

    if (isOpen && props.document) {
      void loadPreview(props.document)
    }
  },
  { immediate: true },
)

onBeforeUnmount(() => {
  activeRequestId += 1
  revokePreviewUrl()
})
</script>

<template>
  <Teleport to="body">
    <div v-if="isOpen && document" class="overlay-backdrop" @click.self="emit('close')">
      <section class="preview-card">
        <header class="document-form-header">
          <div>
            <h2>{{ document.title }}</h2>
            <p>{{ document.originalFilename }} · {{ formatDocumentBytes(document.sizeBytes) }}</p>
          </div>
          <div class="preview-header-actions">
            <button
              type="button"
              class="secondary-action"
              :disabled="activeDownloadId === document.id"
              @click="emit('download', document)"
            >
              {{ activeDownloadId === document.id ? 'Downloading…' : 'Download' }}
            </button>
            <button type="button" class="close-button" @click="emit('close')">Close</button>
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
            :alt="document.title"
            class="preview-image"
          />
          <pre v-else-if="previewIsText" class="preview-text">{{ previewTextContent }}</pre>
        </div>
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

.preview-header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.secondary-action,
.close-button {
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

.close-button {
  padding: 9px 14px;
  background: #fff;
  border-color: #cbd5e1;
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

.secondary-action:hover,
.close-button:hover {
  transform: translateY(-1px);
}

.secondary-action:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 640px) {
  .overlay-backdrop {
    padding: 12px;
    align-items: stretch;
  }

  .preview-card {
    width: 100%;
    max-height: calc(100vh - 24px);
    overflow: auto;
    padding: 14px;
    border-radius: 16px;
  }

  .document-form-header {
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
}
</style>
