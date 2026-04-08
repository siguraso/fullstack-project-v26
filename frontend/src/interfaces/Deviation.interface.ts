export interface Deviation {
  id?: number
  title: string
  category: 'TEMPERATURE' | 'HYGIENE' | 'ALCOHOL' | 'DOCUMENTATION' | 'OTHER'
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
  module: 'IK_FOOD' | 'IK_ALCOHOL'
  status: 'OPEN' | 'IN_PROGRESS' | 'RESOLVED'
  description: string
  createdAt?: string
}
