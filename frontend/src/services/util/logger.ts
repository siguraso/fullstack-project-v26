type LogPayload = Record<string, unknown> | undefined
type LogMethod = 'log' | 'info' | 'warn' | 'error'

type Logger = {
  log: (event: string, payload?: LogPayload) => void
  info: (event: string, payload?: LogPayload) => void
  warn: (event: string, payload?: LogPayload) => void
  error: (event: string, error?: unknown, payload?: LogPayload) => void
}

function buildMessage(scope: string, event: string) {
  return `[frontend][${scope}] ${event}`
}

function serializeError(error: unknown) {
  if (error instanceof Error) {
    return {
      name: error.name,
      message: error.message,
    }
  }

  return {
    value: error,
  }
}

function writeLog(method: LogMethod, scope: string, event: string, payload?: LogPayload) {
  const message = buildMessage(scope, event)

  if (payload && Object.keys(payload).length > 0) {
    console[method](message, payload)
    return
  }

  console[method](message)
}

export function createLogger(scope: string): Logger {
  return {
    log(event, payload) {
      writeLog('log', scope, event, payload)
    },
    info(event, payload) {
      writeLog('info', scope, event, payload)
    },
    warn(event, payload) {
      writeLog('warn', scope, event, payload)
    },
    error(event, error, payload) {
      const nextPayload = error
        ? {
            ...(payload ?? {}),
            error: serializeError(error),
          }
        : payload

      writeLog('error', scope, event, nextPayload)
    },
  }
}
