package com.foodya.foodya_backend.user.controller;

import com.foodya.foodya_backend.user.dto.UserProfileResponse;
import com.foodya.foodya_backend.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.annotations.OpenAPI30;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Tag (name = "User Management", description = "APIs for managing users")
@SecurityRequirement (name = "bearerAuth")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @Operation (
    summary = "Get Current User Profile",
    description = "Retrieve the profile of the currently authenticated user"
  )
  @ApiResponses(value = {
    // Define responses here
    @ApiResponse(
      responseCode = "200",
      description = "Successfully retrieved user profile",
      content = @Content(
        schema = @Schema(implementation = UserProfileResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Unauthorized - Invalid or missing authentication token"
    ),
    @ApiResponse(
      responseCode = "404",
      description = "User not found"
    )
  })
  @GetMapping("/me")
  public ResponseEntity< UserProfileResponse>  getCurrentUserProfile() {
    UserProfileResponse profile = userService.getCurrentUserProfile();
    return ResponseEntity.ok(profile);
  }

  @Operation (
    summary = "Get User Profile by ID",
    description = "Retrieve the profile of a user by their unique ID"
  )
  @ApiResponses(value = {
    // Define responses here
    @ApiResponse(
      responseCode = "200",
      description = "Successfully retrieved user profile",
      content = @Content(
        schema = @Schema(implementation = UserProfileResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "401",
      description = "Unauthorized - Invalid or missing authentication token"
    ),
    @ApiResponse(
      responseCode = "404",
      description = "User not found"
    )
  })
  @GetMapping("/{userId}")
  public ResponseEntity<UserProfileResponse> getUserById(@PathVariable Long userId) {
    UserProfileResponse profile = userService.getUserById(userId);
    return ResponseEntity.ok(profile);
  }
}
