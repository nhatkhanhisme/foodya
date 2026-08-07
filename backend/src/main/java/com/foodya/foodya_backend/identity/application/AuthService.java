package com.foodya.foodya_backend.identity.application;

import java.time.Instant;
import java.util.Set;

import com.foodya.foodya_backend.common.exception.AuthAccountBannedException;
import com.foodya.foodya_backend.common.exception.AuthTokenRevokedException;
import com.foodya.foodya_backend.common.exception.DuplicateResourceException;
import com.foodya.foodya_backend.common.exception.ForbiddenException;
import com.foodya.foodya_backend.common.exception.ResourceNotFoundException;
import com.foodya.foodya_backend.common.exception.ValidationException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.foodya.foodya_backend.identity.api.dto.ChangePasswordRequest;
import com.foodya.foodya_backend.identity.api.dto.JwtAuthResponse;
import com.foodya.foodya_backend.identity.api.dto.LoginRequest;
import com.foodya.foodya_backend.identity.api.dto.RegisterRequest;
import com.foodya.foodya_backend.common.security.JwtService;
import com.foodya.foodya_backend.common.security.TokenType;
import com.foodya.foodya_backend.identity.domain.Role;
import com.foodya.foodya_backend.identity.domain.User;
import com.foodya.foodya_backend.identity.domain.UserStatus;
import com.foodya.foodya_backend.identity.persistence.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {

  private static final Set<String> SELF_REGISTERABLE_ROLES = Set.of("CUSTOMER", "SHIPPER");

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final TokenBlacklistService tokenBlacklistService;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
      AuthenticationManager authenticationManager, JwtService jwtService,
      TokenBlacklistService tokenBlacklistService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.tokenBlacklistService = tokenBlacklistService;
  }

  @Transactional
  public JwtAuthResponse registerUser(RegisterRequest registerRequest) {
    if (userRepository.existsByUsername(registerRequest.getUsername())) {
      throw new DuplicateResourceException( "Username already exists");
    }
    if (userRepository.existsByEmail(registerRequest.getEmail())) {
      throw new DuplicateResourceException( "Email already exists");
    }

    String normalizedPhone = null;
    if (registerRequest.getPhoneNumber() != null && !registerRequest.getPhoneNumber().isBlank()) {
      normalizedPhone = normalizePhoneNumber(registerRequest.getPhoneNumber().trim());
      if (userRepository.existsByPhoneNumber(normalizedPhone)) {
        throw new DuplicateResourceException( "Phone number already exists");
      }
    }

    String requestedRole = registerRequest.getRole() != null
        ? registerRequest.getRole().toUpperCase() : "CUSTOMER";
    if (!SELF_REGISTERABLE_ROLES.contains(requestedRole)) {
      throw new ForbiddenException( "Role " + requestedRole + " cannot be self-assigned");
    }

    User user = new User();
    user.setUsername(registerRequest.getUsername());
    user.setEmail(registerRequest.getEmail());
    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    user.setFullName(registerRequest.getFullName());
    user.setPhoneNumber(normalizedPhone);
    user.setRole(Role.valueOf(requestedRole));
    user.setStatus(UserStatus.ACTIVE);
    user.setIsEmailVerified(false);
    userRepository.save(user);

    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            registerRequest.getUsername(),
            registerRequest.getPassword()));

    return generateTokenResponse(authentication);
  }

  private String normalizePhoneNumber(String phoneNumber) {
    if (phoneNumber.startsWith("0")) {
      return "+84" + phoneNumber.substring(1);
    }
    return phoneNumber.startsWith("+") ? phoneNumber : "+" + phoneNumber;
  }

  public JwtAuthResponse login(LoginRequest loginRequest) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(),
            loginRequest.getPassword()));

    User user = userRepository.findByUsername(loginRequest.getUsername())
        .orElseThrow(() -> new ResourceNotFoundException( "User not found"));
    user.setLastLoginAt(Instant.now());
    userRepository.save(user);

    return generateTokenResponse(authentication);
  }

  public JwtAuthResponse refreshToken(String refreshToken) {
    if (!jwtService.validateToken(refreshToken)) {
      throw new AuthTokenRevokedException( "Invalid refresh token");
    }

    if (!jwtService.isTokenType(refreshToken, TokenType.REFRESH)) {
      throw new AuthTokenRevokedException( "Token is not a refresh token");
    }

    if (tokenBlacklistService.isRevoked(refreshToken)) {
      throw new AuthTokenRevokedException( "Refresh token has been revoked");
    }

    String username = jwtService.extractUsername(refreshToken);

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException( "User not found"));

    if (user.getStatus() == UserStatus.BANNED) {
      throw new AuthAccountBannedException( "Account is deactivated");
    }

    Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
    String newAccessToken = jwtService.generateToken(authentication);

    return JwtAuthResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(getExpireIn(newAccessToken))
        .refreshTokenExpiresIn(getExpireIn(refreshToken))
        .userId(user.getId().toString())
        .username(username)
        .role(user.getRole())
        .build();
  }

  private JwtAuthResponse generateTokenResponse(Authentication authentication) {
    String accessToken = jwtService.generateToken(authentication);
    String refreshToken = jwtService.generateRefreshToken(authentication);
    User user = userRepository.findByUsername(authentication.getName())
        .orElseThrow(() -> new ResourceNotFoundException( "User not found"));

    return JwtAuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(getExpireIn(accessToken))
        .refreshTokenExpiresIn(getExpireIn(refreshToken))
        .userId(user.getId().toString())
        .username(user.getUsername())
        .role(user.getRole())
        .build();
  }

  public void logout(String accessToken, String refreshToken) {
    if (StringUtils.hasText(accessToken) && jwtService.validateToken(accessToken)) {
      tokenBlacklistService.revoke(accessToken);
    }
    if (StringUtils.hasText(refreshToken) && jwtService.validateToken(refreshToken)) {
      tokenBlacklistService.revoke(refreshToken);
    }
    log.info("Tokens revoked on logout");
  }

  public Long getExpireIn(String token) {
    return jwtService.extractExpirationTime(token) - System.currentTimeMillis();
  }

  /**
   * Facade for other modules (e.g. ordering) that need the full User entity for
   * a JPA relationship — avoids exposing UserRepository across the module
   * boundary.
   */
  @Transactional(readOnly = true)
  public User findByUsername(String username) {
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
  }

  @Transactional
  public void changePassword(String username, ChangePasswordRequest request) {
    log.info("Changing password for user: {}", username);

    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      throw new ValidationException( "New password and confirm password do not match");
    }

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException( "User not found"));

    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      throw new ValidationException( "Current password is incorrect");
    }

    if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
      throw new ValidationException( "New password must be different from current password");
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);
    log.info("Password changed for user: {}", username);
  }
}
