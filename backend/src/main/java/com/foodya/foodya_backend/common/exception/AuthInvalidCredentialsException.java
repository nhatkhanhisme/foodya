package com.foodya.foodya_backend.common.exception;

import org.springframework.http.HttpStatus;

public class AuthInvalidCredentialsException extends AppException {
  public static final String CODE = "AUTH_INVALID_CREDENTIALS";
  public static final String DEFAULT_MESSAGE = "Invalid email or password";

  public AuthInvalidCredentialsException() {
    this(DEFAULT_MESSAGE);
  }

  public AuthInvalidCredentialsException(String message) {
    super(HttpStatus.UNAUTHORIZED, CODE, message);
  }
}
