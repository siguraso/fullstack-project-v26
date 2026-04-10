package edu.ntnu.idi.idatt2105.backend.common.exception;

/**
 * Exception thrown when a requested resource cannot be found. Mapped to HTTP
 * 404 by {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * @param message a human-readable description of what was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * @param resource the resource type name (e.g. {@code "User"})
     * @param id       the identifier that was looked up
     */
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with id: " + id);
    }

    /**
     * @param message a human-readable description of what was not found
     * @param cause   the underlying cause
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}