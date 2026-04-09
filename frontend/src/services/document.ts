import type {
  DocumentArea,
  DocumentDetail,
  DocumentFilters,
  DocumentSummary,
  DocumentUpsertPayload,
} from '@/interfaces/Document.interface'
import { jsonApiFetch } from './util/apiHelper'
import { parseJsonSafely, parseResponseBody, readErrorMessage, unwrap } from './util/util'
import type { ApiEnvelope } from './util/util'

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '/api').replace(/\/$/, '')
const API_URL = `${API_BASE_URL}/documents`

type DocumentResponse = {
  id: number
  area: DocumentArea
  title: string
  description?: string | null
  originalFilename: string
  mimeType: string
  sizeBytes: number
  uploadedById?: number | null
  uploadedByName?: string | null
  uploadedByRole?: 'ADMIN' | 'MANAGER' | 'STAFF' | null
  createdAt?: string | null
  updatedAt?: string | null
  tags?: string[]
}

function normalizeDocument(raw: unknown): DocumentSummary | null {
  if (!raw || typeof raw !== 'object') {
    return null
  }

  const record = raw as Record<string, unknown>

  if (
    typeof record.id !== 'number' ||
    typeof record.title !== 'string' ||
    typeof record.originalFilename !== 'string' ||
    typeof record.mimeType !== 'string' ||
    typeof record.sizeBytes !== 'number'
  ) {
    return null
  }

  const area =
    record.area === 'GENERAL' || record.area === 'IK_FOOD' || record.area === 'IK_ALCOHOL'
      ? record.area
      : 'GENERAL'

  return {
    id: record.id,
    area,
    title: record.title,
    description: typeof record.description === 'string' ? record.description : null,
    originalFilename: record.originalFilename,
    mimeType: record.mimeType,
    sizeBytes: record.sizeBytes,
    uploadedById: typeof record.uploadedById === 'number' ? record.uploadedById : null,
    uploadedByName: typeof record.uploadedByName === 'string' ? record.uploadedByName : null,
    uploadedByRole:
      record.uploadedByRole === 'ADMIN' ||
      record.uploadedByRole === 'MANAGER' ||
      record.uploadedByRole === 'STAFF'
        ? record.uploadedByRole
        : null,
    createdAt: typeof record.createdAt === 'string' ? record.createdAt : null,
    updatedAt: typeof record.updatedAt === 'string' ? record.updatedAt : null,
    tags: Array.isArray(record.tags)
      ? record.tags.filter((tag): tag is string => typeof tag === 'string')
      : [],
  }
}

function buildFormData(payload: DocumentUpsertPayload) {
  const formData = new FormData()
  formData.append('title', payload.title)
  formData.append('description', payload.description ?? '')
  formData.append('area', payload.area)

  for (const tag of payload.tags) {
    formData.append('tags', tag)
  }

  if (payload.file) {
    formData.append('file', payload.file)
  }

  return formData
}

export async function fetchDocuments(filters: DocumentFilters = {}): Promise<DocumentSummary[]> {
  const params = new URLSearchParams()

  if (filters.area) {
    params.set('area', filters.area)
  }

  if (filters.query?.trim()) {
    params.set('q', filters.query.trim())
  }

  for (const tag of filters.tags ?? []) {
    if (tag.trim()) {
      params.append('tags', tag.trim().toLowerCase())
    }
  }

  const endpoint = params.toString() ? `${API_URL}?${params.toString()}` : API_URL
  const response = await jsonApiFetch(endpoint, {
    credentials: 'include',
  })

  if (!response.ok) {
    const payload = await parseResponseBody(response)
    throw new Error(readErrorMessage(payload, 'Failed to fetch documents.'))
  }

  const data = await parseJsonSafely(response)
  const unwrapped = data
    ? unwrap<Array<DocumentResponse>>(data as ApiEnvelope<Array<DocumentResponse>>)
    : []

  if (!Array.isArray(unwrapped)) {
    return []
  }

  return unwrapped
    .map((entry) => normalizeDocument(entry))
    .filter((entry): entry is DocumentSummary => entry !== null)
}

