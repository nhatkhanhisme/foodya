package com.foodya.foodya_backend.user.service;

import com.foodya.foodya_backend.shared.exception.AppException;
import com.foodya.foodya_backend.shared.exception.ErrorCode;
import com.foodya.foodya_backend.user.dto.UpdateProfileRequest;
import com.foodya.foodya_backend.user.dto.UserProfileResponse;
import com.foodya.foodya_backend.user.model.User;
import com.foodya.foodya_backend.user.model.UserStatus;
import com.foodya.foodya_backend.user.repository.UserRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandService {

    private final UserRepository userRepository;

    @Transactional
    public UserProfileResponse toggleUserActiveStatus(@NonNull UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found with id: " + userId));
        user.setStatus(user.getStatus() == UserStatus.ACTIVE ? UserStatus.BANNED : UserStatus.ACTIVE);
        userRepository.save(user);
        return UserProfileResponse.fromEntity(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found: " + username));

        log.info("Updating profile for user: {}", username);

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim().toLowerCase();
            if (!newEmail.equals(user.getEmail().toLowerCase())) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new AppException(ErrorCode.DUPLICATE_RESOURCE, "Email already exists");
                }
                user.setEmail(newEmail);
                user.setIsEmailVerified(false);
            }
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            String newPhone = request.getPhoneNumber().trim();
            if (!newPhone.equals(user.getPhoneNumber())) {
                if (userRepository.existsByPhoneNumber(newPhone)) {
                    throw new AppException(ErrorCode.DUPLICATE_RESOURCE, "Phone number already exists");
                }
                user.setPhoneNumber(newPhone);
                user.setIsPhoneNumberVerified(false);
            }
        }

        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(
                    request.getProfileImageUrl().isEmpty() ? null : request.getProfileImageUrl().trim());
        }

        return UserProfileResponse.fromEntity(userRepository.save(user));
    }
}
