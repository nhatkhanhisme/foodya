package com.foodya.foodya_backend.common.exception;

import org.springframework.http.HttpStatus;

public class WebhookInvalidSignatureException extends AppException {
  public static final String CODE = "WEBHOOK_INVALID_SIGNATURE";
  public static final String DEFAULT_MESSAGE = "Invalid webhook signature";

  public WebhookInvalidSignatureException() {
    this(DEFAULT_MESSAGE);
  }

  public WebhookInvalidSignatureException(String message) {
    super(HttpStatus.BAD_REQUEST, CODE, message);
  }
}
