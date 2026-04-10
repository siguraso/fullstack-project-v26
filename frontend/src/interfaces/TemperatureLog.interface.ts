export interface TemperatureLog {
  id: number
  temperatureZoneId: number
  temperatureZoneName?: string
  temperatureCelsius: number
  timestamp: string
  note?: string | null
  recordedById?: number
  recordedByName?: string
  recordedByEmail?: string
  recordedByRole?: string
  lowerLimitCelsius?: number
  upperLimitCelsius?: number
  deviationCreated?: boolean
}

export interface TemperatureLogInput {
  temperatureZoneId: number
  temperatureCelsius: number
  note?: string | null
}
