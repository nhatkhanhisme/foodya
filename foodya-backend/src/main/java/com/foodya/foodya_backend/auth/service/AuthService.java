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
import com.foodya.foodya_backend.user.model.User;
import com.foodya.foodya_backend.user.repository.UserRepository;

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
      throw new RuntimeException("Username already exists");
    }
    if (userRepository.existsByEmail(registerRequest.getEmail())) {
      throw new RuntimeException("Email already exists");
    }
    if (userRepository.existsByPhoneNumber(registerRequest.getPhoneNumber())) {
      throw new RuntimeException("Phone number already exists");
    }

    // Create new user
    User user = new User();
    user.setUsername(registerRequest.getUsername());
    user.setEmail(registerRequest.getEmail());
    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    user.setFullName(registerRequest.getFullName());
    user.setPhoneNumber(registerRequest.getPhoneNumber());
    user.setRole(registerRequest.getRole());
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
        .orElseThrow(() -> new RuntimeException("User not found"));
    user.setLastLoginAt(LocalDateTime.now());
    userRepository.save(user);

    return generateTokenResponse(authentication);
  }

  public JwtAuthResponse refreshToken(RefreshTokenRequest request) {
    String refreshToken = request.getRefreshToken();

    // Validate refresh token
    if (!jwtService.validateToken(refreshToken)) {
      throw new RuntimeException("Invalid refresh token");
    }

    // Extract username from refresh token
    String username = jwtService.extractUsername(refreshToken);

    // Load user
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found"));

    // Check if account is active
    if (!user.isActive()) {
      throw new RuntimeException("Account is deactivated");
    }

    // Create new authentication
    Authentication authentication = new UsernamePasswordAuthenticationToken(
        username, null, null
    );

    // Generate new access token (keep the same refresh token)
    String newAccessToken = jwtService.generateToken(authentication);
    Long expireIn = getExpireIn(newAccessToken);

    return JwtAuthResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(expireIn)
        .build();
  }

  // Helper method to generate token response
  private JwtAuthResponse generateTokenResponse(Authentication authentication) {
    String accessToken = jwtService.generateToken(authentication);
    String refreshToken = jwtService.generateRefreshToken(authentication);
    Long expiresIn = getExpireIn(accessToken);

    return JwtAuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(expiresIn)
        .build();
  }

  public Long getExpireIn(String token) {
    return jwtService.extractExpirationTime(token) - System.currentTimeMillis();
  }
}
