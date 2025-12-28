package com.foodya.foodya_backend.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodya.foodya_backend.auth.dto.ChangePasswordRequest;
import com.foodya.foodya_backend.auth.dto.JwtAuthResponse;
import com.foodya.foodya_backend.auth.dto.LoginRequest;
import com.foodya.foodya_backend.auth.dto.RefreshTokenRequest;
import com.foodya.foodya_backend.auth.dto.RegisterRequest;
import com.foodya.foodya_backend.jwt.JwtService;
import com.foodya.foodya_backend.user.model.Role;
import com.foodya.foodya_backend.user.model.User;
import com.foodya.foodya_backend.user.repository.UserRepository;
import com.foodya.foodya_backend.utils.exception.business.AccountDeactivatedException;
import com.foodya.foodya_backend.utils.exception.business.DuplicateResourceException;
import com.foodya.foodya_backend.utils.exception.business.ResourceNotFoundException;
import com.foodya.foodya_backend.utils.exception.security.UnauthorizedException;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Slf4j
public class AuthService {
  private static final Logger log = LoggerFactory.getLogger(AuthService.class);
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  @Transactional
  public JwtAuthResponse registerUser(RegisterRequest registerRequest) {
    // Validate unique constraints
    if (userRepository.existsByUsername(registerRequest.getUsername())) {
      throw new DuplicateResourceException("Username already exists");
    }
    if (userRepository.existsByEmail(registerRequest.getEmail())) {
      throw new DuplicateResourceException("Email already exists");
    }
    if (registerRequest.getPhoneNumber() != null
        && userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
      throw new DuplicateResourceException("Phone number already exists");
    }

    // Create new user
    User user = new User();
    user.setUsername(registerRequest.getUsername());
    user.setEmail(registerRequest.getEmail());
    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    user.setFullName(registerRequest.getFullName());
    user.setPhoneNumber(registerRequest.getPhoneNumber());
    user.setRole(Role.valueOf(registerRequest.getRole().toUpperCase()));
    user.setIsActive(true);
    user.setIsEmailVerified(false);

    userRepository.save(user);

    // Authenticate and generate tokens
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            registerRequest.getUsername(),
            registerRequest.getPassword()));

    return generateTokenResponse(authentication);
  }

  public JwtAuthResponse login(LoginRequest loginRequest) {
    // Authenticate user
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(),
            loginRequest.getPassword()));

    // Update last login time
    User user = userRepository.findByUsername(loginRequest.getUsername())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    user.setLastLoginAt(LocalDateTime.now());
    userRepository.save(user);

    return generateTokenResponse(authentication);
  }

  public JwtAuthResponse refreshToken(RefreshTokenRequest request) {
    String refreshToken = request.getRefreshToken();

    // Validate refresh token
    if (!jwtService.validateToken(refreshToken)) {
      throw new UnauthorizedException("Invalid refresh token");
    }

    // Extract username from refresh token
    String username = jwtService.extractUsername(refreshToken);

    // Load user
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Check if account is active
    if (!user.getIsActive()) {
      throw new AccountDeactivatedException("Account is deactivated");
    }

    // Create new authentication
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        username, null, null);

    // Generate new access token (keep the same refresh token)
    String newAccessToken = jwtService.generateToken(authentication);
    Long expireIn = getExpireIn(newAccessToken);
    Long refreshExpiresIn = getRefreshTokenExpireIn(refreshToken);
    String userId = user.getId().toString();

    return JwtAuthResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(expireIn)
        .refreshTokenExpiresIn(refreshExpiresIn)
        .userId(userId)
        .username(username)
        .build();
  }

  // Helper method to generate token response
  private JwtAuthResponse generateTokenResponse(Authentication authentication) {
    String accessToken = jwtService.generateToken(authentication);
    String refreshToken = jwtService.generateRefreshToken(authentication);
    Long expiresIn = getExpireIn(accessToken);
    Long refreshTokenExpiresIn = getRefreshTokenExpireIn(refreshToken);
    String userId = userRepository.findByUsername(authentication.getName())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"))
        .getId()
        .toString();
    String username = authentication.getName();
    Role role = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"))
        .getRole();

    return JwtAuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(expiresIn)
        .refreshTokenExpiresIn(refreshTokenExpiresIn)
        .userId(userId)
        .username(username)
        .role(role)
        .build();
  }

  public Long getExpireIn(String token) {
    return jwtService.extractExpirationTime(token) - System.currentTimeMillis();
  }

  public Long getRefreshTokenExpireIn(String refreshToken) {
    return jwtService.extractExpirationTime(refreshToken) - System.currentTimeMillis();
  }

  /**
   * Change password for authenticated user
   */
  @Transactional
  public void changePassword(String username, ChangePasswordRequest request) {
    log.info("User {} is changing password", username);

    // Validate new password matches confirm password
    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      log.warn("Password confirmation mismatch for user:  {}", username);
      throw new RuntimeException("New password and confirm password do not match");
    }

    // Find user
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          log.error("User not found:  {}", username);
          return new RuntimeException("User not found");
        });

    // Verify current password
    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      log.warn("Incorrect current password for user: {}", username);
      throw new RuntimeException("Current password is incorrect");
    }

    // Check new password is different from current
    if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
      log.warn("New password same as old password for user: {}", username);
      throw new IllegalArgumentException("New password must be different from current password");
    }

    // Update password
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);

    log.info("Password changed successfully for user: {}", username);
  }
}
