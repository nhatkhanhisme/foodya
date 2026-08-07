package com.foodya.foodya_backend.shared.exception;

import org.springframework.http.HttpStatus;

public class PaymentFailedException extends AppException {
  public static final String CODE = "PAYMENT_FAILED";
  public static final String DEFAULT_MESSAGE = "Payment could not be completed";

  public PaymentFailedException() {
    this(DEFAULT_MESSAGE);
  }

  public PaymentFailedException(String message) {
    super(HttpStatus.PAYMENT_REQUIRED, CODE, message);
  }
}
