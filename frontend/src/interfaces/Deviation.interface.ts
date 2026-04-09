export type DeviationCategory =
  | 'TEMPERATURE'
  | 'HYGIENE'
  | 'ALCOHOL'
  | 'DOCUMENTATION'
  | 'OTHER'

export type DeviationSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'

export type DeviationModule = 'IK_FOOD' | 'IK_ALCOHOL'

export type DeviationStatus = 'OPEN' | 'IN_PROGRESS' | 'RESOLVED'

export interface Deviation {
  id?: number
  title: string
  category: DeviationCategory
  severity: DeviationSeverity
  module: DeviationModule
  status: DeviationStatus
  description: string
  createdAt?: string
}

export interface CreateDeviationInput {
  title: string
  category: DeviationCategory
  severity: DeviationSeverity
  module: DeviationModule
  status: DeviationStatus
  description: string
  checklistItemId?: number
  logId?: number
}

export interface DeviationFormInput {
  title: string
  referenceNumber: string
  category: DeviationCategory
  severity: DeviationSeverity
  module: DeviationModule
  status: DeviationStatus
  reportedDate: string
  discoveredBy: string
  reportedTo: string
  assignedTo: string
  issueDate: string
  issueDescription: string
  immediateActionDate: string
  immediateAction: string
  immediateActionSignature: string
  causeDate: string
  rootCause: string
  correctiveActionDate: string
  correctiveAction: string
  correctiveActionSignature: string
  completionDate: string
  completionNotes: string
  completionSignature: string
}
