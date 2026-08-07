package com.foodya.foodya_backend.common.exception;

import org.springframework.http.HttpStatus;

public class AuthAccountBannedException extends AppException {
  public static final String CODE = "AUTH_ACCOUNT_BANNED";
  public static final String DEFAULT_MESSAGE = "Your account has been banned";

  public AuthAccountBannedException() {
    this(DEFAULT_MESSAGE);
  }

  public AuthAccountBannedException(String message) {
    super(HttpStatus.FORBIDDEN, CODE, message);
  }
}
