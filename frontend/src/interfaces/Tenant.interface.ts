export interface Tenant {
  id: number
  name: string
  org_number: string
  address: string
  city: string
  country: string
  contact_email: string
  contact_phone: string
  active: boolean
  created_at: string
  updated_at: string
}

export interface TenantUpdatePayload {
  name: string
  orgNumber: string
  address: string
  city: string
  country: string
  contactEmail: string
  contactPhone: string
}
