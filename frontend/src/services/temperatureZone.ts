import type { TemperatureZone } from '@/types/temperature-zone'
import { getAuthSession } from '@/services/auth'
import { parseJsonSafely, unwrap } from './util/util'
import type { ApiEnvelope } from './util/util'

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '/api').replace(/\/$/, '')
const API_URL = `${API_BASE_URL}/ikfood/temperature-zones`
const REQUEST_TIMEOUT_MS = 10000

export async function getTemperatureZones(): Promise<Array<TemperatureZone>> {
  const token = getAuthSession()?.token

  const controller = new AbortController()
  const timeoutId = window.setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS)

  let response: Response

  try {
    response = await fetch(API_URL, {
      headers: token ? { Authorization: `Bearer ${token}` } : undefined,
      credentials: 'include',
      signal: controller.signal,
    })
  } catch (error) {
    if (error instanceof DOMException && error.name === 'AbortError') {
      throw new Error('Temperature zones request timed out. Please try again.')
    }

    throw error
  } finally {
    window.clearTimeout(timeoutId)
  }

  if (!response.ok) {
    throw new Error(`Failed to fetch temperature zones: ${response.status} ${response.statusText}`)
  }

  const data = await parseJsonSafely(response)

  const unwrapped = data
    ? unwrap<Array<TemperatureZone>>(data as ApiEnvelope<Array<TemperatureZone>>)
    : null

  if (!Array.isArray(unwrapped)) {
    return []
  }

  return unwrapped
    .map((item) => normalizeTemperatureZone(item))
    .filter((item): item is TemperatureZone => item !== null)
}

export async function addTemperatureZone(
  data: Omit<TemperatureZone, 'id'>,
): Promise<TemperatureZone> {
  const token = getAuthSession()?.token

  const response = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    credentials: 'include',
    body: JSON.stringify(data),
  })

  if (!response.ok) {
    throw new Error(`Failed to add temperature zone: ${response.status} ${response.statusText}`)
  }

  const responseData = await parseJsonSafely(response)

  const unwrapped = responseData
    ? unwrap<TemperatureZone>(responseData as ApiEnvelope<TemperatureZone>)
    : null

  if (!unwrapped) {
    throw new Error('Invalid response format when adding temperature zone.')
  }

  const normalized = normalizeTemperatureZone(unwrapped)

  if (!normalized) {
    throw new Error('Received malformed temperature zone data from server.')
  }

  return normalized
}

export async function editTemperatureZone(
  id: number,
  data: Omit<TemperatureZone, 'id'>,
): Promise<TemperatureZone> {
  const token = getAuthSession()?.token

  const response = await fetch(`${API_URL}/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    credentials: 'include',
    body: JSON.stringify(data),
  })

  if (!response.ok) {
    throw new Error(`Failed to edit temperature zone: ${response.status} ${response.statusText}`)
  }

  const responseData = await parseJsonSafely(response)

  const unwrapped = responseData
    ? unwrap<TemperatureZone>(responseData as ApiEnvelope<TemperatureZone>)
    : null

  if (!unwrapped) {
    throw new Error('Invalid response format when editing temperature zone.')
  }

  const normalized = normalizeTemperatureZone(unwrapped)

  if (!normalized) {
    throw new Error('Received malformed temperature zone data from server.')
  }

  return normalized
}

export async function deleteTemperatureZone(id: number): Promise<void> {
  const token = getAuthSession()?.token

  const response = await fetch(`${API_URL}/${id}`, {
    method: 'DELETE',
    headers: token ? { Authorization: `Bearer ${token}` } : undefined,
    credentials: 'include',
  })

  if (!response.ok) {
    throw new Error(`Failed to delete temperature zone: ${response.status} ${response.statusText}`)
  }
}

function normalizeTemperatureZone(raw: unknown): TemperatureZone | null {
  if (!raw || typeof raw !== 'object') {
    return null
  }

  const record = raw as Record<string, unknown>

  if (
    typeof record.id !== 'number' ||
    typeof record.name !== 'string' ||
    typeof record.lowerLimitCelsius !== 'number' ||
    typeof record.upperLimitCelsius !== 'number'
  ) {
    return null
  }

  return {
    id: record.id,
    name: record.name,
    lowerLimitCelsius: record.lowerLimitCelsius,
    upperLimitCelsius: record.upperLimitCelsius,
    active: typeof record.active === 'boolean' ? record.active : true,
  }
}
