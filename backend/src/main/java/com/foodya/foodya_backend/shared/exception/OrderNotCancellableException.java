package com.foodya.foodya_backend.shared.exception;

import org.springframework.http.HttpStatus;

public class OrderNotCancellableException extends AppException {
  public static final String CODE = "ORDER_NOT_CANCELLABLE";
  public static final String DEFAULT_MESSAGE = "Order can no longer be cancelled";

  public OrderNotCancellableException() {
    this(DEFAULT_MESSAGE);
  }

  public OrderNotCancellableException(String message) {
    super(HttpStatus.UNPROCESSABLE_ENTITY, CODE, message);
  }
}
