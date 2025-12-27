package com.foodya.foodya_backend.utils.exception.business;

public class BadRequestException extends RuntimeException {
  public BadRequestException(String message) {
    super(message);
  }

}
