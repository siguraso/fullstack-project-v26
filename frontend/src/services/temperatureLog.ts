import type { TemperatureLog, TemperatureLogInput } from '@/interfaces/TemperatureLog.interface'
import { getAuthSession } from '@/services/auth'
import { parseJsonSafely, unwrap } from './util/util'
import type { ApiEnvelope } from './util/util'

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '/api').replace(/\/$/, '')
const API_URL = `${API_BASE_URL}/ikfood/temperature-logs`
const REQUEST_TIMEOUT_MS = 10000

export async function fetchTemperatureLogs(): Promise<TemperatureLog[]> {
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
      throw new Error('Temperature logs request timed out. Please try again.')
    }

    throw error
  } finally {
    window.clearTimeout(timeoutId)
  }

  if (!response.ok) {
    throw new Error(`Failed to fetch temperature logs: ${response.status} ${response.statusText}`)
  }

  const data = await parseJsonSafely(response)

  const unwrapped = data
    ? unwrap<Array<TemperatureLogResponse>>(data as ApiEnvelope<Array<TemperatureLogResponse>>)
    : null

  if (!Array.isArray(unwrapped)) {
    return []
  }

  return unwrapped
    .map((item) => normalizeTemperatureLog(item))
    .filter((item): item is TemperatureLog => item !== null)
}

export async function getTemperatureLogById(id: number): Promise<TemperatureLog> {
  const token = getAuthSession()?.token

  const response = await fetch(`${API_URL}/${id}`, {
    headers: token ? { Authorization: `Bearer ${token}` } : undefined,
    credentials: 'include',
  })

  if (!response.ok) {
    throw new Error(`Failed to fetch temperature log: ${response.status} ${response.statusText}`)
  }

  const responseData = await parseJsonSafely(response)

  const unwrapped = responseData
    ? unwrap<TemperatureLogResponse>(responseData as ApiEnvelope<TemperatureLogResponse>)
    : null

  const normalized = normalizeTemperatureLog(unwrapped)

  if (!normalized) {
    throw new Error('Received malformed temperature log data from server.')
  }

  return normalized
}

export async function createTemperatureLog(data: TemperatureLogInput): Promise<TemperatureLog> {
  const token = getAuthSession()?.token

  const response = await fetch(API_URL, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    credentials: 'include',
    body: JSON.stringify({
      temperatureZoneId: data.temperatureZoneId,
      temperatureCelsius: data.temperatureCelsius,
      note: data.note ?? null,
    }),
  })

  if (!response.ok) {
    throw new Error(`Failed to create temperature log: ${response.status} ${response.statusText}`)
  }

  const responseData = await parseJsonSafely(response)

  const unwrapped = responseData
    ? unwrap<TemperatureLogResponse>(responseData as ApiEnvelope<TemperatureLogResponse>)
    : null

  const normalized = normalizeTemperatureLog(unwrapped)

  if (!normalized) {
    throw new Error('Invalid response format when creating temperature log.')
  }

  return normalized
}

export async function deleteTemperatureLog(id: number): Promise<void> {
  const token = getAuthSession()?.token

  const response = await fetch(`${API_URL}/${id}`, {
    method: 'DELETE',
    headers: token ? { Authorization: `Bearer ${token}` } : undefined,
    credentials: 'include',
  })

  if (!response.ok) {
    throw new Error(`Failed to delete temperature log: ${response.status} ${response.statusText}`)
  }
}

type TemperatureLogResponse = {
  id: number
  recordedById?: number
  recordedByName?: string
  recordedByEmail?: string
  recordedByRole?: string
  temperatureZoneId: number
  temperatureZoneName?: string
  lowerLimitCelsius?: number
  upperLimitCelsius?: number
  temperatureCelsius: number
  note?: string | null
  recordedAt?: string
  timestamp?: string
  deviationCreated?: boolean
}

function normalizeTemperatureLog(raw: unknown): TemperatureLog | null {
  if (!raw || typeof raw !== 'object') {
    return null
  }

  const record = raw as Record<string, unknown>

  if (
    typeof record.id !== 'number' ||
    typeof record.temperatureZoneId !== 'number' ||
    typeof record.temperatureCelsius !== 'number'
  ) {
    return null
  }

  const timestampCandidate =
    typeof record.recordedAt === 'string'
      ? record.recordedAt
      : typeof record.timestamp === 'string'
        ? record.timestamp
        : null

  return {
    id: record.id,
    temperatureZoneId: record.temperatureZoneId,
    temperatureZoneName:
      typeof record.temperatureZoneName === 'string' ? record.temperatureZoneName : undefined,
    temperatureCelsius: record.temperatureCelsius,
    timestamp: timestampCandidate ?? new Date().toISOString(),
    note: typeof record.note === 'string' ? record.note : null,
    recordedById: typeof record.recordedById === 'number' ? record.recordedById : undefined,
    recordedByName: typeof record.recordedByName === 'string' ? record.recordedByName : undefined,
    recordedByEmail:
      typeof record.recordedByEmail === 'string' ? record.recordedByEmail : undefined,
    recordedByRole: typeof record.recordedByRole === 'string' ? record.recordedByRole : undefined,
    lowerLimitCelsius:
      typeof record.lowerLimitCelsius === 'number' ? record.lowerLimitCelsius : undefined,
    upperLimitCelsius:
      typeof record.upperLimitCelsius === 'number' ? record.upperLimitCelsius : undefined,
    deviationCreated:
      typeof record.deviationCreated === 'boolean' ? record.deviationCreated : undefined,
  }
}
