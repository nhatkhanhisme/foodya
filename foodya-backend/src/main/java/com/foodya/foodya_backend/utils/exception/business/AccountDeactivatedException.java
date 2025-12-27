package com.foodya.foodya_backend.utils.exception.business;

public class AccountDeactivatedException extends RuntimeException{
  public AccountDeactivatedException(String message) {
    super(message);
  }
}
