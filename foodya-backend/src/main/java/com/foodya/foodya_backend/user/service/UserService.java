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
import com.foodya.foodya_backend.utils.exception.business.DuplicateResourceException;
import com.foodya.foodya_backend.utils.exception.business.ResourceNotFoundException;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
    if (userRepository.findById(userId).isEmpty()) {
      throw new ResourceNotFoundException("User not found with id: " + userId);
    }
    if (userRepository.findById(userId).isEmpty()) {
      throw new ResourceNotFoundException("User not found with id: " + userId);
    }
    userRepository.deleteById(userId);
  }

  public UserProfileResponse toggleUserActiveStatus(@NonNull UUID userId) {
    User updateUser = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    updateUser.setIsActive(!updateUser.getIsActive());
    userRepository.save(updateUser);
    return mapToUserProfileResponse(updateUser);
  }

  public UserProfileResponse getCurrentUserProfile() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    return mapToUserProfileResponse(user);
  }

  @Transactional
  public UserProfileResponse updateProfile(@Valid UpdateProfileRequest request) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

    // Debug logging
    log.info("Received updateProfile request for user: {}", username);
    log.info("Request object: {}", request);
    log.info("FullName: '{}', Email: '{}', PhoneNumber: '{}', ProfileImageUrl: '{}'",
        request.getFullName(), request.getEmail(), request.getPhoneNumber(), request.getProfileImageUrl());
    // 1. Cập nhật Full Name (Chuẩn hóa trim)
    if (request.getFullName() != null && !request.getFullName().isBlank()) {
      log.info("Updating full name: ");
      user.setFullName(request.getFullName().trim());
    }

    // 2. Cập nhật Email
    if (request.getEmail() != null && !request.getEmail().isBlank()) {
      // Chuẩn hóa về chữ thường + xóa khoảng trắng
      String newEmail = request.getEmail().trim().toLowerCase();
      String currentEmail = user.getEmail().toLowerCase();

      if (!newEmail.equals(currentEmail)) {
        // Chỉ check DB nếu email thực sự thay đổi
        if (userRepository.existsByEmail(newEmail)) {
          throw new DuplicateResourceException("Email already exists");
        }
        user.setEmail(newEmail);
        user.setIsEmailVerified(false);
      }
    }

    // 3. Cập nhật Phone với normalization
    if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
      String newPhone = request.getPhoneNumber().trim();

      // Normalize the phone number
      try {
        newPhone = com.foodya.foodya_backend.utils.phone.PhoneNumberUtil.normalize(newPhone, "VN");
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Invalid phone number format: " + e.getMessage());
      }

      if (!newPhone.equals(user.getPhoneNumber())) {
        if (userRepository.existsByPhoneNumber(newPhone)) {
          throw new DuplicateResourceException("Phone number already exists");
        }
        user.setPhoneNumber(newPhone);
        user.setIsPhoneNumberVerified(false);
      }
    }

    // 4. Cập nhật Profile Image
    // Logic: Nếu gửi chuỗi rỗng "" nghĩa là muốn xóa ảnh. Nếu null thì bỏ qua.
    if (request.getProfileImageUrl() != null) {
      if (request.getProfileImageUrl().isEmpty()) {
        user.setProfileImageUrl(null); // Xóa ảnh
      } else {
        user.setProfileImageUrl(request.getProfileImageUrl().trim()); // Cập nhật ảnh mới
      }
    }

    User updatedUser = userRepository.save(user);
    log.info("Updated user: " + user.getUsername());
    log.info("imported user: " + updatedUser.getUsername());
    return UserProfileResponse.fromEntity(updatedUser);
  }

  private UserProfileResponse mapToUserProfileResponse(User user) {
    return UserProfileResponse.fromEntity(user);
  }
}
