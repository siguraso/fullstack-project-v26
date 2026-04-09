/// <reference types="cypress" />

const authSession = {
  email: 'admin@example.com',
  remember: true,
  token: 'test-access-token',
  refreshToken: 'test-refresh-token',
  role: 'ADMIN',
}

const tenant = {
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

const dashboardOverview = {
  criticalAlerts: [],
  checklistsToday: {
    completedItems: 0,
    totalItems: 0,
  },
  activeDeviations: [],
  pendingChecklists: [],
  teamActivity: [],
}

const users = [
  {
    id: 1,
    firstName: 'Ada',
    lastName: 'Admin',
    username: 'ada.admin',
    email: 'admin@example.com',
    phone: '12345678',
    role: 'ADMIN',
    active: true,
  },
]

const authenticatedRoutes = [
  { path: '/dashboard', heading: 'Dashboard' },
  { path: '/deviation', heading: 'Deviations' },
  { path: '/checklists', heading: 'Checklists' },
  { path: '/inspections', heading: 'Inspection Logs' },
  { path: '/settings', heading: 'Organisation Configuration' },
  { path: '/checklist-builder', heading: 'Checklist Management & Templates' },
  { path: '/temperature-logs', heading: 'Temperature Logs' },
  { path: '/alcohol-logs', heading: 'Alcohol Compliance Logs' },
]

const publicRoutes = [
  { path: '/login', heading: 'Welcome back' },
  { path: '/register', heading: 'Create your account' },
  { path: '/invite/accept?token=test-token', heading: 'Set up your staff account' },
]

function seedAuthSession(win: Window) {
  win.localStorage.setItem('regula.auth.session', JSON.stringify(authSession))
}

function stubResponsiveApis() {
  cy.intercept('GET', '/api/tenants/current', {
    body: { data: tenant },
  }).as('getTenant')

  cy.intercept('GET', '/api/dashboard/overview', {
    body: { data: dashboardOverview },
  }).as('getDashboardOverview')

  cy.intercept('GET', '/api/deviations', {
    body: { data: [] },
  }).as('getDeviations')

  cy.intercept('GET', '/api/checklists/today', {
    body: { data: [{ items: [] }] },
  }).as('getTodayChecklist')

  cy.intercept('GET', '/api/checklists/templates', {
    body: { data: [] },
  }).as('getChecklistTemplates')

  cy.intercept('GET', '/api/checklist-library', {
    body: { data: [] },
  }).as('getChecklistLibrary')

  cy.intercept('GET', '/api/checklist-library/*/in-use', {
    body: { data: false },
  }).as('getChecklistLibraryInUse')

  cy.intercept('GET', '/api/inspection/logs', {
    body: { data: [] },
  }).as('getInspectionLogs')

  cy.intercept('GET', '/api/users', {
    body: { data: users },
  }).as('getUsers')

  cy.intercept('GET', '/api/ikalcohol/logs', {
    body: { data: [] },
  }).as('getAlcoholLogs')

  cy.intercept('GET', '/api/ikfood/temperature-zones', {
    body: { data: [] },
  }).as('getTemperatureZones')

  cy.intercept('GET', '/api/ikfood/temperature-logs', {
    body: { data: [] },
  }).as('getTemperatureLogs')

  cy.intercept('GET', '/api/invitations/validate*', {
    body: {
      data: {
        valid: true,
        email: 'invite@example.com',
        organizationId: 1,
      },
    },
  }).as('validateInvite')
}

function expectNoPageOverflow() {
  cy.document().then((doc) => {
    expect(doc.documentElement.scrollWidth).to.be.lte(doc.documentElement.clientWidth + 1)
  })
}

function visitAuthenticatedRoute(path: string) {
  cy.visit(path, {
    onBeforeLoad(win) {
      seedAuthSession(win)
    },
  })
}

describe('Responsive route smoke', () => {
  beforeEach(() => {
    stubResponsiveApis()
  })

  it('opens and closes the mobile drawer on phone', () => {
    cy.viewport(390, 844)
    visitAuthenticatedRoute('/dashboard')

    cy.contains('h1', 'Dashboard').should('be.visible')
    cy.get('[aria-label="Toggle sidebar"]').click()
    cy.get('.sidebar-mobile.sidebar-open').should('exist')
    cy.get('[aria-label="Close navigation"]').click()
    cy.get('.sidebar-mobile.sidebar-closed').should('exist')
    expectNoPageOverflow()
  })

  it('renders all authenticated routes cleanly on phone width', () => {
    cy.viewport(390, 844)

    authenticatedRoutes.forEach((route) => {
      visitAuthenticatedRoute(route.path)
      cy.contains('h1', route.heading).should('be.visible')
      expectNoPageOverflow()
    })
  })

  it('renders the public routes cleanly on phone width', () => {
    cy.viewport(390, 844)

    publicRoutes.forEach((route) => {
      cy.visit(route.path)
      cy.contains('h1', route.heading).should('be.visible')
      expectNoPageOverflow()
    })
  })

  it('keeps the shell usable on tablet and desktop widths', () => {
    cy.viewport(768, 1024)
    visitAuthenticatedRoute('/dashboard')
    cy.get('[aria-label="Toggle sidebar"]').should('be.visible').click()
    cy.get('.sidebar-mobile.sidebar-open').should('exist')
    cy.get('.sidebar-backdrop').click({ force: true })
    cy.get('.sidebar-mobile.sidebar-closed').should('exist')

    cy.viewport(1280, 800)
    visitAuthenticatedRoute('/dashboard')
    cy.get('[aria-label="Toggle sidebar"]').should('not.exist')
    cy.get('.sidebar').should('be.visible')
  })
})
