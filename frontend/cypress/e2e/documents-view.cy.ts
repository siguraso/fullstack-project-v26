/// <reference types="cypress" />

const authSession = {
  email: 'manager@example.com',
  remember: true,
  token: 'test-access-token',
  refreshToken: 'test-refresh-token',
  role: 'MANAGER',
}

const documents = [
  {
    id: 1,
    area: 'GENERAL',
    title: 'Opening Manual',
    description: 'General operating procedures',
    originalFilename: 'opening-manual.pdf',
    mimeType: 'application/pdf',
    sizeBytes: 2048,
    uploadedById: 2,
    uploadedByName: 'Morgan Manager',
    uploadedByRole: 'MANAGER',
    createdAt: '2026-04-01T10:00:00',
    updatedAt: '2026-04-02T09:00:00',
    tags: ['opening', 'manual'],
  },
  {
    id: 2,
    area: 'IK_FOOD',
    title: 'Cold Room Checklist',
    description: 'Food safety process for cold storage',
    originalFilename: 'cold-room.docx',
    mimeType: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    sizeBytes: 4096,
    uploadedById: 2,
    uploadedByName: 'Morgan Manager',
    uploadedByRole: 'MANAGER',
    createdAt: '2026-04-03T10:00:00',
    updatedAt: '2026-04-03T10:00:00',
    tags: ['food', 'cold-room'],
  },
]

function seedAuthSession(win: Window) {
  win.localStorage.setItem('regula.auth.session', JSON.stringify(authSession))
}

describe('Documents view', () => {
  beforeEach(() => {
    cy.intercept('GET', '/api/documents*', (req) => {
      const area = req.query.area
      const query = String(req.query.q ?? '').toLowerCase()
      const tags = ([] as string[]).concat(req.query.tags ?? []).map((tag) => tag.toLowerCase())

      let result = documents

      if (area) {
        result = result.filter((document) => document.area === area)
      }

      if (query) {
        result = result.filter((document) =>
          [document.title, document.description, document.originalFilename, ...document.tags]
            .join(' ')
            .toLowerCase()
            .includes(query),
        )
      }

      if (tags.length > 0) {
        result = result.filter((document) => document.tags.some((tag) => tags.includes(tag)))
      }

      req.reply({
        body: { data: result },
      })
    }).as('getDocuments')

    cy.intercept('GET', '/api/documents/1/download', {
      headers: {
        'content-type': 'application/pdf',
        'content-disposition': 'attachment; filename="opening-manual.pdf"',
      },
      body: 'pdf-bytes',
    }).as('downloadDocument')
  })

  it('filters by keyword and tag, then downloads a document', () => {
    cy.visit('/documents', {
      onBeforeLoad(win) {
        seedAuthSession(win)
        cy.stub(win.URL, 'createObjectURL').returns('blob:test-url').as('createObjectUrl')
      },
    })

    cy.contains('h1', 'Document Library').should('be.visible')
    cy.get('input[type="search"]').type('opening')
    cy.wait('@getDocuments')
    cy.contains('Opening Manual').should('be.visible')
    cy.contains('Cold Room Checklist').should('not.exist')

    cy.get('input[placeholder="Press Enter to add a tag"]').first().type('manual{enter}')
    cy.wait('@getDocuments')
    cy.contains('Opening Manual').should('be.visible')

    cy.contains('button', 'Download').click()
    cy.wait('@downloadDocument')
    cy.get('@createObjectUrl').should('have.been.called')
  })
})
