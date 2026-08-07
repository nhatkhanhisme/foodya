package com.foodya.foodya_backend.common.exception;

import org.springframework.http.HttpStatus;

public class RestaurantSuspendedException extends AppException {
  public static final String CODE = "RESTAURANT_SUSPENDED";
  public static final String DEFAULT_MESSAGE = "Restaurant is currently suspended";

  public RestaurantSuspendedException() {
    this(DEFAULT_MESSAGE);
  }

  public RestaurantSuspendedException(String message) {
    super(HttpStatus.UNPROCESSABLE_ENTITY, CODE, message);
  }
}
