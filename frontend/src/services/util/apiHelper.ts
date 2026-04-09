import type { AuthSession } from '@/interfaces/Auth.interface'
import { parseResponseBody, unwrapMaybeEnvelope } from './util'

const AUTH_SESSION_KEY = 'regula.auth.session'

type JsonApiFetchOptions = RequestInit & {
  skipAuthRefresh?: boolean
}

function parseSession(value: string | null): AuthSession | null {
  if (!value) return null

  try {
    const parsed = JSON.parse(value) as Partial<AuthSession>

    return {
      email: parsed.email ?? '',
      remember: parsed.remember ?? false,
      token: parsed.token ?? null,
      refreshToken: parsed.refreshToken ?? null,
      role: parsed.role ?? null,
    }
  } catch {
    return null
  }
}

function readStoredSession(): { session: AuthSession; storage: Storage } | null {
  const localSession = parseSession(window.localStorage.getItem(AUTH_SESSION_KEY))

  if (localSession) {
    return { session: localSession, storage: window.localStorage }
  }

  const sessionSession = parseSession(window.sessionStorage.getItem(AUTH_SESSION_KEY))

  if (sessionSession) {
    return { session: sessionSession, storage: window.sessionStorage }
  }

  return null
}

function clearStoredSession() {
  window.localStorage.removeItem(AUTH_SESSION_KEY)
  window.sessionStorage.removeItem(AUTH_SESSION_KEY)
}

function readAccessToken(payload: unknown): string | null {
  const unwrappedPayload = unwrapMaybeEnvelope<unknown>(payload)

  if (!unwrappedPayload || typeof unwrappedPayload !== 'object') {
    return null
  }

  const record = unwrappedPayload as Record<string, unknown>
  const candidate =
    record.token ?? record.accessToken ?? record.access_token ?? record.jwt ?? record.idToken

  return typeof candidate === 'string' && candidate.trim().length > 0 ? candidate : null
}

async function tryRefreshToken(): Promise<string | null> {
  const sessionRef = readStoredSession()
  const refreshToken = sessionRef?.session.refreshToken

  if (!sessionRef || !refreshToken) {
    return null
  }

  const response = await fetch('/api/auth/refresh', {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ refreshToken }),
  })

  if (!response.ok) {
    return null
  }

  const responseBody = await parseResponseBody(response)
  const accessToken = readAccessToken(responseBody)

  if (!accessToken) {
    return null
  }

  sessionRef.storage.setItem(
    AUTH_SESSION_KEY,
    JSON.stringify({
      ...sessionRef.session,
      token: accessToken,
    }),
  )

  return accessToken
}

function buildHeaders(options: RequestInit, token: string | null): Headers {
  const headers = new Headers(options.headers)

  if (token && !headers.has('Authorization')) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  const hasBody = options.body !== undefined && options.body !== null
  const isFormData = typeof FormData !== 'undefined' && options.body instanceof FormData

  if (hasBody && !isFormData && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  return headers
}

export async function jsonApiFetch(url: string, options: JsonApiFetchOptions = {}) {
  console.log(url)

  const { skipAuthRefresh = false, ...requestOptions } = options
  const token = readStoredSession()?.session.token ?? null

  let response = await fetch(url, {
    ...requestOptions,
    headers: buildHeaders(requestOptions, token),
  })

  if (response.status === 403 && !skipAuthRefresh) {
    const newToken = await tryRefreshToken()

    if (!newToken) {
      clearStoredSession()

      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }

      return response
    }

    response = await fetch(url, {
      ...requestOptions,
      headers: buildHeaders(requestOptions, newToken),
    })
  }

  return response
}
