package com.foodya.foodya_backend.review.application;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodya.foodya_backend.common.exception.ForbiddenException;
import com.foodya.foodya_backend.common.exception.ResourceNotFoundException;
import com.foodya.foodya_backend.common.exception.ReviewAlreadyExistsException;
import com.foodya.foodya_backend.common.exception.ValidationException;
import com.foodya.foodya_backend.identity.application.AuthService;
import com.foodya.foodya_backend.ordering.api.dto.OrderResponse;
import com.foodya.foodya_backend.ordering.application.OrderService;
import com.foodya.foodya_backend.ordering.domain.OrderStatus;
import com.foodya.foodya_backend.review.api.dto.ReviewRequest;
import com.foodya.foodya_backend.review.api.dto.ReviewResponse;
import com.foodya.foodya_backend.review.domain.Review;
import com.foodya.foodya_backend.review.domain.event.RestaurantRatingChangedEvent;
import com.foodya.foodya_backend.review.persistence.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final OrderService orderService;
  private final AuthService authService;
  private final ApplicationEventPublisher eventPublisher;

  @Transactional
  public ReviewResponse createReview(Authentication authentication, UUID orderId, ReviewRequest request) {
    OrderResponse order = orderService.getOrderById(orderId);

    UUID currentUserId = authService.findByUsername(authentication.getName()).getId();
    if (!currentUserId.equals(order.getCustomerId())) {
      throw new ForbiddenException("You are not allowed to review this order");
    }

    // BR-15 precondition (UC-C10)
    if (order.getStatus() != OrderStatus.DELIVERED) {
      throw new ValidationException("Order must be delivered before it can be reviewed");
    }

    // BR-15: at most one review per order
    if (reviewRepository.existsByOrderId(orderId)) {
      throw new ReviewAlreadyExistsException();
    }

    Review review = Review.builder()
        .orderId(orderId)
        .customerId(order.getCustomerId())
        .restaurantId(order.getRestaurantId())
        .rating(request.getRating())
        .comment(request.getComment())
        .build();
    Review saved = reviewRepository.save(review);

    publishRatingChanged(order.getRestaurantId());

    return ReviewResponse.fromEntity(saved);
  }

  @Transactional(readOnly = true)
  public Page<ReviewResponse> getReviewsByRestaurant(UUID restaurantId, Pageable pageable) {
    return reviewRepository.findByRestaurantId(restaurantId, pageable)
        .map(ReviewResponse::fromEntity);
  }

  @Transactional
  public void deleteReview(UUID reviewId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
    UUID restaurantId = review.getRestaurantId();

    reviewRepository.delete(review);

    // BR-16: recalculate after removal too, so a stale high/low rating never
    // outlives the review that produced it
    publishRatingChanged(restaurantId);
  }

  // BR-16: arithmetic mean, recomputed from review's own table and handed to
  // catalog as finished values (see RestaurantRatingChangedEvent) so the
  // listener never has to query back into this module.
  private void publishRatingChanged(UUID restaurantId) {
    double average = reviewRepository.averageRatingByRestaurantId(restaurantId);
    long count = reviewRepository.countByRestaurantId(restaurantId);
    eventPublisher.publishEvent(new RestaurantRatingChangedEvent(restaurantId, average, (int) count));
  }
}
