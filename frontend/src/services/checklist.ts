const API_URL = '/api/checklists'

export async function getTodayChecklist(tenantId: number) {
  const res = await fetch(`${API_URL}/today?tenantId=${tenantId}`)

  if (!res.ok) throw new Error('Failed to fetch checklist')

  return res.json()
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