import { jsonApiFetch } from './util/apiHelper'
import { assertOk, parseResponseBody, unwrapMaybeEnvelope } from './util/util'

import type { InspectionExportFilter, InspectionLog, InspectionStats } from '../interfaces/Inspection.interface'

export async function getInspectionLogs(): Promise<InspectionLog[]> {
  const response = await jsonApiFetch('/api/inspection/logs')
  await assertOk(response, 'Failed to load inspection logs.')

  const payload = await parseResponseBody(response)
  const logs = unwrapMaybeEnvelope<unknown>(payload)

  return Array.isArray(logs) ? (logs as InspectionLog[]) : []
}

export async function getInspectionStats(): Promise<InspectionStats> {
  const response = await jsonApiFetch('/api/inspection/stats')
  await assertOk(response, 'Failed to load inspection stats.')

  const payload = await parseResponseBody(response)
  return unwrapMaybeEnvelope<InspectionStats>(payload) as InspectionStats
}

export async function exportInspectionPdf(filter: InspectionExportFilter): Promise<Blob> {
  const response = await jsonApiFetch('/api/inspection/export/pdf', {
    method: 'POST',
    body: JSON.stringify(filter),
  })
  await assertOk(response, 'Failed to export inspection PDF.')
  return response.blob()
}
