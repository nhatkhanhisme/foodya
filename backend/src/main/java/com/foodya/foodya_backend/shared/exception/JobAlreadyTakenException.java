package com.foodya.foodya_backend.shared.exception;

import org.springframework.http.HttpStatus;

public class JobAlreadyTakenException extends AppException {
  public static final String CODE = "JOB_ALREADY_TAKEN";
  public static final String DEFAULT_MESSAGE = "This job is already taken by another shipper";

  public JobAlreadyTakenException() {
    this(DEFAULT_MESSAGE);
  }

  public JobAlreadyTakenException(String message) {
    super(HttpStatus.CONFLICT, CODE, message);
  }
}
