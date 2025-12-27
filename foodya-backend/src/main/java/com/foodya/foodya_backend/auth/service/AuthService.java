package com.foodya.foodya_backend.auth.service;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

@Service
public class AuthService {
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
    if (userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
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
    user.setActive(true);
    user.setIsEmailVerified(false);

    userRepository.save(user);

    // Authenticate and generate tokens
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            registerRequest.getUsername(),
            registerRequest.getPassword()
        )
    );

    return generateTokenResponse(authentication);
  }

  public JwtAuthResponse login(LoginRequest loginRequest) {
    // Authenticate user
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getUsername(),
            loginRequest.getPassword()
        )
    );

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
    if (!user.isActive()) {
      throw new AccountDeactivatedException("Account is deactivated");
    }

    // Create new authentication
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        username, null, null
    );

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

    return JwtAuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(expiresIn)
        .refreshTokenExpiresIn(refreshTokenExpiresIn)
        .userId(userId)
        .username(username)
        .build();
  }

  public Long getExpireIn(String token) {
    return jwtService.extractExpirationTime(token) - System.currentTimeMillis();
  }
  public Long getRefreshTokenExpireIn(String refreshToken) {
    return jwtService.extractExpirationTime(refreshToken) - System.currentTimeMillis();
  }
}
