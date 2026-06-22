package com.foodya.foodya_backend.common.exception.security;

public class UnauthorizedException extends RuntimeException {
  public UnauthorizedException(String message) {
    super(message);
  }
}
