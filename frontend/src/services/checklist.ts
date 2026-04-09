import { apiFetch } from './util/apiHelper'
import type { ApiEnvelope } from './util/util'
import { parseJsonSafely, unwrap } from './util/util'

const API_URL = '/api/checklists'

function unwrapMaybeEnvelope<T>(payload: unknown): T {
  if (payload && typeof payload === 'object' && 'data' in (payload as Record<string, unknown>)) {
    return unwrap<T>(payload as ApiEnvelope<T>)
  }

  return payload as T
}

async function readJson<T>(res: Response, fallbackMessage: string): Promise<T> {
  const payload = await parseJsonSafely(res)

  if (!res.ok) {
    throw new Error(fallbackMessage)
  }

  return unwrapMaybeEnvelope<T>(payload)
}

export async function getTodayChecklist() {
  const res = await apiFetch(`${API_URL}/today`)
  const data = await readJson<unknown>(res, 'Failed to fetch checklist')

  return Array.isArray(data) ? data : []
}

export async function generateChecklist(templateId: number) {
  if (!Number.isFinite(templateId)) {
    throw new Error('Invalid template id')
  }

  const res = await apiFetch(`${API_URL}/templates/${templateId}/generate`, {
    method: 'POST',
  })

  if (!res.ok) {
    throw new Error('Failed to generate checklist')
  }
}

export async function updateChecklistItem(id: number, completed: boolean) {
  const res = await apiFetch(`${API_URL}/items/${id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({
      completed,
      comment: null,
    }),
  })

  if (!res.ok) throw new Error('Failed to update item')
}

export async function getTemplates() {
  const res = await apiFetch(`${API_URL}/templates`)
  const data = await readJson<unknown>(res, 'Failed to fetch templates')

  return Array.isArray(data) ? data : []
}

export async function createTemplate(payload: any) {
  const res = await apiFetch(`${API_URL}/templates`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

  return readJson<any>(res, 'Failed to create template')
}

export async function updateTemplate(id: number, payload: any) {
  if (!Number.isFinite(id)) {
    throw new Error('Invalid template id')
  }

  const res = await apiFetch(`${API_URL}/templates/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

  return readJson<any>(res, 'Failed to update template')
}

export async function getLibraryItems() {
  const res = await apiFetch(`/api/checklist-library`, {
    method: 'GET',
  })
  const data = await readJson<unknown>(res, 'Failed to fetch library items')

  return Array.isArray(data) ? data : []
}

export async function createLibraryItem(payload: any) {
  const res = await apiFetch(`/api/checklist-library`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

  return readJson<any>(res, 'Failed to create library item')
}

export async function deleteLibraryItem(id: number) {
  if (!Number.isFinite(id)) {
    throw new Error('Invalid library item id')
  }

  const res = await apiFetch(`/api/checklist-library/${id}`, {
    method: 'DELETE',
  })

  if (!res.ok) {
    throw new Error('Failed to delete library item')
  }
}

export async function updateLibraryItem(id: number, payload: any) {
  if (!Number.isFinite(id)) {
    throw new Error('Invalid library item id')
  }

  const res = await apiFetch(`/api/checklist-library/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

  return readJson<any>(res, 'Update failed')
}

export async function deleteTemplate(id: number) {
  if (!Number.isFinite(id)) {
    throw new Error('Invalid template id')
  }

  const res = await apiFetch(`/api/checklists/templates/${id}`, {
    method: 'DELETE',
  })

  if (!res.ok) throw new Error('Delete failed')
}

export async function isLibraryItemInUse(id: number) {
  if (!Number.isFinite(id)) {
    return false
  }

  const res = await apiFetch(`/api/checklist-library/${id}/in-use`)
  const data = await readJson<unknown>(res, 'Failed to check library item usage')

  return Boolean(data)
}

export async function toggleTemplate(id: number) {
  if (!Number.isFinite(id)) {
    throw new Error('Invalid template id')
  }

  const res = await apiFetch(`/api/checklists/templates/${id}/toggle`, {
    method: 'PATCH',
  })

  if (!res.ok) {
    throw new Error('Failed to toggle template')
  }
}
