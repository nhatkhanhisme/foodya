package com.foodya.foodya_backend.shared.exception;

import org.springframework.http.HttpStatus;

public class AddressLastOneException extends AppException {
  public static final String CODE = "ADDRESS_LAST_ONE";
  public static final String DEFAULT_MESSAGE = "Cannot remove the last remaining address";

  public AddressLastOneException() {
    this(DEFAULT_MESSAGE);
  }

  public AddressLastOneException(String message) {
    super(HttpStatus.UNPROCESSABLE_ENTITY, CODE, message);
  }
}
