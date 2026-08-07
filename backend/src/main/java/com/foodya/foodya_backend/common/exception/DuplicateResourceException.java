package com.foodya.foodya_backend.common.exception;

import org.springframework.http.HttpStatus;

public class DuplicateResourceException extends AppException {
  public static final String CODE = "DUPLICATE_RESOURCE";
  public static final String DEFAULT_MESSAGE = "Resource already exists";

  public DuplicateResourceException() {
    this(DEFAULT_MESSAGE);
  }

  public DuplicateResourceException(String message) {
    super(HttpStatus.CONFLICT, CODE, message);
  }
}
