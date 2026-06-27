package com.foodya.foodya_backend.shared.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

  // 400 Bad Request
  VALIDATION_ERROR(
    HttpStatus.BAD_REQUEST,
    "Request validation failed"),
    
  WEBHOOK_INVALID_SIGNATURE(
    HttpStatus.BAD_REQUEST,
    "Invalid webhook signature"),

  // 401 Unauthorized
  AUTH_INVALID_CREDENTIALS(
    HttpStatus.UNAUTHORIZED,
    "Invalid email or password"),
  AUTH_TOKEN_REVOKED(
    HttpStatus.UNAUTHORIZED,
    "Authentication token is no longer valid"),

  // 402 Payment Required
  PAYMENT_FAILED(
    HttpStatus.PAYMENT_REQUIRED,
    "Payment could not be completed"),

  // 403 Forbidden
  AUTH_ACCOUNT_BANNED(
    HttpStatus.FORBIDDEN,
    "Your account has been banned"),
  FORBIDDEN(
    HttpStatus.FORBIDDEN,
    "You do not have permission to access this resource"),

  // 404 Not Found
  RESOURCE_NOT_FOUND(
    HttpStatus.NOT_FOUND,
    "Requested resource not found"),

  // 409 Conflict
  AUTH_EMAIL_TAKEN(
    HttpStatus.CONFLICT,
    "Email is already exists"),
  AUTH_USERNAME_TAKEN(
    HttpStatus.CONFLICT,
    "Username is already exists"),
  JOB_ALREADY_TAKEN(
    HttpStatus.CONFLICT,
    "This job is already taken by another shipper"),
  DUPLICATE_RESOURCE(
    HttpStatus.CONFLICT,
    "Resource already exists"),

  // 422 Unprocessable Entity
  RESTAURANT_CLOSED(
    HttpStatus.UNPROCESSABLE_ENTITY,
    "Restaurant is currently closed"),
  RESTAURANT_SUSPENDED(
    HttpStatus.UNPROCESSABLE_ENTITY,
    "Restaurant is currently suspended"),
  ITEMS_UNAVAILABLE(
    HttpStatus.UNPROCESSABLE_ENTITY,
    "One or more items are unavailable"),
  ORDER_NOT_CANCELLABLE(
    HttpStatus.UNPROCESSABLE_ENTITY,
    "Order can no longer be cancelled"),
  INVALID_ORDER_TRANSITION(
    HttpStatus.UNPROCESSABLE_ENTITY,
    "Invalid order status transition"),
  REVIEW_ALREADY_EXISTS(
    HttpStatus.UNPROCESSABLE_ENTITY,
    "You already submitted a review for this order"),
  ADDRESS_LAST_ONE(
    HttpStatus.UNPROCESSABLE_ENTITY,
    "Cannot remove the last remaining address"),
  PAYMENT_TIMEOUT(
    HttpStatus.UNPROCESSABLE_ENTITY,
    "Payment session has expired"),
  SHIPPER_NOT_APPROVED(
    HttpStatus.UNPROCESSABLE_ENTITY,
    "Shipper account is not approved"),

  // 429 Too Many Requests
  RATE_LIMIT_EXCEEDED(
    HttpStatus.TOO_MANY_REQUESTS,
    "Too many requests, please try again later"),

  // 500 Internal Server Error
  INTERNAL_ERROR(
    HttpStatus.INTERNAL_SERVER_ERROR,
    "An unexpected error occurred");

  private final HttpStatus status;
  private final String defaultMessage;

  ErrorCode(HttpStatus status, String defaultMessage) {
    this.status = status;
    this.defaultMessage = defaultMessage;
  }
}
