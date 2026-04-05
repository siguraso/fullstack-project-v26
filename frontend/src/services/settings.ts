import { getAuthSession } from '@/services/auth'

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '/api').replace(/\/$/, '')

type ApiEnvelope<T> = {
  success?: boolean
  message?: string | null
  error?: string | null
  detail?: string | null
  data?: T
}

export interface Tenant {
  id: number
  name: string
  org_number: string
  address: string
  city: string
  country: string
  contact_email: string
  contact_phone: string
  active: boolean
  created_at: string
  updated_at: string
}

export interface TenantUpdatePayload {
  name: string
  orgNumber: string
  address: string
  city: string
  country: string
  contactEmail: string
  contactPhone: string
}

export interface User {
  id: number
  tenantId: number
  firstName: string
  lastName: string
  username: string
  email: string
  phone: string
  role: string
  active: boolean
}

export interface UserUpdatePayload {
  firstName: string
  lastName: string
  email: string
  phone: string
  role: string
}

function buildHeaders(contentType = false): HeadersInit {
  const session = getAuthSession()
  const headers: Record<string, string> = {}

  if (contentType) {
    headers['Content-Type'] = 'application/json'
  }

  if (session?.token) {
    headers.Authorization = `Bearer ${session.token}`
  }

  return headers
}

function unwrap<T>(payload: T | ApiEnvelope<T>): T {
  if (payload && typeof payload === 'object' && 'data' in payload) {
    return (payload as ApiEnvelope<T>).data as T
  }

  return payload as T
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

function readErrorMessage(payload: unknown, fallback: string): string {
  if (!payload || typeof payload !== 'object') {
    return fallback
  }

  const record = payload as Record<string, unknown>
  const candidate = record.message ?? record.error ?? record.detail

  return typeof candidate === 'string' && candidate.trim().length > 0 ? candidate : fallback
}

async function request<T>(path: string, init?: RequestInit, fallbackError?: string): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    credentials: 'include',
    ...init,
  })
  const payload = await parseResponseBody(response)

  if (!response.ok) {
    throw new Error(readErrorMessage(payload, fallbackError ?? 'Request failed.'))
  }

  return unwrap<T>(payload as T | ApiEnvelope<T>)
}

export async function getCurrentTenant(): Promise<Tenant> {
  return request<Tenant>('/tenants/current', {
    headers: buildHeaders(),
  }, 'Unable to load workspace details.')
}

export async function updateCurrentTenant(payload: TenantUpdatePayload): Promise<Tenant> {
  const response = await request<Tenant | null>(
    '/tenants/current',
    {
      method: 'PUT',
      headers: buildHeaders(true),
      body: JSON.stringify(payload),
    },
    'Unable to save workspace details.',
  )

  if (response && typeof response === 'object') {
    return response as Tenant
  }

  return getCurrentTenant()
}

export async function getUsers(): Promise<User[]> {
  const users = await request<User[]>(
    '/users',
    {
      headers: buildHeaders(),
    },
    'Unable to load staff members.',
  )

  return Array.isArray(users) ? users : []
}

export async function getUser(id: number): Promise<User> {
  return request<User>(
    `/users/${id}`,
    {
      headers: buildHeaders(),
    },
    'Unable to load staff member details.',
  )
}

export async function updateUser(id: number, payload: UserUpdatePayload): Promise<User> {
  const response = await request<User | null>(
    `/users/${id}`,
    {
      method: 'PUT',
      headers: buildHeaders(true),
      body: JSON.stringify(payload),
    },
    'Unable to update staff member.',
  )

  if (response && typeof response === 'object') {
    return response as User
  }

  return getUser(id)
}

export async function deactivateUser(id: number): Promise<void> {
  await request<null>(
    `/users/${id}`,
    {
      method: 'DELETE',
      headers: buildHeaders(),
    },
    'Unable to deactivate staff member.',
  )
}
