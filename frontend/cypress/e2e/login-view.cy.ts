/// <reference types="cypress" />

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

function visitLogin() {
  cy.visit('/login', {
    onBeforeLoad(win) {
      cy.stub(win, 'matchMedia').callsFake((query: string) => ({
        matches: query === '(prefers-reduced-motion: reduce)',
        media: query,
        onchange: null,
        addListener: () => {},
        removeListener: () => {},
        addEventListener: () => {},
        removeEventListener: () => {},
        dispatchEvent: () => false,
      }))
    },
  })
}

describe('Login view', () => {
  it('shows an error when sign-in fails', () => {
    cy.intercept('POST', '/api/auth/login', {
      statusCode: 401,
      body: {
        message: 'Invalid credentials.',
      },
    }).as('login')

    visitLogin()

    cy.get('input[type="email"]').type('admin@example.com')
    cy.get('input[type="password"]').type('wrong-password')
    cy.contains('button', 'Sign in').click()

    cy.wait('@login')
    cy.contains('.status-error', 'Invalid credentials.').should('be.visible')
    cy.location('pathname').should('eq', '/login')
  })

  it('signs in and redirects to the dashboard', () => {
    cy.intercept('POST', '/api/auth/login', (req) => {
      expect(req.body).to.deep.equal({
        email: 'admin@example.com',
        password: 'correct-horse-battery-staple',
      })

      req.reply({
        statusCode: 200,
        body: {
          data: {
            accessToken: 'test-access-token',
            refreshToken: 'test-refresh-token',
            role: 'ADMIN',
          },
        },
      })
    }).as('login')

    cy.intercept('GET', '/api/tenants/current', {
      body: { data: tenant },
    }).as('getTenant')

    cy.intercept('GET', '/api/dashboard/overview', {
      body: { data: dashboardOverview },
    }).as('getDashboardOverview')

    visitLogin()

    cy.get('input[type="email"]').type('admin@example.com')
    cy.get('input[type="password"]').type('correct-horse-battery-staple')
    cy.contains('button', 'Sign in').click()

    cy.wait('@login')
    cy.wait(['@getTenant', '@getDashboardOverview'])
    cy.location('pathname').should('eq', '/dashboard')
    cy.contains('h1', 'Dashboard').should('be.visible')
    cy.window().then((win) => {
      expect(win.sessionStorage.getItem('regula.auth.session')).to.contain('admin@example.com')
    })
  })
})
