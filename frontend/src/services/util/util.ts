export type ApiEnvelope<T> = {
  success?: boolean
  message?: string | null
  error?: string | null
  detail?: string | null
  data?: T
}

export function unwrap<T>(payload: T | ApiEnvelope<T>): T {
  if (payload && typeof payload === 'object' && 'data' in payload) {
    return (payload as ApiEnvelope<T>).data as T
  }

  return payload as T
}

export async function parseJsonSafely(response: Response): Promise<unknown> {
  const contentType = response.headers.get('content-type') ?? ''

  if (!contentType.includes('application/json')) {
    return null
  }

  try {
    return await response.json()
  } catch {
    return null
  }
}

export async function parseResponseBody(response: Response): Promise<unknown> {
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

export function readErrorMessage(payload: unknown, fallback: string): string {
  if (!payload || typeof payload !== 'object') {
    return fallback
  }

  const record = payload as Record<string, unknown>
  const candidate = record.message ?? record.error ?? record.detail

  return typeof candidate === 'string' && candidate.trim().length > 0 ? candidate : fallback
}

export function unwrapMaybeEnvelope<T>(payload: unknown): T {
  if (payload && typeof payload === 'object' && 'data' in (payload as Record<string, unknown>)) {
    return unwrap<T>(payload as ApiEnvelope<T>)
  }

  return payload as T
}

export async function assertOk(response: Response, fallback: string): Promise<void> {
  if (response.ok) {
    return
  }

  const payload = await parseResponseBody(response)
  throw new Error(readErrorMessage(payload, fallback))
}
