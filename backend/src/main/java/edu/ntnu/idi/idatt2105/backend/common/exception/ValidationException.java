package edu.ntnu.idi.idatt2105.backend.common.exception;

/**
 * Exception thrown when a business-level validation rule is violated (e.g.
 * duplicate email, invalid file type). Mapped to HTTP 400 by
 * {@link GlobalExceptionHandler}.
 */
public class ValidationException extends RuntimeException {

    /**
     * @param message a human-readable description of the validation failure
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * @param message a human-readable description of the validation failure
     * @param cause   the underlying cause
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
