import { apiFetch } from "./apiHelper"
import { getAuthSession } from "./auth"



export async function getDashboardOverview() {
  const res = await apiFetch('/api/dashboard/overview')
  
    if (!res.ok) {
      console.error('Dashboard failed:', res.status)
    return null
    } 
  
    const json = await res.json()
    return json.data
  }