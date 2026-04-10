/// <reference types="cypress" />

import { defaultTenant, visitAuthenticatedRoute } from '../support/app-helpers'

const logs = [
  {
    id: 201,
    title: 'Entry wristband check',
    description: 'Checked IDs during first rush.',
    logType: 'AGE_VERIFICATION',
    status: 'OK',
    idChecked: true,
    serviceRefused: false,
    estimatedAge: 24,
    recordedByName: 'Ada Admin',
    recordedAt: '2026-04-08T20:00:00.000Z',
  },
  {
    id: 202,
    title: 'Refused service at bar',
    description: 'Guest was visibly intoxicated.',
    logType: 'SERVICE_REFUSAL',
    status: 'WARNING',
    idChecked: true,
    serviceRefused: true,
    estimatedAge: 32,
    recordedByName: 'Morgan Manager',
    recordedAt: '2026-04-08T21:15:00.000Z',
  },
]

describe('Alcohol log view', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/tenants/current', {
      body: { data: defaultTenant },
    }).as('getTenant')
  })

  it('creates a regular alcohol log and opens the details dialog', () => {
    cy.intercept('GET', '/api/ikalcohol/logs', {
      body: { data: logs },
    }).as('getAlcoholLogs')

    cy.intercept('POST', '/api/ikalcohol/logs', (req) => {
      expect(req.body).to.deep.equal({
        title: 'Closing stock count',
        description: 'Counted sealed and open bottles before closing.',
        logType: 'CLOSING_STOCK',
        status: 'OK',
        idChecked: null,
        serviceRefused: null,
        estimatedAge: null,
      })

      req.reply({
        statusCode: 201,
        body: {
          data: {
            id: 203,
            title: 'Closing stock count',
            description: 'Counted sealed and open bottles before closing.',
            logType: 'CLOSING_STOCK',
            status: 'OK',
            idChecked: null,
            serviceRefused: null,
            estimatedAge: null,
            recordedByName: 'Ada Admin',
            recordedAt: '2026-04-08T23:00:00.000Z',
          },
        },
      })
    }).as('createAlcoholLog')

    visitAuthenticatedRoute('/alcohol-logs')
    cy.wait(['@getTenant', '@getAlcoholLogs'])

    cy.contains('h1', 'Alcohol Compliance Logs').should('be.visible')
    cy.get('input[placeholder*="Age verification"]').type('Closing stock count')
    cy.get('select').first().select('Closing Stock')
    cy.get('select').eq(1).select('OK')
    cy.get('textarea[placeholder*="Additional details"]').type(
      'Counted sealed and open bottles before closing.',
    )
    cy.contains('button', 'Create log').click()

    cy.wait('@createAlcoholLog')
    cy.contains('tr', 'Closing stock count').should('contain', 'Closing Stock')

    cy.contains('tr', 'Closing stock count').within(() => {
      cy.contains('button', 'View').click()
    })

    cy.contains('.dialog-content', 'Closing stock count').should('exist')
    cy.contains('.detail-item', 'Type').should('contain', 'Closing Stock')
    cy.contains('.detail-item', 'Status').should('contain', 'OK')
    cy.contains('.description', 'Counted sealed and open bottles before closing.').should('exist')
    cy.contains('button', 'Close').click()
    cy.contains('.dialog-content', 'Closing stock count').should('not.exist')
  })
})
