package com.foodya.foodya_backend.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
  private final ErrorCode errorCode;

  public AppException(ErrorCode errorCode) {
    super(errorCode.getDefaultMessage());
    this.errorCode = errorCode;
  }

  public AppException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

}
