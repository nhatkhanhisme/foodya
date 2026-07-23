package com.foodya.foodya_backend.user.api;

import com.foodya.foodya_backend.user.api.dto.UserProfileResponse;
import com.foodya.foodya_backend.user.application.UserCommandService;
import com.foodya.foodya_backend.user.application.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - User Management", description = "⚠️ Admin only - NOT for mobile app")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    @Operation(summary = "Get all users", description = "Admin only - Retrieve all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
            content = @Content(schema = @Schema(implementation = UserProfileResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userQueryService.getAllUsers());
    }

    // No DELETE endpoint on purpose: BR-30 — accounts are BANNED, never deleted
    // (FK constraints from orders/restaurants would break, and history must survive)

    @Operation(summary = "Toggle user status",
        description = "Admin only - Flip account status between ACTIVE and BANNED. Banned users cannot log in.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status toggled; body contains the new state"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{userId}/toggle-active")
    public ResponseEntity<UserProfileResponse> toggleUserActive(
            @Parameter(description = "User ID") @PathVariable UUID userId) {
        return ResponseEntity.ok(userCommandService.toggleUserActiveStatus(userId));
    }
}
