package com.foodya.foodya_backend.common.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends AppException {
  public static final String CODE = "FORBIDDEN";
  public static final String DEFAULT_MESSAGE = "You do not have permission to access this resource";

  public ForbiddenException() {
    this(DEFAULT_MESSAGE);
  }

  public ForbiddenException(String message) {
    super(HttpStatus.FORBIDDEN, CODE, message);
  }
}
