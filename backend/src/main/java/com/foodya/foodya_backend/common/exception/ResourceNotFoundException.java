package com.foodya.foodya_backend.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends AppException {
  public static final String CODE = "RESOURCE_NOT_FOUND";
  public static final String DEFAULT_MESSAGE = "Requested resource not found";

  public ResourceNotFoundException() {
    this(DEFAULT_MESSAGE);
  }

  public ResourceNotFoundException(String message) {
    super(HttpStatus.NOT_FOUND, CODE, message);
  }
}
