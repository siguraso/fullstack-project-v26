export interface InspectionLog {
  type: 'DEVIATION' | 'TEMPERATURE' | 'ALCOHOL'
  referenceId: number
  title: string
  description?: string
  status?: string
  severity?: string
  module?: string
  actor?: string
  timestamp: string
}

export interface InspectionStats {
  // Deviations
  deviationTotal: number
  deviationOpen: number
  deviationInProgress: number
  deviationResolved: number
  deviationCritical: number
  deviationHigh: number
  deviationMedium: number
  deviationLow: number
  // Temperature
  temperatureTotal: number
  temperatureOk: number
  temperatureWarning: number
  temperatureCritical: number
  // Alcohol
  alcoholTotal: number
  alcoholOk: number
  alcoholWarning: number
  alcoholCritical: number
}

export interface InspectionExportFilter {
  types?: string[]
  severities?: string[]
  statuses?: string[]
  dateFrom?: string
  dateTo?: string
}
