package com.foodya.foodya_backend.user.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.foodya.foodya_backend.user.dto.UpdateProfileRequest;
import com.foodya.foodya_backend.user.dto.UserProfileResponse;
import com.foodya.foodya_backend.user.model.User;
import com.foodya.foodya_backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.NonNull;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<UserProfileResponse> getAllUsers() {
    List<User> users = userRepository.findAll();
    return users.stream()
        .map(this::mapToUserProfileResponse)
        .collect(Collectors.toList());
  }

  public void deleteUserById(@NonNull UUID userId) {
    userRepository.deleteById(userId);
  }

  public UserProfileResponse toggleUserActiveStatus(@NonNull UUID userId) {
    User updateUser = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    updateUser.setIsActive(!updateUser.getIsActive());
    userRepository.save(updateUser);
    return mapToUserProfileResponse(updateUser);
  }

  public UserProfileResponse getCurrentUserProfile() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found" + username));
    return mapToUserProfileResponse(user);
  }


  @Transactional
  public UserProfileResponse updateProfile(UpdateProfileRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("User not found:  " + username));

    // Cập nhật Full Name
    if (request.getFullName() != null && !request.getFullName().isBlank()) {
      user.setFullName(request.getFullName());
    }

    // Cập nhật Email (kiểm tra trùng)
    if (request.getEmail() != null && !request.getEmail().isBlank()) {
      if (userRepository.existsByEmail(request.getEmail()) &&
          !user.getEmail().equals(request.getEmail())) {
        throw new RuntimeException("Email already exists");
      }
      user.setEmail(request.getEmail());
      user.setIsEmailVerified(false); // Reset verification
    }

    // Cập nhật Phone (kiểm tra trùng)
    if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
      if (userRepository.existsByPhoneNumber(request.getPhoneNumber()) &&
          !user.getPhoneNumber().equals(request.getPhoneNumber())) {
        throw new RuntimeException("Phone number already exists");
      }
      user.setPhoneNumber(request.getPhoneNumber());
      user.setIsPhoneNumberVerified(false); // Reset verification
    }

    // Cập nhật Profile Image
    if (request.getProfileImageUrl() != null) {
      user.setProfileImageUrl(request.getProfileImageUrl());
    }


    User updatedUser = userRepository.save(user);
    return UserProfileResponse.fromEntity(updatedUser);
  }

  private UserProfileResponse mapToUserProfileResponse(User user) {
    return UserProfileResponse.fromEntity(user);
  }
}
