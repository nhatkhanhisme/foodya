package com.foodya.foodya_backend.user.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.foodya.foodya_backend.user.dto.UserProfileResponse;
import com.foodya.foodya_backend.user.model.User;
import com.foodya.foodya_backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserProfileResponse getCurrentUserProfile() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found" + username));
    return mapToUserProfileResponse(user);
  }

  public UserProfileResponse getUserById(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    return mapToUserProfileResponse(user);
  }

  private UserProfileResponse mapToUserProfileResponse(User user) {
    return UserProfileResponse.builder()
        .id(user.getId())
        .username(user.getUsername())
        .email(user.getEmail())
        .fullName(user.getFullName())
        .phoneNumber(user.getPhoneNumber())
        .role(user.getRole())
        .isActive(user.isActive())
        .isEmailVerified(user.getIsEmailVerified())
        .lastLoginAt(user.getLastLoginAt())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }
}
