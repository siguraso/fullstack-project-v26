import { jsonApiFetch } from './util/apiHelper'
import { assertOk, parseResponseBody, unwrapMaybeEnvelope } from './util/util'

import type { InspectionLog } from '../interfaces/Inspection.interface'

export async function getInspectionLogs(): Promise<InspectionLog[]> {
  const response = await jsonApiFetch('/api/inspection/logs')
  await assertOk(response, 'Failed to load inspection logs.')

  const payload = await parseResponseBody(response)
  const logs = unwrapMaybeEnvelope<unknown>(payload)

  return Array.isArray(logs) ? (logs as InspectionLog[]) : []
}
