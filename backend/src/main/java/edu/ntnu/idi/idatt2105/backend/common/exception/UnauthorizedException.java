package edu.ntnu.idi.idatt2105.backend.common.exception;

/**
 * Exception thrown when an operation is denied because the caller does not
 * own the resource or lacks the required context. Mapped to HTTP 403 by
 * {@link GlobalExceptionHandler}.
 */
public class UnauthorizedException extends RuntimeException {

    /**
     * @param message a human-readable reason for the access denial
     */
    public UnauthorizedException(String message) {
        super(message);
    }

    /**
     * @param message a human-readable reason for the access denial
     * @param cause   the underlying cause
     */
    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
