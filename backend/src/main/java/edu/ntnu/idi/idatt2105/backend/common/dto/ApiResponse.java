package edu.ntnu.idi.idatt2105.backend.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private final boolean success;
  private final String  message;
  private final T       data;

  private ApiResponse(boolean success, String message, T data) {
    this.success = success;
    this.message = message;
    this.data    = data;
  }

  // Success
  public static <T> ApiResponse<T> ok(T data) {
    return new ApiResponse<>(true, null, data);
  }

  public static <T> ApiResponse<T> ok(String message, T data) {
    return new ApiResponse<>(true, message, data);
  }

  public static ApiResponse<Void> ok(String message) {
    return new ApiResponse<>(true, message, null);
  }

  // Error
  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<>(false, message, null);
  }

  public static <T> ApiResponse<T> error(String message, T data) {
    return new ApiResponse<>(false, message, data);
  }

  // Getters
  public boolean isSuccess() { return success; }
  public String  getMessage() { return message; }
  public T       getData()    { return data; }
}