package com.foodya.foodya_backend.admin.controller;

import com.foodya.foodya_backend.user.dto.UserProfileResponse;
import com.foodya.foodya_backend.user.service.UserService;
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
@SecurityRequirement(name = "BearerAuth")
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "Get all users", description = "Admin only - Retrieve all users")
    @GetMapping
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Delete user", description = "Admin only - Permanently delete user")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUserById(userId);
        return ResponseEntity. noContent().build();
    }

    @Operation(summary = "Toggle user status", description = "Admin only - Enable/disable user account")
    @PatchMapping("/{userId}/toggle-active")
    public ResponseEntity<UserProfileResponse> toggleUserActive(@PathVariable UUID userId) {
        UserProfileResponse updatedUser = userService.toggleUserActiveStatus(userId);
        return ResponseEntity.ok(updatedUser);
    }
}
