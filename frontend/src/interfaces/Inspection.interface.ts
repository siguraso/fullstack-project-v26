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
