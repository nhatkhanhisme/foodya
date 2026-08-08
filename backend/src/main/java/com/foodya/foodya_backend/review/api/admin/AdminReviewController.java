package com.foodya.foodya_backend.review.api.admin;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodya.foodya_backend.review.application.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/reviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Reviews", description = "Moderation action from UC-A06 (remove review)")
@SecurityRequirement(name = "bearerAuth")
public class AdminReviewController {

  private final ReviewService reviewService;

  @Operation(summary = "Remove a review", description = "UC-A06 dispute-handling action; recalculates the restaurant's rating")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Review removed")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteReview(@Parameter(description = "Review ID") @PathVariable UUID id) {
    reviewService.deleteReview(id);
    return ResponseEntity.noContent().build();
  }
}
