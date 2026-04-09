import { jsonApiFetch } from './util/apiHelper'

export async function getDashboardOverview() {
  const res = await jsonApiFetch('/api/dashboard/overview')

  if (!res.ok) {
    console.error('Dashboard failed:', res.status)
    return null
  }

  const json = await res.json()
  return json.data
}
