export type UserRole = 'ADMIN' | 'MANAGER' | 'STAFF'

export interface LoginPayload {
  email: string
  password: string
}

export interface RegisterPayload {
  email: string
  password: string
  firstName: string
  lastName: string
  orgNumber?: string
  phone?: string
  inviteToken?: string
}

export interface AuthSession {
  email: string
  fullName: string | null
  remember: boolean
  token: string | null
  refreshToken: string | null
  role: UserRole | null
}
