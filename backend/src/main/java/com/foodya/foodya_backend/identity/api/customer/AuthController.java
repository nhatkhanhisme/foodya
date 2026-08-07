package com.foodya.foodya_backend.identity.api.customer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodya.foodya_backend.identity.api.dto.ChangePasswordRequest;
import com.foodya.foodya_backend.identity.api.dto.JwtAuthResponse;
import com.foodya.foodya_backend.identity.api.dto.LoginRequest;
import com.foodya.foodya_backend.identity.api.dto.RefreshTokenRequest;
import com.foodya.foodya_backend.identity.api.dto.RegisterRequest;
import com.foodya.foodya_backend.identity.application.AuthService;
import com.foodya.foodya_backend.common.exception.ForbiddenException;
import com.foodya.foodya_backend.common.exception.ValidationException;
import com.foodya.foodya_backend.common.security.AuthCookieService;
import com.foodya.foodya_backend.common.security.CsrfTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication and authorization APIs for user registration, login, and token management")
public class AuthController {

  private final AuthService authService;
  private final AuthCookieService authCookieService;
  private final CsrfTokenService csrfTokenService;

  @Operation(
      summary = "Register new user",
      description = "Create a new user account with username, email, password, and other required information. " +
                    "Phone number will be automatically normalized to international format (+84...). " +
                    "Web clients: the refresh token is also set as an HttpOnly cookie — ignore the refreshToken field in the body and rely on the cookie instead."
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
  public ResponseEntity<JwtAuthResponse> register(
      @Valid @RequestBody RegisterRequest registerRequest, HttpServletResponse httpResponse) {
    JwtAuthResponse response = authService.registerUser(registerRequest);
    setAuthCookies(httpResponse, response);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @Operation(
      summary = "User login",
      description = "Authenticate user with username and password. Returns access token and refresh token upon successful authentication. " +
                    "Web clients: the refresh token is also set as an HttpOnly cookie — ignore the refreshToken field in the body and rely on the cookie instead."
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
  public ResponseEntity<JwtAuthResponse> login(
      @Valid @RequestBody LoginRequest loginRequest, HttpServletResponse httpResponse) {
    JwtAuthResponse response = authService.login(loginRequest);
    setAuthCookies(httpResponse, response);
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Refresh access token",
      description = "Generate a new access token using a valid refresh token. The refresh token remains unchanged. " +
                    "Web clients: send no body — the refresh token is read from its HttpOnly cookie, and the X-XSRF-TOKEN " +
                    "header (value from the readable XSRF-TOKEN cookie) is required as CSRF protection. " +
                    "Mobile clients: pass the refresh token in the body instead; no CSRF header needed."
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
  @SecurityRequirements()
  @PostMapping("/refresh")
  public ResponseEntity<JwtAuthResponse> refreshToken(
      @CookieValue(value = AuthCookieService.REFRESH_COOKIE_NAME, required = false) String cookieRefreshToken,
      @RequestBody(required = false) RefreshTokenRequest request,
      @Parameter(hidden = true) @RequestHeader(value = "X-XSRF-TOKEN", required = false) String csrfHeader,
      HttpServletResponse httpResponse) {

    boolean fromCookie = StringUtils.hasText(cookieRefreshToken);
    String refreshToken = fromCookie ? cookieRefreshToken
        : (request != null ? request.getRefreshToken() : null);

    if (!StringUtils.hasText(refreshToken)) {
      throw new ValidationException( "Refresh token is required");
    }
    if (fromCookie && !csrfTokenService.isValid(cookieRefreshToken, csrfHeader)) {
      throw new ForbiddenException( "Missing or invalid CSRF token");
    }

    JwtAuthResponse response = authService.refreshToken(refreshToken);
    if (fromCookie) {
      setAuthCookies(httpResponse, response);
    }
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Logout", description = "Revoke access and refresh tokens. Both tokens will be blacklisted immediately. " +
      "Web clients also need a valid X-XSRF-TOKEN header; the auth cookies are cleared on success.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Logged out, tokens revoked"),
      @ApiResponse(responseCode = "401", description = "Missing or invalid access token")
  })
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      @RequestHeader("Authorization") String authHeader,
      @CookieValue(value = AuthCookieService.REFRESH_COOKIE_NAME, required = false) String cookieRefreshToken,
      @RequestBody(required = false) RefreshTokenRequest request,
      @Parameter(hidden = true) @RequestHeader(value = "X-XSRF-TOKEN", required = false) String csrfHeader,
      HttpServletResponse httpResponse) {

    boolean fromCookie = StringUtils.hasText(cookieRefreshToken);
    if (fromCookie && !csrfTokenService.isValid(cookieRefreshToken, csrfHeader)) {
      throw new ForbiddenException( "Missing or invalid CSRF token");
    }

    String accessToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
    String refreshToken = fromCookie ? cookieRefreshToken : (request != null ? request.getRefreshToken() : null);
    authService.logout(accessToken, refreshToken);
    authCookieService.clearAuthCookies(httpResponse);
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

    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    authService.changePassword(username, request);

    Map<String, String> response = new HashMap<>();
    response.put("message", "Password changed successfully");

    return ResponseEntity.ok(response);
  }

  private void setAuthCookies(HttpServletResponse httpResponse, JwtAuthResponse response) {
    String csrfToken = csrfTokenService.deriveToken(response.getRefreshToken());
    authCookieService.setAuthCookies(
        httpResponse, response.getRefreshToken(), csrfToken, response.getRefreshTokenExpiresIn());
  }

}
