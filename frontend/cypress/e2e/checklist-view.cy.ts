/// <reference types="cypress" />

import { defaultTenant, visitAuthenticatedRoute } from '../support/app-helpers'

const todayChecklist = [
  {
    id: 301,
    name: 'Morning food safety',
    module: 'IK_FOOD',
    status: 'OPEN',
    items: [
      {
        id: 401,
        title: 'Calibrate fridge probe',
        description: 'Verify the fridge probe against reference thermometer.',
        completed: false,
      },
      {
        id: 402,
        title: 'Inspect prep sink',
        description: 'Confirm soap and paper are stocked.',
        completed: true,
      },
    ],
  },
  {
    id: 302,
    name: 'Bar opening',
    module: 'IK_ALCOHOL',
    status: 'OPEN',
    items: [
      {
        id: 403,
        title: 'Check ID scanner battery',
        description: 'Confirm the scanner is charged before service.',
        completed: false,
      },
    ],
  },
]

describe('Checklist view', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/tenants/current', {
      body: { data: defaultTenant },
    }).as('getTenant')
  })

  it('renders grouped checklist sections and toggles a task', () => {
    cy.intercept('GET', '/api/checklists/today', {
      body: { data: todayChecklist },
    }).as('getTodayChecklist')

    cy.intercept('PATCH', '/api/checklists/items/401', (req) => {
      expect(req.body).to.deep.equal({
        completed: true,
        comment: null,
      })
      req.reply({ statusCode: 200, body: {} })
    }).as('updateChecklistItem')

    visitAuthenticatedRoute('/checklists')
    cy.wait(['@getTenant', '@getTodayChecklist'])

    cy.contains('h1', 'Checklists').should('be.visible')
    cy.contains('.summary-bar', '33%').should('be.visible')
    cy.contains('.module-section', 'IK-Food').should('be.visible')
    cy.contains('.module-section', 'IK-Alcohol').should('be.visible')

    cy.contains('.task', 'Calibrate fridge probe').click()
    cy.wait('@updateChecklistItem')
    cy.contains('.task', 'Calibrate fridge probe').should('have.class', 'completed')
  })

  it('navigates to the checklist builder', () => {
    cy.intercept('GET', '/api/checklists/today', {
      body: { data: todayChecklist },
    }).as('getTodayChecklist')

    cy.intercept('GET', '/api/checklists/templates', {
      body: { data: [] },
    }).as('getTemplates')

    cy.intercept('GET', '/api/checklist-library', {
      body: { data: [] },
    }).as('getChecklistLibrary')

    visitAuthenticatedRoute('/checklists')
    cy.wait(['@getTenant', '@getTodayChecklist'])

    cy.contains('button', '+ New Checklist').click()
    cy.wait(['@getTemplates', '@getChecklistLibrary'])
    cy.location('pathname').should('eq', '/checklist-builder')
    cy.contains('h1', 'Checklist Builder').should('be.visible')
  })
})
