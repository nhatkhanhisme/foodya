package com.foodya.foodya_backend.common.exception;

import org.springframework.http.HttpStatus;

public class RateLimitExceededException extends AppException {
  public static final String CODE = "RATE_LIMIT_EXCEEDED";
  public static final String DEFAULT_MESSAGE = "Too many requests, please try again later";

  public RateLimitExceededException() {
    this(DEFAULT_MESSAGE);
  }

  public RateLimitExceededException(String message) {
    super(HttpStatus.TOO_MANY_REQUESTS, CODE, message);
  }
}
