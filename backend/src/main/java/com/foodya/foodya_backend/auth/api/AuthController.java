package com.foodya.foodya_backend.auth.api;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodya.foodya_backend.auth.api.dto.ChangePasswordRequest;
import com.foodya.foodya_backend.auth.api.dto.JwtAuthResponse;
import com.foodya.foodya_backend.auth.api.dto.LoginRequest;
import com.foodya.foodya_backend.auth.api.dto.RefreshTokenRequest;
import com.foodya.foodya_backend.auth.api.dto.RegisterRequest;
import com.foodya.foodya_backend.auth.application.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization APIs for user registration, login, and token management")
public class AuthController {

  private final AuthService authService;

  @Operation(
      summary = "Register new user",
      description = "Create a new user account with username, email, password, and other required information. " +
                    "Phone number will be automatically normalized to international format (+84...)."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "201",
          description = "User registered successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = JwtAuthResponse.class)
          )
      )
  })
  // Empty @SecurityRequirements clears the global bearerAuth lock in Swagger UI
  @SecurityRequirements()
  @PostMapping("/register")
  public ResponseEntity<JwtAuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
    JwtAuthResponse response = authService.registerUser(registerRequest);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Operation(
      summary = "User login",
      description = "Authenticate user with username and password. Returns access token and refresh token upon successful authentication."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Login successful",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = JwtAuthResponse.class)
          )
      )
  })
  @SecurityRequirements()
  @PostMapping("/login")
  public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    JwtAuthResponse response = authService.login(loginRequest);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Refresh access token",
      description = "Generate a new access token using a valid refresh token. The refresh token remains unchanged."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Token refreshed successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = JwtAuthResponse.class)
          )
      )
  })
  // Auth is the refresh token in the body, not the (possibly expired) access token
  @SecurityRequirements()
  @PostMapping("/refresh")
  public ResponseEntity<JwtAuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
    JwtAuthResponse response = authService.refreshToken(request);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Logout", description = "Revoke access and refresh tokens. Both tokens will be blacklisted immediately.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Logged out, tokens revoked"),
      @ApiResponse(responseCode = "401", description = "Missing or invalid access token")
  })
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      @RequestHeader("Authorization") String authHeader,
      @RequestBody(required = false) RefreshTokenRequest request) {

    String accessToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
    String refreshToken = request != null ? request.getRefreshToken() : null;
    authService.logout(accessToken, refreshToken);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Change password",
      description = "Change password for authenticated user. Requires current password for verification."
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Password changed successfully",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  value = "{\"message\": \"Password changed successfully\"}"
              )
          )
      )
  })
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/change-password")
  public ResponseEntity<Map<String, String>> changePassword(
      @Valid @RequestBody ChangePasswordRequest request) {

    // Get current authenticated user
    String username = SecurityContextHolder.getContext().getAuthentication().getName();

    // Change password
    authService.changePassword(username, request);

    // Return success message
    Map<String, String> response = new HashMap<>();
    response.put("message", "Password changed successfully");

    return ResponseEntity.ok(response);
  }

}
