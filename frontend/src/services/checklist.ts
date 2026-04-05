const API_URL = '/api/checklists'

export async function getTodayChecklist(tenantId: number) {
  const res = await fetch(`${API_URL}/today?tenantId=${tenantId}`)

  if (!res.ok) throw new Error('Failed to fetch checklist')

  return res.json()
}

export async function generateChecklist(templateId: number) {
  await fetch(`/api/checklists/templates/${templateId}/generate`, {
    method: 'POST'
  })
}

export async function updateChecklistItem(id: number, completed: boolean) {
  const res = await fetch(`${API_URL}/items/${id}`, {
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

export async function getTemplates(tenantId: number) {
  const res = await fetch(`${API_URL}/templates?tenantId=${tenantId}`)
  return res.json()
}

export async function createTemplate(payload: any) {
  return fetch(`${API_URL}/templates`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export async function updateTemplate(id: number, payload: any) {
  return fetch(`${API_URL}/templates/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })
}

export async function getLibraryItems(tenantId: number) {
  const res = await fetch(`/api/checklist-library?tenantId=${tenantId}`, {
    method: 'GET',
    headers: { 'Content-Type': 'application/json'},
  })
  return res.json()
}

export async function createLibraryItem(tenantId: number, payload: any) {
  const res = await fetch(`/api/checklist-library?tenantId=${tenantId}`, {
    method: 'POST',
    headers: { 'Content-type': 'application/json' },
    body: JSON.stringify(payload)
  })

  return res.json()
}

export async function deleteLibraryItem(id: number) {
  await fetch(`/api/checklist-library/${id}`, {
    method: 'DELETE'
  })
}

export async function updateLibraryItem(id: number, payload: any) {
  const res = await fetch(`/api/checklist-library/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  })

  if (!res.ok) throw new Error('Update failed')

  return res.json()
}

export async function deleteTemplate(id: number) {
  const res = await fetch(`/api/checklists/templates/${id}`, {
    method: "DELETE"
  })

  if (!res.ok) throw new Error("Delete failed")
}

export async function isLibraryItemInUse(id: number) {
  const res = await fetch(`/api/checklist-library/${id}/in-use`)
  return res.json()
}

export async function toggleTemplate(id: number) {
  await fetch(`/api/checklists/templates/${id}/toggle`, {
    method: 'PATCH'
  })
}