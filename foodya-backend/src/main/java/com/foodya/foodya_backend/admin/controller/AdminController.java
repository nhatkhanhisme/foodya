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
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management APIs")
@SecurityRequirement(name = "BearerAuth")
public class AdminController {

    private final UserService userService;

    @Operation(summary = "Get all users (Admin only)")
    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Delete user by ID (Admin only)")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUserById(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Toggle user active status (Admin only)")
    @PatchMapping("/users/{userId}/toggle-active")
    public ResponseEntity<String> toggleUserActive(@PathVariable UUID userId) {
        UserProfileResponse updateUser = userService.toggleUserActiveStatus(userId);
        String msg = "User active status toggled successfully, current active status is: " + updateUser.getIsActive();
        return ResponseEntity.ok(msg);
    }
}
