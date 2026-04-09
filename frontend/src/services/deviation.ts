import type { CreateDeviationInput, Deviation } from '@/interfaces/Deviation.interface'
import { jsonApiFetch } from './util/apiHelper'
import { assertOk, parseResponseBody, unwrapMaybeEnvelope } from './util/util'

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
  const response = await jsonApiFetch(API)
  await assertOk(response, 'Failed to load deviations.')

  const payload = await parseResponseBody(response)
  const unwrapped = unwrapMaybeEnvelope<unknown>(payload)

  if (!Array.isArray(unwrapped)) {
    return []
  }

  return unwrapped
    .map((item) => normalizeDeviation(item))
    .filter((item): item is Deviation => item !== null)
}

export async function createDeviation(data: CreateDeviationInput): Promise<Deviation> {
  const response = await jsonApiFetch(API, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
  await assertOk(response, 'Failed to create deviation.')

  const payload = await parseResponseBody(response)
  const unwrapped = unwrapMaybeEnvelope<unknown>(payload)
  const normalized = normalizeDeviation(unwrapped, data.module)

  if (!normalized) {
    throw new Error('Received malformed deviation payload from server.')
  }

  return normalized
}

export async function updateDeviation(
  id: number,
  data: { status?: 'OPEN' | 'IN_PROGRESS' | 'RESOLVED' },
): Promise<Deviation> {
  const response = await jsonApiFetch(`${API}/${id}`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  })
  await assertOk(response, 'Failed to update deviation.')

  const payload = await parseResponseBody(response)
  const unwrapped = unwrapMaybeEnvelope<unknown>(payload)
  const normalized = normalizeDeviation(unwrapped)

  if (!normalized) {
    throw new Error('Received malformed deviation payload from server.')
  }

  return normalized
}

export async function resolveDeviation(id: number) {
  return updateDeviation(id, { status: 'RESOLVED' })
}
