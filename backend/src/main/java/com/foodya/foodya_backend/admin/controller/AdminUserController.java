package com.foodya.foodya_backend.admin.controller;

import com.foodya.foodya_backend.user.dto.UserProfileResponse;
import com.foodya.foodya_backend.user.service.UserCommandService;
import com.foodya.foodya_backend.user.service.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
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
    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userQueryService.getAllUsers());
    }

    @Operation(summary = "Delete user", description = "Admin only - Permanently delete user")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userCommandService.deleteUserById(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Toggle user status", description = "Admin only - Enable/disable user account")
    @PatchMapping("/{userId}/toggle-active")
    public ResponseEntity<UserProfileResponse> toggleUserActive(@PathVariable UUID userId) {
        return ResponseEntity.ok(userCommandService.toggleUserActiveStatus(userId));
    }
}
