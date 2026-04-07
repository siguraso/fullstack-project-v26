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

const zones = [
  {
    id: 1,
    name: 'Fridge',
    lowerLimitCelsius: 0,
    upperLimitCelsius: 5,
    active: true,
  },
  {
    id: 2,
    name: 'Freezer',
    lowerLimitCelsius: -24,
    upperLimitCelsius: -18,
    active: true,
  },
]

const logs = [
  {
    id: 101,
    temperatureZoneId: 1,
    temperatureZoneName: 'Fridge',
    temperatureCelsius: 2,
    recordedAt: '2026-03-10T08:15:00.000Z',
    note: 'Morning check',
    recordedByName: 'Ada Admin',
  },
  {
    id: 102,
    temperatureZoneId: 2,
    temperatureZoneName: 'Freezer',
    temperatureCelsius: -15,
    recordedAt: '2026-03-10T08:20:00.000Z',
    note: null,
    recordedByName: 'Mina Manager',
  },
]

function stubTenant() {
  cy.intercept('GET', '/api/tenants/current', {
    body: { data: tenant },
  }).as('getTenant')
}

function stubTemperatureData() {
  cy.intercept('GET', '/api/ikfood/temperature-zones', {
    body: { data: zones },
  }).as('getTemperatureZones')

  cy.intercept('GET', '/api/ikfood/temperature-logs', {
    body: { data: logs },
  }).as('getTemperatureLogs')
}

function visitTemperatureLogs() {
  cy.visit('/temperature-logs', {
    onBeforeLoad(win) {
      win.localStorage.setItem('regula.auth.session', JSON.stringify(authSession))
    },
  })

  cy.wait(['@getTemperatureZones', '@getTemperatureLogs'])
}

describe('TemperatureLogView', () => {
  beforeEach(() => {
    stubTenant()
  })

  it('loads zones and temperature log history', () => {
    stubTemperatureData()
    visitTemperatureLogs()

    cy.contains('h1', 'Temperature Logs').should('be.visible')
    cy.contains('.card', 'Create Temperature Log').within(() => {
      cy.contains('option', 'Fridge').should('exist')
      cy.contains('option', 'Freezer').should('exist')
    })

    cy.contains('.card', 'Temperature zones').within(() => {
      cy.contains('td', 'Fridge').should('be.visible')
      cy.contains('td', '0°C').should('be.visible')
      cy.contains('td', '5°C').should('be.visible')
      cy.contains('td', 'Freezer').should('be.visible')
      cy.contains('button', 'Edit').should('be.visible')
    })

    cy.contains('.card', 'Temperature Log History').within(() => {
      cy.contains('td', 'Fridge').should('be.visible')
      cy.contains('td', '2 °C').should('be.visible')
      cy.contains('td', 'Optimal').should('be.visible')
      cy.contains('td', 'Ada Admin').should('be.visible')
      cy.contains('td', 'Freezer').should('be.visible')
      cy.contains('td', '-15 °C').should('be.visible')
      cy.contains('td', 'Abnormal').should('be.visible')
      cy.contains('button', 'Delete').should('be.visible')
    })
  })

  it('filters temperature log history by zone', () => {
    stubTemperatureData()
    visitTemperatureLogs()

    cy.contains('.card', 'Temperature Log History').within(() => {
      cy.get('select').select('Freezer')
      cy.contains('td', 'Freezer').should('be.visible')
      cy.contains('td', '-15 °C').should('be.visible')
      cy.contains('td', 'Fridge').should('not.exist')
      cy.contains('td', '2 °C').should('not.exist')
    })
  })

  it('creates a temperature log and prepends it to the history', () => {
    const createdLog = {
      id: 103,
      temperatureZoneId: 1,
      temperatureZoneName: 'Fridge',
      temperatureCelsius: 4,
      recordedAt: '2026-03-10T09:00:00.000Z',
      note: 'After delivery',
      recordedByName: 'Ada Admin',
    }

    cy.intercept('POST', '/api/ikfood/temperature-logs', (req) => {
      expect(req.body).to.deep.equal({
        temperatureZoneId: 1,
        temperatureCelsius: 4,
        note: 'After delivery',
      })

      req.reply({
        statusCode: 201,
        body: { data: createdLog },
      })
    }).as('createTemperatureLog')

    stubTemperatureData()
    visitTemperatureLogs()

    cy.contains('.card', 'Create Temperature Log').within(() => {
      cy.get('select[aria-label="Filter deviations"]').select('Fridge')
      cy.get('input[placeholder="Enter temperature"]').type('4')
      cy.get('input[placeholder="Additional notes"]').type('After delivery')
      cy.contains('button', 'Create Log').click()
      cy.contains('Temperature log created successfully.').should('be.visible')
    })

    cy.wait('@createTemperatureLog')

    cy.contains('.card', 'Temperature Log History').within(() => {
      cy.get('tbody tr').first().should('contain', '4 °C')
      cy.contains('td', '4 °C').should('be.visible')
      cy.contains('td', 'Fridge').should('be.visible')
      cy.contains('td', 'Optimal').should('be.visible')
    })
  })

  it('deletes a temperature log after confirmation', () => {
    cy.intercept('DELETE', '/api/ikfood/temperature-logs/101', {
      statusCode: 204,
    }).as('deleteTemperatureLog')

    stubTemperatureData()
    visitTemperatureLogs()

    cy.contains('.card', 'Temperature Log History').within(() => {
      cy.contains('tr', '2 °C').within(() => {
        cy.contains('button', 'Delete').click()
      })
    })

    cy.contains('h3', 'Delete Temperature Log').should('be.visible')
    cy.contains('p', 'Temperature: 2 °C').should('be.visible')
    cy.contains('button', 'Delete Log').click()
    cy.wait('@deleteTemperatureLog')

    cy.contains('.card', 'Temperature Log History').within(() => {
      cy.contains('td', '2 °C').should('not.exist')
      cy.contains('td', '-15 °C').should('be.visible')
    })
  })

  it('shows load errors from the temperature APIs', () => {
    cy.intercept('GET', '/api/ikfood/temperature-zones', {
      statusCode: 500,
      statusMessage: 'Internal Server Error',
    }).as('getTemperatureZones')

    cy.intercept('GET', '/api/ikfood/temperature-logs', {
      statusCode: 503,
      statusMessage: 'Service Unavailable',
    }).as('getTemperatureLogs')

    visitTemperatureLogs()

    cy.contains('Failed to fetch temperature zones: 500').should('be.visible')
    cy.contains('Failed to fetch temperature logs: 503').should('be.visible')
  })
})
