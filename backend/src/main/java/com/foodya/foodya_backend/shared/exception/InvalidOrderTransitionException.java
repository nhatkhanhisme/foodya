package com.foodya.foodya_backend.shared.exception;

import org.springframework.http.HttpStatus;

public class InvalidOrderTransitionException extends AppException {
  public static final String CODE = "INVALID_ORDER_TRANSITION";
  public static final String DEFAULT_MESSAGE = "Invalid order status transition";

  public InvalidOrderTransitionException() {
    this(DEFAULT_MESSAGE);
  }

  public InvalidOrderTransitionException(String message) {
    super(HttpStatus.UNPROCESSABLE_ENTITY, CODE, message);
  }
}
