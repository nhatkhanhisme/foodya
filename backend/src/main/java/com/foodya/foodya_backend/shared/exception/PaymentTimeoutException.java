package com.foodya.foodya_backend.shared.exception;

import org.springframework.http.HttpStatus;

public class PaymentTimeoutException extends AppException {
  public static final String CODE = "PAYMENT_TIMEOUT";
  public static final String DEFAULT_MESSAGE = "Payment session has expired";

  public PaymentTimeoutException() {
    this(DEFAULT_MESSAGE);
  }

  public PaymentTimeoutException(String message) {
    super(HttpStatus.UNPROCESSABLE_ENTITY, CODE, message);
  }
}
