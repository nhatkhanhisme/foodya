package com.foodya.foodya_backend.user.application;

import com.foodya.foodya_backend.shared.exception.AppException;
import com.foodya.foodya_backend.shared.exception.ErrorCode;
import com.foodya.foodya_backend.user.api.dto.UpdateProfileRequest;
import com.foodya.foodya_backend.user.api.dto.UserProfileResponse;
import com.foodya.foodya_backend.auth.domain.Role;
import com.foodya.foodya_backend.auth.domain.User;
import com.foodya.foodya_backend.auth.domain.UserStatus;
import com.foodya.foodya_backend.auth.persistence.UserRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

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

    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserProfileResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        return UserProfileResponse.fromEntity(getCurrentUserEntity());
    }

    /**
     * Facade for other modules that only need the current user's id for
     * authorization checks — avoids exposing the User entity/repository across
     * the module boundary.
     */
    @Transactional(readOnly = true)
    public UUID getCurrentUserId() {
        return getCurrentUserEntity().getId();
    }

    @Transactional(readOnly = true)
    public Role getCurrentUserRole() {
        return getCurrentUserEntity().getRole();
    }

    private User getCurrentUserEntity() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found: " + username));
    }

}
