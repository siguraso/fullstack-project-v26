package edu.ntnu.idi.idatt2105.backend.common.exception;

import edu.ntnu.idi.idatt2105.backend.common.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handler that maps common backend exceptions to
 * structured {@link ApiResponse} instances and appropriate HTTP status codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handles {@link ResourceNotFoundException}, returning HTTP 404.
   *
   * @param ex the exception
   * @return a 404 response with the exception message
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
    log.warn("Resource not found: {}", ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error(ex.getMessage()));
  }

  /**
   * Handles {@link UnauthorizedException}, returning HTTP 403.
   *
   * @param ex the exception
   * @return a 403 response with the exception message
   */
  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
    log.warn("Unauthorized access attempt: {}", ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error(ex.getMessage()));
  }

  /**
   * Handles Spring Security {@link AccessDeniedException}, returning HTTP 403.
   *
   * @param ex the exception
   * @return a 403 response with a generic access-denied message
   */
  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
    log.warn("Access denied: {}", ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(ApiResponse.error("Access denied"));
  }

  /**
   * Handles {@link BadCredentialsException} from a failed login attempt,
   * returning HTTP 401.
   *
   * @param ex the exception
   * @return a 401 response with a generic invalid-credentials message
   */
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
    log.warn("Bad credentials: {}", ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(ApiResponse.error("Invalid email or password"));
  }

  /**
   * Handles {@link ValidationException}, returning HTTP 400.
   *
   * @param ex the exception
   * @return a 400 response with the validation error message
   */
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidation(ValidationException ex) {
    log.warn("Validation error: {}", ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(ex.getMessage()));
  }

  /**
   * Handles {@link IllegalArgumentException}, returning HTTP 400.
   *
   * @param ex the exception
   * @return a 400 response with the exception message
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
    log.warn("Illegal argument: {}", ex.getMessage());
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(ex.getMessage()));
  }

  /**
   * Handles {@code @Valid} failures on request bodies.
   * Returns a map of {@code field -> error message} so the frontend
   * can highlight the exact fields that failed.
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String field   = ((FieldError) error).getField();
      String message = error.getDefaultMessage();
      fieldErrors.put(field, message);
    });
    log.warn("Request body validation failed: {}", fieldErrors);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("Validation failed", fieldErrors));
  }

  /**
   * Catch-all handler for any unhandled exception, returning HTTP 500.
   *
   * @param ex the exception
   * @return a 500 response with a generic error message
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
    log.error("Unexpected error", ex);
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("An unexpected error occurred"));
  }
}