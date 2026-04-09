import type { AlcoholLog, AlcoholLogInput } from '@/interfaces/AlcoholLog.interface'
import { getAuthSession } from '@/services/auth'
import { parseJsonSafely, unwrap } from './util/util'
import type { ApiEnvelope } from './util/util'
import { apiFetch } from './util/apiHelper'

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '/api').replace(/\/$/, '')
const API_URL = `${API_BASE_URL}/ikalcohol/logs`
const REQUEST_TIMEOUT_MS = 10000

interface AlcoholLogResponseData {
  id: number
  title: string
  description?: string | null
  logType: string
  status?: string | null
  idChecked?: boolean | null
  serviceRefused?: boolean | null
  estimatedAge?: number | null
  recordedById?: number
  recordedByName?: string
  recordedByRole?: string
  recordedAt?: string
  createdAt?: string
  updatedAt?: string
}

function normalizeAlcoholLog(log: AlcoholLogResponseData): AlcoholLog | null {
  if (!log.id || !log.logType) {
    return null
  }

  return {
    id: log.id,
    title: log.title,
    description: log.description ?? null,
    logType: log.logType as any,
    status: log.status as any,
    idChecked: log.idChecked ?? null,
    serviceRefused: log.serviceRefused ?? null,
    estimatedAge: log.estimatedAge ?? null,
    recordedById: log.recordedById,
    recordedByName: log.recordedByName,
    recordedByRole: log.recordedByRole,
    recordedAt: log.recordedAt,
    createdAt: log.createdAt,
    updatedAt: log.updatedAt,
  }
}

export async function fetchAlcoholLogs(): Promise<AlcoholLog[]> {
  const token = getAuthSession()?.token

  const controller = new AbortController()
  const timeoutId = window.setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS)

  let response: Response

  try {
    response = await apiFetch(API_URL, {
      credentials: 'include',
      signal: controller.signal,
    })
  } catch (error) {
    if (error instanceof DOMException && error.name === 'AbortError') {
      throw new Error('Alcohol logs request timed out. Please try again.')
    }

    throw error
  } finally {
    window.clearTimeout(timeoutId)
  }

  if (!response.ok) {
    throw new Error(`Failed to fetch alcohol logs: ${response.status} ${response.statusText}`)
  }

  const data = await parseJsonSafely(response)

  const unwrapped = data
    ? unwrap<Array<AlcoholLogResponseData>>(data as ApiEnvelope<Array<AlcoholLogResponseData>>)
    : null

  if (!Array.isArray(unwrapped)) {
    return []
  }

  return unwrapped
    .map((item) => normalizeAlcoholLog(item))
    .filter((item): item is AlcoholLog => item !== null)
}

export async function createAlcoholLog(payload: AlcoholLogInput): Promise<AlcoholLog> {
  const controller = new AbortController()
  const timeoutId = window.setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS)

  let response: Response

  try {
    response = await apiFetch(API_URL, {
      method: 'POST',
      credentials: 'include',
      body: JSON.stringify(payload),
      signal: controller.signal,
    })
  } catch (error) {
    if (error instanceof DOMException && error.name === 'AbortError') {
      throw new Error('Create alcohol log request timed out. Please try again.')
    }

    throw error
  } finally {
    window.clearTimeout(timeoutId)
  }

  if (!response.ok) {
    throw new Error(`Failed to create alcohol log: ${response.status} ${response.statusText}`)
  }

  const data = await parseJsonSafely(response)

  const unwrapped = data
    ? unwrap<AlcoholLogResponseData>(data as ApiEnvelope<AlcoholLogResponseData>)
    : null

  if (!unwrapped) {
    throw new Error('Invalid response from server')
  }

  const normalized = normalizeAlcoholLog(unwrapped)

  if (!normalized) {
    throw new Error('Failed to normalize alcohol log response')
  }

  return normalized
}
