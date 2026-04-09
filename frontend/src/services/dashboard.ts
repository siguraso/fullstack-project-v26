import { jsonApiFetch } from './util/apiHelper'
import { assertOk, parseResponseBody, unwrapMaybeEnvelope } from './util/util'

export async function getDashboardOverview() {
  const response = await jsonApiFetch('/api/dashboard/overview')
  await assertOk(response, 'Failed to load dashboard overview.')

  const payload = await parseResponseBody(response)
  return unwrapMaybeEnvelope<unknown>(payload)
}
