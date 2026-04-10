/// <reference types="cypress" />

import { defaultTenant, visitAuthenticatedRoute } from '../support/app-helpers'

const initialDeviations = [
  {
    id: 11,
    title: 'Walk-in cooler temperature drift',
    category: 'TEMPERATURE',
    severity: 'HIGH',
    module: 'IK_FOOD',
    status: 'OPEN',
    reportedDate: '2026-04-08',
    discoveredBy: 'Ada Admin',
    reportedTo: 'Morgan Manager',
    assignedTo: 'Morgan Manager',
    issueDescription: 'Cooler reached 8C during opening checks.',
    immediateAction: 'Moved dairy products to backup unit.',
    rootCause: 'Door left ajar overnight.',
    correctiveAction: 'Replace faulty door seal.',
    completionNotes: '',
    createdAt: '2026-04-08T07:00:00.000Z',
  },
  {
    id: 12,
    title: 'Missing hand soap at prep sink',
    category: 'HYGIENE',
    severity: 'CRITICAL',
    module: 'IK_FOOD',
    status: 'IN_PROGRESS',
    reportedDate: '2026-04-09',
    discoveredBy: 'Morgan Manager',
    reportedTo: 'Ada Admin',
    assignedTo: 'Ada Admin',
    issueDescription: 'Prep sink had no hand soap before service.',
    immediateAction: 'Placed temporary refill bottle at sink.',
    rootCause: 'Supply cabinet was not restocked.',
    correctiveAction: 'Add soap refill to opening checklist.',
    completionNotes: '',
    createdAt: '2026-04-09T08:15:00.000Z',
  },
]

describe('Deviation view', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/tenants/current', {
      body: { data: defaultTenant },
    }).as('getTenant')
  })

  it('creates a deviation from the form and shows it in the table', () => {
    cy.intercept('GET', '/api/deviations', {
      body: { data: initialDeviations },
    }).as('getDeviations')

    cy.intercept('POST', '/api/deviations', (req) => {
      expect(req.body).to.include({
        title: 'Sanitizer dilution out of range',
        category: 'TEMPERATURE',
        severity: 'HIGH',
        module: 'IK_FOOD',
        status: 'OPEN',
      })
      expect(req.body.issueDescription).to.equal('Measured 80 ppm instead of 200 ppm.')

      req.reply({
        statusCode: 201,
        body: {
          data: {
            id: 13,
            title: 'Sanitizer dilution out of range',
            category: 'TEMPERATURE',
            severity: 'HIGH',
            module: 'IK_FOOD',
            status: 'OPEN',
            reportedDate: req.body.reportedDate,
            discoveredBy: '',
            reportedTo: '',
            assignedTo: '',
            issueDescription: 'Measured 80 ppm instead of 200 ppm.',
            immediateAction: '',
            rootCause: '',
            correctiveAction: '',
            completionNotes: '',
            createdAt: '2026-04-09T09:15:00.000Z',
          },
        },
      })
    }).as('createDeviation')

    visitAuthenticatedRoute('/deviation')
    cy.wait(['@getTenant', '@getDeviations'])

    cy.contains('h1', 'Deviations').should('be.visible')
    cy.get('input[placeholder*="Cold storage"]').type('Sanitizer dilution out of range')
    cy.contains('button', 'Major').click()
    cy.get('textarea[placeholder*="Describe the deviation"]').type(
      'Measured 80 ppm instead of 200 ppm.',
    )
    cy.contains('button', 'Submit Deviation').click()

    cy.wait('@createDeviation')
    cy.contains('tr', 'Sanitizer dilution out of range').should('contain', 'HIGH')
  })

  it('opens deviation details and resolves an existing deviation', () => {
    let deviationLoadCount = 0
    cy.intercept('GET', '/api/deviations', (req) => {
      deviationLoadCount += 1
      req.reply({
        body: {
          data:
            deviationLoadCount === 1
              ? initialDeviations
              : [
                  { ...initialDeviations[0], status: 'RESOLVED' },
                  initialDeviations[1],
                ],
        },
      })
    }).as('getDeviations')

    cy.intercept('PATCH', '/api/deviations/11', (req) => {
      expect(req.body).to.deep.equal({ status: 'RESOLVED' })
      req.reply({
        body: {
          data: {
            ...initialDeviations[0],
            status: 'RESOLVED',
          },
        },
      })
    }).as('resolveDeviation')

    visitAuthenticatedRoute('/deviation')
    cy.wait(['@getTenant', '@getDeviations'])

    cy.contains('tr', 'Walk-in cooler temperature drift').within(() => {
      cy.contains('button', 'View').click()
    })

    cy.contains('.dialog-content', 'Walk-in cooler temperature drift').should('exist')
    cy.contains('[aria-label="Deviation summary"]', 'Category').should('contain', 'Temperature')
    cy.contains('[aria-label="Deviation details"]', 'Door left ajar overnight.').should('exist')

    cy.contains('button', 'Resolve').click()
    cy.wait('@resolveDeviation')
    cy.wait('@getDeviations')

    cy.contains('.dialog-content', 'Walk-in cooler temperature drift').should('not.exist')
    cy.contains('tr', 'Walk-in cooler temperature drift').within(() => {
      cy.contains('button', 'View').click()
    })
    cy.contains('.dialog-content', 'Walk-in cooler temperature drift').should('exist')
    cy.contains('[aria-label="Deviation summary"]', 'Status').should('contain', 'Resolved')
  })
})
