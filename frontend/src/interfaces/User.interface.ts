export interface User {
  id: number
  tenantId: number
  firstName: string
  lastName: string
  username: string
  email: string
  phone: string
  role: string
  active: boolean
}

export interface UserUpdatePayload {
  firstName: string
  lastName: string
  email: string
  phone: string
  role: string
}
