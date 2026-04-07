import { apiFetch } from './apiHelper'

export interface InspectionLog {
  type: 'DEVIATION' | 'TEMPERATURE' | 'ALCOHOL'
  referenceId: number
  title: string
  description?: string
  status?: string
  severity?: string
  module?: string
  actor?: string
  timestamp: string
}

export async function getInspectionLogs(): Promise<InspectionLog[]> {
  const res = await apiFetch('/api/inspection/logs')

  if (!res.ok) return []

  const json = await res.json()
  return json.data ?? []
}