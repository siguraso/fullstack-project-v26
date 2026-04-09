import type { Tenant, TenantUpdatePayload } from '@/interfaces/Tenant.interface'
import type { User, UserUpdatePayload } from '@/interfaces/User.interface'
import { jsonApiFetch } from './util/apiHelper'
import { parseResponseBody, readErrorMessage, unwrap } from './util/util'
import type { ApiEnvelope } from './util/util'

export type { Tenant, TenantUpdatePayload } from '@/interfaces/Tenant.interface'

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '/api').replace(/\/$/, '')

function buildHeaders(contentType = false): HeadersInit {
  const headers: Record<string, string> = {}

  if (contentType) {
    headers['Content-Type'] = 'application/json'
  }

  return headers
}

async function request<T>(path: string, init?: RequestInit, fallbackError?: string): Promise<T> {
  const response = await jsonApiFetch(`${API_BASE_URL}${path}`, {
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
  return request<Tenant>(
    '/tenants/current',
    {
      headers: buildHeaders(),
    },
    'Unable to load workspace details.',
  )
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

export async function sendStaffInvite(email: string): Promise<void> {
  await request<null>(
    '/users/invite',
    {
      method: 'POST',
      headers: buildHeaders(true),
      body: JSON.stringify({ email }),
    },
    'Unable to send staff invite.',
  )
}
