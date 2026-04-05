import type { Deviation } from '@/stores/deviation'
import { unwrap, parseJsonSafely } from './util/util'
import type { ApiEnvelope } from './util/util'

const API = 'http://localhost:8080/api/deviations'
const DEFAULT_TENANT_ID = Number(import.meta.env.VITE_TENANT_ID ?? 1)

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

export async function getDeviations(tenantId = DEFAULT_TENANT_ID): Promise<Deviation[]> {
  const res = await fetch(`${API}?tenantId=${tenantId}`)
  const payload = await parseJsonSafely(res)
  const unwrapped = payload ? unwrap<unknown>(payload as ApiEnvelope<unknown>) : null

  if (!Array.isArray(unwrapped)) {
    return []
  }

  return unwrapped
    .map((item) => normalizeDeviation(item))
    .filter((item): item is Deviation => item !== null)
}

export async function createDeviation(
  data: Deviation,
  tenantId = DEFAULT_TENANT_ID,
): Promise<Deviation | null> {
  const res = await fetch(API, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ ...data, tenantId }),
  })
  const payload = await parseJsonSafely(res)
  const unwrapped = payload ? unwrap<unknown>(payload as ApiEnvelope<unknown>) : null

  if (!res.ok) {
    return null
  }

  return normalizeDeviation(unwrapped, data.module)
}