export async function getDocumentById(id: number): Promise<DocumentDetail> {
  const response = await jsonApiFetch(`${API_URL}/${id}`, {
    credentials: 'include',
  })

  if (!response.ok) {
    const payload = await parseResponseBody(response)
    throw new Error(readErrorMessage(payload, 'Failed to load document details.'))
  }

  const data = await parseJsonSafely(response)
  const unwrapped = data ? unwrap<DocumentResponse>(data as ApiEnvelope<DocumentResponse>) : null
  const normalized = normalizeDocument(unwrapped)

  if (!normalized) {
    throw new Error('Received malformed document details from server.')
  }

  return normalized
}

export async function createDocument(payload: DocumentUpsertPayload): Promise<DocumentDetail> {
  const response = await jsonApiFetch(API_URL, {
    method: 'POST',
    credentials: 'include',
    body: buildFormData(payload),
  })

  if (!response.ok) {
    const responseBody = await parseResponseBody(response)
    throw new Error(readErrorMessage(responseBody, 'Failed to upload document.'))
  }

  const data = await parseJsonSafely(response)
  const unwrapped = data ? unwrap<DocumentResponse>(data as ApiEnvelope<DocumentResponse>) : null
  const normalized = normalizeDocument(unwrapped)

  if (!normalized) {
    throw new Error('Received malformed upload response from server.')
  }

  return normalized
}

export async function updateDocument(
  id: number,
  payload: DocumentUpsertPayload,
): Promise<DocumentDetail> {
  const response = await jsonApiFetch(`${API_URL}/${id}`, {
    method: 'PUT',
    credentials: 'include',
    body: buildFormData(payload),
  })

  if (!response.ok) {
    const responseBody = await parseResponseBody(response)
    throw new Error(readErrorMessage(responseBody, 'Failed to update document.'))
  }

  const data = await parseJsonSafely(response)
  const unwrapped = data ? unwrap<DocumentResponse>(data as ApiEnvelope<DocumentResponse>) : null
  const normalized = normalizeDocument(unwrapped)

  if (!normalized) {
    throw new Error('Received malformed update response from server.')
  }

  return normalized
}

export async function deleteDocument(id: number): Promise<void> {
  const response = await jsonApiFetch(`${API_URL}/${id}`, {
    method: 'DELETE',
    credentials: 'include',
  })

  if (!response.ok) {
    const responseBody = await parseResponseBody(response)
    throw new Error(readErrorMessage(responseBody, 'Failed to delete document.'))
  }
}

function parseFilenameFromDisposition(headerValue: string | null) {
  if (!headerValue) {
    return null
  }

  const utfMatch = headerValue.match(/filename\*=UTF-8''([^;]+)/i)
  if (utfMatch?.[1]) {
    return decodeURIComponent(utfMatch[1])
  }

  const asciiMatch = headerValue.match(/filename=\"([^\"]+)\"/i)
  return asciiMatch?.[1] ?? null
}

export interface DocumentFileData {
  blob: Blob
  filename: string
  mimeType: string
}

export async function fetchDocumentFile(
  document: Pick<DocumentSummary, 'id' | 'originalFilename'>,
): Promise<DocumentFileData> {
  const response = await jsonApiFetch(`${API_URL}/${document.id}/download`, {
    credentials: 'include',
  })

  if (!response.ok) {
    const responseBody = await parseResponseBody(response)
    throw new Error(readErrorMessage(responseBody, 'Failed to download document.'))
  }

  const blob = await response.blob()
  const filename =
    parseFilenameFromDisposition(response.headers.get('content-disposition')) ??
    document.originalFilename

  return {
    blob,
    filename,
    mimeType: response.headers.get('content-type') ?? blob.type,
  }
}

export async function downloadDocumentFile(document: Pick<DocumentSummary, 'id' | 'originalFilename'>) {
  const file = await fetchDocumentFile(document)
  const url = window.URL.createObjectURL(file.blob)
  const anchor = window.document.createElement('a')

  anchor.href = url
  anchor.download = file.filename
  window.document.body.appendChild(anchor)
  anchor.click()
  anchor.remove()
  window.URL.revokeObjectURL(url)
}
