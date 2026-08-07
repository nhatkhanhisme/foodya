package com.foodya.foodya_backend.common.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends AppException {
  public static final String CODE = "VALIDATION_ERROR";
  public static final String DEFAULT_MESSAGE = "Request validation failed";

  public ValidationException() {
    this(DEFAULT_MESSAGE);
  }

  public ValidationException(String message) {
    super(HttpStatus.BAD_REQUEST, CODE, message);
  }
}
