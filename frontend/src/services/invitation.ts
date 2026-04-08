const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '/api').replace(/\/$/, '')

type ApiEnvelope<T> = {
  success?: boolean
  message?: string | null
  error?: string | null
  detail?: string | null
  data?: T
}

interface InviteValidationResponseData {
  valid: boolean
  email: string
  organizationId: number
}

function unwrap<T>(payload: T | ApiEnvelope<T>): T {
  if (payload && typeof payload === 'object' && 'data' in payload) {
    return (payload as ApiEnvelope<T>).data as T
  }

  return payload as T
}

function readErrorMessage(payload: unknown, fallback: string): string {
  if (!payload || typeof payload !== 'object') {
    return fallback
  }

  const record = payload as Record<string, unknown>
  const candidate = record.message ?? record.error ?? record.detail

  return typeof candidate === 'string' && candidate.trim().length > 0 ? candidate : fallback
}

async function parseResponseBody(response: Response): Promise<unknown> {
  const contentType = response.headers.get('content-type') ?? ''

  if (contentType.includes('application/json')) {
    try {
      return await response.json()
    } catch {
      return null
    }
  }

  const text = await response.text()

  return text.length > 0 ? { message: text } : null
}

export async function validateInviteToken(token: string): Promise<InviteValidationResponseData> {
  const response = await fetch(
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
