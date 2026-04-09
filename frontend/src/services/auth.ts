import type {
  AuthSession,
  LoginPayload,
  RegisterPayload,
  UserRole,
} from '@/interfaces/Auth.interface'
import { jsonApiFetch } from './util/apiHelper'
import { parseResponseBody, readErrorMessage, unwrapMaybeEnvelope } from './util/util'

export type { AuthSession, LoginPayload, RegisterPayload, UserRole }

const AUTH_SESSION_KEY = 'regula.auth.session'
const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '/api').replace(/\/$/, '')

interface AuthLoginResponseData {
  accessToken?: string
  refreshToken?: string
  email?: string
  fullName?: string
  organizationId?: number
  role?: UserRole
}

interface RefreshTokenResponseData {
  accessToken?: string
}

function getStorage(remember: boolean): Storage {
  return remember ? window.localStorage : window.sessionStorage
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

function readToken(payload: unknown): string | null {
  const unwrappedPayload = unwrapMaybeEnvelope<unknown>(payload)

  if (!unwrappedPayload || typeof unwrappedPayload !== 'object') {
    return null
  }

  const record = unwrappedPayload as Record<string, unknown>
  const candidate =
    record.token ?? record.accessToken ?? record.access_token ?? record.jwt ?? record.idToken

  return typeof candidate === 'string' && candidate.trim().length > 0 ? candidate : null
}

function readRole(payload: unknown): UserRole | null {
  const unwrappedPayload = unwrapMaybeEnvelope<unknown>(payload)

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

function readLoginResponseData(payload: unknown): AuthLoginResponseData | null {
  const unwrappedPayload = unwrapMaybeEnvelope<unknown>(payload)

  if (!unwrappedPayload || typeof unwrappedPayload !== 'object') {
    return null
  }

  return unwrappedPayload as AuthLoginResponseData
}

function readRefreshTokenResponseData(payload: unknown): RefreshTokenResponseData | null {
  const unwrappedPayload = unwrapMaybeEnvelope<unknown>(payload)

  if (!unwrappedPayload || typeof unwrappedPayload !== 'object') {
    return null
  }

  return unwrappedPayload as RefreshTokenResponseData
}

function persistSession(session: AuthSession) {
  window.localStorage.removeItem(AUTH_SESSION_KEY)
  window.sessionStorage.removeItem(AUTH_SESSION_KEY)
  getStorage(session.remember).setItem(AUTH_SESSION_KEY, JSON.stringify(session))
}

function createSession(email: string, remember: boolean, responseBody: unknown): AuthSession {
  const data = readLoginResponseData(responseBody)

  return {
    email,
    remember,
    token: data?.accessToken ?? readToken(responseBody),
    refreshToken: data?.refreshToken ?? null,
    role: readRole(responseBody),
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
  const response = await jsonApiFetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    credentials: 'include',
    body: JSON.stringify(payload),
  })

  const responseBody = await parseResponseBody(response)

  if (!response.ok) {
    throw new Error(
      readErrorMessage(responseBody, 'Unable to sign in with the provided credentials.'),
    )
  }

  const session = createSession(payload.email, remember, responseBody)

  persistSession(session)

  return session
}

export async function register(payload: RegisterPayload, remember: boolean): Promise<AuthSession> {
  const response = await jsonApiFetch(`${API_BASE_URL}/auth/register`, {
    method: 'POST',
    credentials: 'include',
    body: JSON.stringify(payload),
  })

  const responseBody = await parseResponseBody(response)

  if (!response.ok) {
    throw new Error(readErrorMessage(responseBody, 'Unable to create your account right now.'))
  }

  const session = createSession(payload.email, remember, responseBody)

  persistSession(session)

  return session
}

export async function refreshToken() {
  const session = getAuthSession()
  const refreshToken = session?.refreshToken

  if (!refreshToken) return null

  const res = await jsonApiFetch('/api/auth/refresh', {
    method: 'POST',
    body: JSON.stringify({ refreshToken }),
    skipAuthRefresh: true,
  })

  if (!res.ok) return null

  const responseBody = await parseResponseBody(res)
  const data = readRefreshTokenResponseData(responseBody)

  return data?.accessToken ?? null
}
