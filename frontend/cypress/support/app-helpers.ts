/// <reference types="cypress" />

export const adminSession = {
  email: 'admin@example.com',
  remember: true,
  token: 'test-access-token',
  refreshToken: 'test-refresh-token',
  role: 'ADMIN',
} as const

export const defaultTenant = {
  id: 1,
  name: 'Test Kitchen',
  org_number: '123456789',
  address: 'Test Street 1',
  city: 'Trondheim',
  country: 'Norway',
  contact_email: 'admin@example.com',
  contact_phone: '12345678',
  active: true,
  created_at: '2026-01-01T00:00:00.000Z',
  updated_at: '2026-01-01T00:00:00.000Z',
}

export function seedAuthSession(win: Window, session = adminSession) {
  win.localStorage.setItem('regula.auth.session', JSON.stringify(session))
}

export function visitAuthenticatedRoute(path: string, session = adminSession) {
  cy.visit(path, {
    onBeforeLoad(win) {
      seedAuthSession(win, session)
    },
  })
}
