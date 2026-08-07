package com.foodya.foodya_backend.shared.exception;

import org.springframework.http.HttpStatus;

public class ItemsUnavailableException extends AppException {
  public static final String CODE = "ITEMS_UNAVAILABLE";
  public static final String DEFAULT_MESSAGE = "One or more items are unavailable";

  public ItemsUnavailableException() {
    this(DEFAULT_MESSAGE);
  }

  public ItemsUnavailableException(String message) {
    super(HttpStatus.UNPROCESSABLE_ENTITY, CODE, message);
  }
}
