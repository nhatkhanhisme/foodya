package com.foodya.foodya_backend.user.controller;

import com.foodya.foodya_backend.user.dto.UpdateProfileRequest;
import com.foodya.foodya_backend.user.dto.UserProfileResponse;
import com.foodya.foodya_backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User", description = "User Profile APIs for mobile app")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @Operation(summary = "Get Current User Profile", description = "Retrieve the profile of the currently authenticated user")
  @ApiResponses(value = {
      // Define responses here
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile", content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
      @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication token"),
      @ApiResponse(responseCode = "404", description = "User not found")
  })
  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {
    UserProfileResponse profile = userService.getCurrentUserProfile();
    return ResponseEntity.ok(profile);
  }

  @Operation(summary = "Update my profile", description = "Update profile information of the current user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Profile updated successfully", content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input - Validation failed or duplicate email/phone"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication token")
  })
  @PutMapping("/me")
  public ResponseEntity<UserProfileResponse> updateProfile(
      @Valid @RequestBody UpdateProfileRequest request) {
    UserProfileResponse profile = userService.updateProfile(request);
    return ResponseEntity.ok(profile);
  }
}
