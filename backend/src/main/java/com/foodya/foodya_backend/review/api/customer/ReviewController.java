package com.foodya.foodya_backend.review.api.customer;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.foodya.foodya_backend.review.api.dto.ReviewRequest;
import com.foodya.foodya_backend.review.api.dto.ReviewResponse;
import com.foodya.foodya_backend.review.application.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Rate/review a delivered order; browse a restaurant's reviews")
public class ReviewController {

  private final ReviewService reviewService;

  @Operation(summary = "Review an order", description = "Rate and optionally comment on a DELIVERED order (UC-C10)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Review created successfully")
  })
  @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
  @SecurityRequirement(name = "bearerAuth")
  @PostMapping("/api/v1/customers/orders/{id}/review")
  public ResponseEntity<ReviewResponse> createReview(
      Authentication authentication,
      @Parameter(description = "Order ID") @PathVariable UUID id,
      @Valid @RequestBody ReviewRequest request) {
    return new ResponseEntity<>(reviewService.createReview(authentication, id, request), HttpStatus.CREATED);
  }

  @Operation(summary = "List restaurant reviews", description = "Public, paginated (UC-C10)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully")
  })
  @GetMapping("/api/v1/restaurants/{id}/reviews")
  public ResponseEntity<Page<ReviewResponse>> getReviewsByRestaurant(
      @Parameter(description = "Restaurant ID") @PathVariable UUID id,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(reviewService.getReviewsByRestaurant(id, pageable));
  }
}
