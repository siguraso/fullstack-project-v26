/// <reference types="cypress" />

import { defaultTenant, visitAuthenticatedRoute } from '../support/app-helpers'

const inspectionLogs = [
  {
    type: 'DEVIATION',
    referenceId: 11,
    title: 'Resolved freezer alert',
    description: 'Follow-up completed for temperature deviation.',
    status: 'RESOLVED',
    actor: 'Ada Admin',
    timestamp: '2026-04-09T10:00:00.000Z',
  },
  {
    type: 'ALCOHOL',
    referenceId: 21,
    title: 'Incident log added',
    description: 'Service refusal recorded at main bar.',
    status: 'OPEN',
    actor: 'Morgan Manager',
    timestamp: '2026-04-09T11:00:00.000Z',
  },
]

describe('Inspections view', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/tenants/current', {
      body: { data: defaultTenant },
    }).as('getTenant')
  })

  it('renders inspection logs and exports them as JSON', () => {
    cy.intercept('GET', '/api/inspection/logs', {
      body: { data: inspectionLogs },
    }).as('getInspectionLogs')

    visitAuthenticatedRoute('/inspections')
    cy.wait(['@getTenant', '@getInspectionLogs'])

    cy.contains('h1', 'Inspection Logs').should('be.visible')
    cy.contains('.table-row', 'Resolved freezer alert').should('contain', 'Ada Admin')
    cy.contains('.table-row', 'Incident log added').should('contain', 'OPEN')

    cy.window().then((win) => {
      cy.stub(win.URL, 'createObjectURL').returns('blob:inspection-export').as('createObjectURL')
      cy.stub(win.URL, 'revokeObjectURL').as('revokeObjectURL')
      cy.stub(win.HTMLAnchorElement.prototype, 'click').as('anchorClick')
    })

    cy.contains('button', 'Export').click()

    cy.get('@createObjectURL').should('have.been.called')
    cy.get('@anchorClick').should('have.been.called')
    cy.get('@revokeObjectURL').should('have.been.calledWith', 'blob:inspection-export')
  })
})
