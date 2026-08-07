package com.foodya.foodya_backend.shared.exception;

import org.springframework.http.HttpStatus;

public class RestaurantClosedException extends AppException {
  public static final String CODE = "RESTAURANT_CLOSED";
  public static final String DEFAULT_MESSAGE = "Restaurant is currently closed";

  public RestaurantClosedException() {
    this(DEFAULT_MESSAGE);
  }

  public RestaurantClosedException(String message) {
    super(HttpStatus.UNPROCESSABLE_ENTITY, CODE, message);
  }
}
