import type { Deviation } from '@/interfaces/Deviation.interface'
import { apiFetch } from './util/apiHelper'
import { unwrap, parseJsonSafely } from './util/util'
import type { ApiEnvelope } from './util/util'

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? '/api').replace(/\/$/, '')
const API = `${API_BASE_URL}/deviations`

type DeviationModule = Deviation['module']

function normalizeModule(value: unknown): DeviationModule | undefined {
  if (value === 'IK_FOOD' || value === 'IK-MAT' || value === 'IK_MAT') {
    return 'IK_FOOD'
  }

  if (value === 'IK_ALCOHOL' || value === 'IK-ALKOHOL' || value === 'IK_ALKOHOL') {
    return 'IK_ALCOHOL'
  }

  return undefined
}

function normalizeDeviation(raw: unknown, fallbackModule?: DeviationModule): Deviation | null {
  if (!raw || typeof raw !== 'object') {
    return null
  }

  const record = raw as Record<string, unknown>
  const module = normalizeModule(record.module) ?? fallbackModule

  if (!module) {
    return null
  }

  return {
    ...(record as unknown as Deviation),
    module,
  }
}

export async function getDeviations(): Promise<Deviation[]> {
  const res = await apiFetch(API)
  const payload = await parseJsonSafely(res)
  const unwrapped = payload ? unwrap<unknown>(payload as ApiEnvelope<unknown>) : null

  if (!Array.isArray(unwrapped)) {
    return []
  }

  return unwrapped
    .map((item) => normalizeDeviation(item))
    .filter((item): item is Deviation => item !== null)
}

export async function createDeviation(data: Deviation): Promise<Deviation | null> {
  const res = await apiFetch(API, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
  const payload = await parseJsonSafely(res)
  const unwrapped = payload ? unwrap<unknown>(payload as ApiEnvelope<unknown>) : null

  if (!res.ok) {
    return null
  }

  return normalizeDeviation(unwrapped, data.module)
}

export async function updateDeviation(
  id: number,
  data: { status?: 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' },
): Promise<Deviation | null> {
  const res = await apiFetch(`${API}/${id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  })

  const payload = await parseJsonSafely(res)
  const unwrapped = payload ? unwrap<unknown>(payload as ApiEnvelope<unknown>) : null

  if (!res.ok) {
    return null
  }

  return normalizeDeviation(unwrapped)
}

export async function resolveDeviation(id: number) {
  return updateDeviation(id, { status: 'RESOLVED' })
}
