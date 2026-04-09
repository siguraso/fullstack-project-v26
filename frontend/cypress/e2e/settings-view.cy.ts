/// <reference types="cypress" />

import { defaultTenant, visitAuthenticatedRoute } from '../support/app-helpers'

const users = [
  {
    id: 1,
    tenantId: 1,
    firstName: 'Ada',
    lastName: 'Admin',
    username: 'ada.admin',
    email: 'admin@example.com',
    phone: '12345678',
    role: 'ADMIN',
    active: true,
  },
  {
    id: 2,
    tenantId: 1,
    firstName: 'Morgan',
    lastName: 'Manager',
    username: 'morgan.manager',
    email: 'manager@example.com',
    phone: '87654321',
    role: 'MANAGER',
    active: true,
  },
]

describe('Settings view', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/tenants/current', {
      body: { data: defaultTenant },
    }).as('getTenant')
  })

  it('saves workspace details and sends a staff invite', () => {
    cy.intercept('GET', '/api/users', {
      body: { data: users },
    }).as('getUsers')

    cy.intercept('PUT', '/api/tenants/current', (req) => {
      expect(req.body).to.deep.equal({
        name: 'Updated Kitchen',
        orgNumber: '123456789',
        address: 'Test Street 1',
        city: 'Oslo',
        country: 'Norway',
        contactEmail: 'admin@example.com',
        contactPhone: '12345678',
      })

      req.reply({
        body: {
          data: {
            ...defaultTenant,
            name: 'Updated Kitchen',
            city: 'Oslo',
          },
        },
      })
    }).as('saveTenant')

    cy.intercept('POST', '/api/users/invite', (req) => {
      expect(req.body).to.deep.equal({ email: 'new.staff@example.com' })
      req.reply({
        statusCode: 200,
        body: { data: null },
      })
    }).as('sendInvite')

    visitAuthenticatedRoute('/settings')
    cy.wait(['@getTenant', '@getUsers'])

    cy.contains('h1', 'Organisation Configuration').should('be.visible')
    cy.get('input[placeholder="The Nordic Kitchen"]').clear().type('Updated Kitchen')
    cy.get('input[placeholder="Oslo"]').clear().type('Oslo')
    cy.contains('button', 'Save workspace').click()

    cy.wait('@saveTenant')
    cy.contains('Workspace settings saved.').should('be.visible')

    cy.contains('button', 'Invite Staff').click()
    cy.get('input[placeholder="new.staff@example.com"]').type('new.staff@example.com')
    cy.contains('button', 'Send').click()

    cy.wait('@sendInvite')
    cy.contains('Invitation sent to new.staff@example.com.').should('be.visible')
  })

  it('filters staff and updates a staff member from the editor', () => {
    let usersRequestCount = 0
    cy.intercept('GET', '/api/users', (req) => {
      usersRequestCount += 1
      req.reply({
        body: {
          data:
            usersRequestCount === 1
              ? users
              : [
                  users[0],
                  {
                    ...users[1],
                    phone: '99999999',
                    role: 'ADMIN',
                  },
                ],
        },
      })
    }).as('getUsers')

    cy.intercept('GET', '/api/users/2', {
      body: {
        data: users[1],
      },
    }).as('getUser')

    cy.intercept('PUT', '/api/users/2', (req) => {
      expect(req.body).to.deep.equal({
        firstName: 'Morgan',
        lastName: 'Manager',
        email: 'manager@example.com',
        phone: '99999999',
        role: 'ADMIN',
      })

      req.reply({
        body: {
          data: {
            ...users[1],
            phone: '99999999',
            role: 'ADMIN',
          },
        },
      })
    }).as('updateUser')

    visitAuthenticatedRoute('/settings')
    cy.wait(['@getTenant', '@getUsers'])

    cy.get('input[placeholder*="Search by name"]').type('Morgan')
    cy.contains('.table-row', 'Morgan Manager').should('be.visible')
    cy.contains('.table-row', 'Ada Admin').should('not.exist')

    cy.get('select').first().select('MANAGER')
    cy.contains('.table-row', 'Morgan Manager').should('be.visible')

    cy.contains('.table-row', 'Morgan Manager').within(() => {
      cy.get('button[aria-label="Edit staff member"]').click()
    })

    cy.wait('@getUser')
    cy.contains('.user-editor-card', 'Morgan Manager').should('be.visible')
    cy.get('.user-editor-card input[type="text"]').eq(2).clear().type('99999999')
    cy.get('.user-editor-card select').select('ADMIN')
    cy.contains('.user-editor-card button', 'Save user').scrollIntoView().click({ force: true })

    cy.wait('@updateUser')
    cy.wait('@getUsers')
    cy.contains('Morgan Manager updated.').should('be.visible')
    cy.get('select').first().select('All roles')
    cy.contains('.table-row', 'Morgan Manager').should('contain', 'ADMIN')
    cy.contains('.table-row', 'Morgan Manager').should('contain', '99999999')
  })
})
