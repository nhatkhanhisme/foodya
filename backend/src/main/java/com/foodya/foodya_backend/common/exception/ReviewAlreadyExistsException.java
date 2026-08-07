package com.foodya.foodya_backend.common.exception;

import org.springframework.http.HttpStatus;

public class ReviewAlreadyExistsException extends AppException {
  public static final String CODE = "REVIEW_ALREADY_EXISTS";
  public static final String DEFAULT_MESSAGE = "You already submitted a review for this order";

  public ReviewAlreadyExistsException() {
    this(DEFAULT_MESSAGE);
  }

  public ReviewAlreadyExistsException(String message) {
    super(HttpStatus.UNPROCESSABLE_ENTITY, CODE, message);
  }
}
