const AUTH_SESSION_KEY = 'regula.auth.session'
const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '/api').replace(/\/$/, '')

type UserRole = 'ADMIN' | 'MANAGER' | 'STAFF'

export interface LoginPayload {
  email: string
  password: string
}

export interface RegisterPayload {
  email: string
  password: string
  firstName: string
  lastName: string
  orgNumber?: string
  phone?: string
  inviteToken?: string
}

export interface AuthSession {
  email: string
  remember: boolean
  token: string | null
  refreshToken: string | null
}

interface LoginResponseData {
  accessToken?: string
  refreshToken?: string
  email?: string
  fullName?: string
  organizationId?: number
  role?: UserRole
}

function getStorage(remember: boolean): Storage {
  return remember ? window.localStorage : window.sessionStorage
}

function parseSession(value: string | null): AuthSession | null {
  if (!value) {
    return null
  }

  try {
    return JSON.parse(value) as AuthSession
  } catch {
    return null
  }
}

function readResponseMessage(payload: unknown): string | null {
  if (!payload || typeof payload !== 'object') {
    return null
  }

  const record = payload as Record<string, unknown>
  const candidate = record.message ?? record.error ?? record.detail

  return typeof candidate === 'string' && candidate.trim().length > 0 ? candidate : null
}

function unwrapApiResponse(payload: unknown): unknown {
  if (!payload || typeof payload !== 'object') {
    return payload
  }

  const record = payload as Record<string, unknown>

  if ('data' in record) {
    return record.data
  }

  return payload
}

function readToken(payload: unknown): string | null {
  const unwrappedPayload = unwrapApiResponse(payload)

  if (!unwrappedPayload || typeof unwrappedPayload !== 'object') {
    return null
  }

  const record = unwrappedPayload as Record<string, unknown>
  const candidate =
    record.token ?? record.accessToken ?? record.access_token ?? record.jwt ?? record.idToken

  return typeof candidate === 'string' && candidate.trim().length > 0 ? candidate : null
}

function readRole(payload: unknown): UserRole | null {
  const unwrappedPayload = unwrapApiResponse(payload)

  if (!unwrappedPayload || typeof unwrappedPayload !== 'object') {
    return null
  }

  const record = unwrappedPayload as Record<string, unknown>
  const candidate = record.role

  if (candidate === 'ADMIN' || candidate === 'MANAGER' || candidate === 'STAFF') {
    return candidate
  }

  return null
}

function readLoginResponseData(payload: unknown): LoginResponseData | null {
  const unwrappedPayload = unwrapApiResponse(payload)

  if (!unwrappedPayload || typeof unwrappedPayload !== 'object') {
    return null
  }

  return unwrappedPayload as LoginResponseData
}

async function parseResponseBody(response: Response): Promise<unknown> {
  const contentType = response.headers.get('content-type') ?? ''

  if (contentType.includes('application/json')) {
    return response.json()
  }

  const text = await response.text()

  return text.length > 0 ? { message: text } : null
}

function persistSession(session: AuthSession) {
  window.localStorage.removeItem(AUTH_SESSION_KEY)
  window.sessionStorage.removeItem(AUTH_SESSION_KEY)
  getStorage(session.remember).setItem(AUTH_SESSION_KEY, JSON.stringify(session))
}

function createSession(email: string, remember: boolean, responseBody: unknown): AuthSession {
  const data = unwrapApiResponse(responseBody) as any

  return {
    email,
    remember,
    token: data?.accessToken ?? null,
    refreshToken: data?.refreshToken ?? null,
  }
}

export function getAuthSession(): AuthSession | null {
  return (
    parseSession(window.localStorage.getItem(AUTH_SESSION_KEY)) ??
    parseSession(window.sessionStorage.getItem(AUTH_SESSION_KEY))
  )
}

export function isAuthenticated(): boolean {
  return getAuthSession() !== null
}

export function clearAuthSession() {
  window.localStorage.removeItem(AUTH_SESSION_KEY)
  window.sessionStorage.removeItem(AUTH_SESSION_KEY)
}

export async function login(payload: LoginPayload, remember: boolean): Promise<AuthSession> {
  const response = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
    body: JSON.stringify(payload),
  })

  const responseBody = await parseResponseBody(response)

  if (!response.ok) {
    throw new Error(
      readResponseMessage(responseBody) ?? 'Unable to sign in with the provided credentials.',
    )
  }

  const session = createSession(payload.email, remember, responseBody)

  persistSession(session)

  return session
}

export async function register(payload: RegisterPayload, remember: boolean): Promise<AuthSession> {
  const response = await fetch(`${API_BASE_URL}/auth/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
    body: JSON.stringify(payload),
  })

  const responseBody = await parseResponseBody(response)

  if (!response.ok) {
    throw new Error(readResponseMessage(responseBody) ?? 'Unable to create your account right now.')
  }

  const session = createSession(payload.email, remember, responseBody)

  persistSession(session)

  return session
}

export async function refreshToken() {
  const session = getAuthSession()
  const refreshToken = session?.refreshToken

  if (!refreshToken) return null

  const res = await fetch('/api/auth/refresh', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ refreshToken }),
  })

  if (!res.ok) return null

  const json = await res.json()
  return json.data.accessToken
}