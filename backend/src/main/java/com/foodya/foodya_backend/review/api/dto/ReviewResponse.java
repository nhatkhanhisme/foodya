package com.foodya.foodya_backend.review.api.dto;

import java.time.Instant;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.foodya.foodya_backend.review.domain.Review;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing review details")
public class ReviewResponse {

  private UUID id;
  private UUID orderId;
  private UUID customerId;
  private UUID restaurantId;
  private Integer rating;
  private String comment;
  private Instant createdAt;

  public static ReviewResponse fromEntity(Review review) {
    return ReviewResponse.builder()
        .id(review.getId())
        .orderId(review.getOrderId())
        .customerId(review.getCustomerId())
        .restaurantId(review.getRestaurantId())
        .rating(review.getRating())
        .comment(review.getComment())
        .createdAt(review.getCreatedAt())
        .build();
  }
}
