import { jsonApiFetch } from './util/apiHelper'
import { parseResponseBody, readErrorMessage, unwrap } from './util/util'
import type { ApiEnvelope } from './util/util'

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '/api').replace(/\/$/, '')

interface InviteValidationResponseData {
  valid: boolean
  email: string
  organizationId: number
}

export async function validateInviteToken(token: string): Promise<InviteValidationResponseData> {
  const response = await jsonApiFetch(
    `${API_BASE_URL}/invitations/validate?token=${encodeURIComponent(token)}`,
    {
      credentials: 'include',
    },
  )

  const payload = await parseResponseBody(response)

  if (!response.ok) {
    throw new Error(readErrorMessage(payload, 'Invalid or expired invite token.'))
  }

  return unwrap(payload as InviteValidationResponseData | ApiEnvelope<InviteValidationResponseData>)
}
