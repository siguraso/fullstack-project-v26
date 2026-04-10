/// <reference types="cypress" />

import { defaultTenant, visitAuthenticatedRoute } from '../support/app-helpers'

const inspectionStats = {
  deviationTotal: 12,
  deviationOpen: 4,
  deviationInProgress: 3,
  deviationResolved: 5,
  deviationCritical: 2,
  deviationHigh: 3,
  deviationMedium: 4,
  deviationLow: 3,
  temperatureTotal: 8,
  temperatureOk: 5,
  temperatureWarning: 2,
  temperatureCritical: 1,
  alcoholTotal: 6,
  alcoholOk: 4,
  alcoholWarning: 1,
  alcoholCritical: 1,
}

describe('Inspections view', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/tenants/current', {
      body: { data: defaultTenant },
    }).as('getTenant')

    cy.intercept('GET', '/api/inspection/stats', {
      body: { data: inspectionStats },
    }).as('getInspectionStats')
  })

  it('renders KPI strip with stats from the API', () => {
    visitAuthenticatedRoute('/inspections')
    cy.wait(['@getTenant', '@getInspectionStats'])

    cy.contains('h1', 'Inspection Overview').should('be.visible')
    cy.contains('.kpi-value', '12').should('be.visible')
    cy.contains('.kpi-label', 'Total Deviations').should('be.visible')
    cy.contains('.kpi-label', 'Open').should('be.visible')
    cy.contains('.kpi-label', 'Critical').should('be.visible')
    cy.contains('.kpi-label', 'Temp. Logs').should('be.visible')
    cy.contains('.kpi-label', 'Alcohol Logs').should('be.visible')
  })

  it('opens the export modal and exports PDF', () => {
    visitAuthenticatedRoute('/inspections')
    cy.wait(['@getTenant', '@getInspectionStats'])

    cy.intercept('POST', '/api/inspection/export/pdf', (req) => {
      req.reply({
        statusCode: 200,
        headers: { 'Content-Type': 'application/pdf' },
        body: new Blob(['%PDF-1.4'], { type: 'application/pdf' }),
      })
    }).as('exportPdf')

    cy.contains('button', 'Export to PDF').click()
    cy.contains('h2', 'Export to PDF').should('be.visible')

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    let createObjectURL: any, revokeObjectURL: any, anchorClick: any

    cy.window().then((win) => {
      createObjectURL = cy.stub(win.URL, 'createObjectURL').returns('blob:inspection-report')
      revokeObjectURL = cy.stub(win.URL, 'revokeObjectURL')
      anchorClick = cy.stub(win.HTMLAnchorElement.prototype, 'click')
    })

    cy.contains('button', 'Export PDF').click()
    cy.wait('@exportPdf')

    cy.then(() => {
      expect(createObjectURL).to.have.been.called
      expect(anchorClick).to.have.been.called
      expect(revokeObjectURL).to.have.been.calledWith('blob:inspection-report')
    })
  })

  it('allows filtering by type in the export modal', () => {
    visitAuthenticatedRoute('/inspections')
    cy.wait(['@getTenant', '@getInspectionStats'])

    cy.intercept('POST', '/api/inspection/export/pdf', (req) => {
      expect(req.body.types).to.deep.equal(['DEVIATION'])
      req.reply({
        statusCode: 200,
        headers: { 'Content-Type': 'application/pdf' },
        body: '',
      })
    }).as('exportPdfFiltered')

    cy.contains('button', 'Export to PDF').click()
    cy.contains('.chip', 'Deviations').click()

    cy.window().then((win) => {
      cy.stub(win.URL, 'createObjectURL').returns('blob:filtered')
      cy.stub(win.URL, 'revokeObjectURL')
      cy.stub(win.HTMLAnchorElement.prototype, 'click')
    })

    cy.contains('button', 'Export PDF').click()
    cy.wait('@exportPdfFiltered')
  })

  it('closes the modal when clicking Cancel', () => {
    visitAuthenticatedRoute('/inspections')
    cy.wait(['@getTenant', '@getInspectionStats'])

    cy.contains('button', 'Export to PDF').click()
    cy.contains('h2', 'Export to PDF').should('be.visible')
    cy.contains('button', 'Cancel').click()
    cy.contains('h2', 'Export to PDF').should('not.exist')
  })
})
