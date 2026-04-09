import type { DocumentArea, DocumentDetail, DocumentSummary } from '@/interfaces/Document.interface'

export type AreaOption = {
  label: string
  value: DocumentArea | null
}

export type DocumentFormValues = {
  title: string
  description: string
  area: DocumentArea
  tags: string[]
  file: File | null
}

export const documentAreaOptions: AreaOption[] = [
  { label: 'All', value: null },
  { label: 'General', value: 'GENERAL' },
  { label: 'IK-food', value: 'IK_FOOD' },
  { label: 'IK-Alcohol', value: 'IK_ALCOHOL' },
]

export function createEmptyDocumentForm(
  defaultArea: DocumentArea | null = null,
): DocumentFormValues {
  return {
    title: '',
    description: '',
    area: defaultArea ?? 'GENERAL',
    tags: [],
    file: null,
  }
}

export function createDocumentFormValues(
  document: Pick<DocumentDetail, 'title' | 'description' | 'area' | 'tags'> | null,
  defaultArea: DocumentArea | null = null,
): DocumentFormValues {
  if (!document) {
    return createEmptyDocumentForm(defaultArea)
  }

  return {
    title: document.title,
    description: document.description ?? '',
    area: document.area,
    tags: [...document.tags],
    file: null,
  }
}

export function normalizeDocumentTag(tag: string) {
  return tag.trim().toLowerCase()
}

export function displayDocumentArea(area: DocumentArea) {
  if (area === 'IK_FOOD') {
    return 'IK-food'
  }

  if (area === 'IK_ALCOHOL') {
    return 'IK-Alcohol'
  }

  return 'General'
}

export function formatDocumentDate(value: string | null) {
  if (!value) {
    return 'Unknown'
  }

  return new Intl.DateTimeFormat('en-GB', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value))
}

export function formatDocumentBytes(sizeBytes: number) {
  if (sizeBytes < 1024) {
    return `${sizeBytes} B`
  }

  if (sizeBytes < 1024 * 1024) {
    return `${(sizeBytes / 1024).toFixed(1)} KB`
  }

  return `${(sizeBytes / (1024 * 1024)).toFixed(1)} MB`
}

export function canPreviewDocument(
  document: Pick<DocumentSummary, 'mimeType' | 'originalFilename'>,
) {
  const mimeType = document.mimeType.toLowerCase()
  const filename = document.originalFilename.toLowerCase()

  return (
    mimeType === 'application/pdf' ||
    mimeType.startsWith('image/') ||
    mimeType.startsWith('text/') ||
    filename.endsWith('.txt')
  )
}
