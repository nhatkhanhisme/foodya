package com.foodya.foodya_backend.shared.exception;

import org.springframework.http.HttpStatus;

public class AuthTokenRevokedException extends AppException {
  public static final String CODE = "AUTH_TOKEN_REVOKED";
  public static final String DEFAULT_MESSAGE = "Authentication token is no longer valid";

  public AuthTokenRevokedException() {
    this(DEFAULT_MESSAGE);
  }

  public AuthTokenRevokedException(String message) {
    super(HttpStatus.UNAUTHORIZED, CODE, message);
  }
}
