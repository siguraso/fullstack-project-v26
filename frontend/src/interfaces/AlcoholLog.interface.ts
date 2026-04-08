export type AlcoholLogType = 'AGE_VERIFICATION' | 'SERVICE_REFUSAL' | 'CLOSING_STOCK' | 'TRAINING' | 'INCIDENT' | 'OTHER'
export type LogStatus = 'OK' | 'WARNING' | 'CRITICAL'

export interface AlcoholLog {
  id: number
  title: string
  description?: string | null
  logType: AlcoholLogType
  status?: LogStatus
  idChecked?: boolean | null
  serviceRefused?: boolean | null
  estimatedAge?: number | null
  recordedById?: number
  recordedByName?: string
  recordedByRole?: string
  recordedAt?: string
  createdAt?: string
  updatedAt?: string
}

export interface AlcoholLogInput {
  title: string
  description?: string | null
  logType: AlcoholLogType
  status?: LogStatus | null
  idChecked?: boolean | null
  serviceRefused?: boolean | null
  estimatedAge?: number | null
}




