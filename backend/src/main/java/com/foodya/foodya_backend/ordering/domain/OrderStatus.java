package com.foodya.foodya_backend.ordering.domain;

public enum OrderStatus {
  PENDING,
  AWAITING_PAYMENT,
  CONFIRMED,
  REJECTED,
  READY_FOR_PICKUP,
  PICKED_UP,
  DELIVERED,
  CANCELLED
}
