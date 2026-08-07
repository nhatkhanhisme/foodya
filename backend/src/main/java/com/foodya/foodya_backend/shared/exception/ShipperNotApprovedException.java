package com.foodya.foodya_backend.shared.exception;

import org.springframework.http.HttpStatus;

public class ShipperNotApprovedException extends AppException {
  public static final String CODE = "SHIPPER_NOT_APPROVED";
  public static final String DEFAULT_MESSAGE = "Shipper account is not approved";

  public ShipperNotApprovedException() {
    this(DEFAULT_MESSAGE);
  }

  public ShipperNotApprovedException(String message) {
    super(HttpStatus.UNPROCESSABLE_ENTITY, CODE, message);
  }
}
