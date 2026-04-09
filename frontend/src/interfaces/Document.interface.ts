export type DocumentArea = 'GENERAL' | 'IK_FOOD' | 'IK_ALCOHOL'

export interface DocumentSummary {
  id: number
  area: DocumentArea
  title: string
  description: string | null
  originalFilename: string
  mimeType: string
  sizeBytes: number
  uploadedById: number | null
  uploadedByName: string | null
  uploadedByRole: 'ADMIN' | 'MANAGER' | 'STAFF' | null
  createdAt: string | null
  updatedAt: string | null
  tags: string[]
}

export interface DocumentDetail extends DocumentSummary {}

export interface DocumentFilters {
  area?: DocumentArea | null
  query?: string
  tags?: string[]
}

export interface DocumentUpsertPayload {
  title: string
  description?: string | null
  area: DocumentArea
  tags: string[]
  file?: File | null
}
