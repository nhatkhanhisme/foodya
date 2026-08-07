package com.foodya.foodya_backend.catalog.api.admin;

import com.foodya.foodya_backend.catalog.application.RestaurantPopularityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/popularity")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Restaurant Popularity", description = "⚠️ Admin only - NOT for mobile app")
@SecurityRequirement(name = "bearerAuth")
public class AdminPopularityController {

    private final RestaurantPopularityService restaurantPopularityService;

    @Operation(summary = "Backfill popularity scores",
            description = "Rebuilds weekly popularity buckets from DELIVERED orders of the last 28 days. "
                    + "Idempotent: buckets are wiped and recounted from the DB, safe to rerun.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Buckets rebuilt; body reports how many orders were counted")
    })
    @PostMapping("/backfill")
    public ResponseEntity<String> backfill() {
        long processed = restaurantPopularityService.backfill();
        return ResponseEntity.ok("Backfilled popularity from " + processed + " delivered orders");
    }
}
