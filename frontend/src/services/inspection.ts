import { apiFetch } from './apiHelper'

import type { InspectionLog } from '../interfaces/Inspection.interface'

export async function getInspectionLogs(): Promise<InspectionLog[]> {
  const res = await apiFetch('/api/inspection/logs')

  if (!res.ok) return []

  const json = await res.json()
  return json.data ?? []
}
