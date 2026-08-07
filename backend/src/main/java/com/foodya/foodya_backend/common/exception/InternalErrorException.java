package com.foodya.foodya_backend.common.exception;

import org.springframework.http.HttpStatus;

public class InternalErrorException extends AppException {
  public static final String CODE = "INTERNAL_ERROR";
  public static final String DEFAULT_MESSAGE = "An unexpected error occurred";

  public InternalErrorException() {
    this(DEFAULT_MESSAGE);
  }

  public InternalErrorException(String message) {
    super(HttpStatus.INTERNAL_SERVER_ERROR, CODE, message);
  }
}
