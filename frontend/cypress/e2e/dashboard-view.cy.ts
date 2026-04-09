/// <reference types="cypress" />

import { defaultTenant, visitAuthenticatedRoute } from '../support/app-helpers'

const initialDashboardOverview = {
  criticalAlerts: [
    {
      id: 41,
      title: 'Freezer temperature breach',
      description: 'Freezer 2 recorded an out-of-range reading.',
    },
  ],
  checklistsToday: {
    completedItems: 3,
    totalItems: 5,
  },
  activeDeviations: [{ id: 41 }],
  pendingChecklists: [
    {
      id: 501,
      name: 'Opening checklist',
      items: [
        { id: 901, title: 'Sanitize prep area', completed: false },
        { id: 902, title: 'Verify hand wash station', completed: false },
      ],
    },
  ],
  teamActivity: [
    {
      actor: 'Ada Admin',
      title: 'Resolved a hygiene deviation',
      occurredAt: '2026-04-09T08:00:00.000Z',
    },
    {
      actor: 'Morgan Manager',
      title: 'Completed opening checklist',
      occurredAt: '2026-04-09T07:30:00.000Z',
    },
  ],
}

const resolvedDashboardOverview = {
  ...initialDashboardOverview,
  criticalAlerts: [],
}

describe('Dashboard view', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/tenants/current', {
      body: { data: defaultTenant },
    }).as('getTenant')

    let dashboardRequestCount = 0
    cy.intercept('GET', '/api/dashboard/overview', (req) => {
      dashboardRequestCount += 1
      req.reply({
        body: {
          data: dashboardRequestCount === 1 ? initialDashboardOverview : resolvedDashboardOverview,
        },
      })
    }).as('getDashboardOverview')

    cy.intercept('PATCH', '/api/deviations/41', {
      body: {
        data: {
          id: 41,
          title: 'Freezer temperature breach',
          category: 'TEMPERATURE',
          severity: 'CRITICAL',
          module: 'IK_FOOD',
          status: 'RESOLVED',
          reportedDate: '2026-04-09',
          discoveredBy: 'Ada Admin',
          reportedTo: 'Morgan Manager',
          assignedTo: 'Morgan Manager',
          issueDescription: 'Freezer 2 recorded an out-of-range reading.',
          immediateAction: '',
          rootCause: '',
          correctiveAction: '',
          completionNotes: '',
          createdAt: '2026-04-09T07:45:00.000Z',
        },
      },
    }).as('resolveAlert')

    cy.intercept('GET', '/api/ikfood/temperature-zones', {
      body: { data: [] },
    }).as('getTemperatureZones')

    cy.intercept('GET', '/api/ikfood/temperature-logs', {
      body: { data: [] },
    }).as('getTemperatureLogs')
  })

  it('renders overview data, resolves a critical alert, and navigates from quick actions', () => {
    visitAuthenticatedRoute('/dashboard')

    cy.wait(['@getTenant', '@getDashboardOverview'])
    cy.contains('h1', 'Dashboard').should('be.visible')
    cy.contains('.card', 'Checklist Completion').should('contain', '3 / 5')
    cy.contains('.card', 'Checklist Completion').should('contain', '60% complete')
    cy.contains('.card', 'Deviations').should('contain', '1')

    cy.contains('h3', 'Critical Alerts (1)').should('be.visible')
    cy.contains('li', 'Freezer temperature breach').within(() => {
      cy.contains('button', 'Resolve').click()
    })

    cy.wait('@resolveAlert')
    cy.wait('@getDashboardOverview')
    cy.contains('Freezer temperature breach').should('not.exist')

    cy.contains('button', 'New Temperature Log').click()
    cy.wait(['@getTemperatureZones', '@getTemperatureLogs'])
    cy.location('pathname').should('eq', '/temperature-logs')
    cy.contains('h1', 'Temperature Logs').should('be.visible')
  })
})
