package com.foodya.foodya_backend.user.application;

import com.foodya.foodya_backend.shared.exception.AppException;
import com.foodya.foodya_backend.shared.exception.ErrorCode;
import com.foodya.foodya_backend.user.api.dto.UserProfileResponse;
import com.foodya.foodya_backend.user.domain.Role;
import com.foodya.foodya_backend.user.domain.User;
import com.foodya.foodya_backend.user.persistence.UserRepository;

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
public class UserQueryService {

    private final UserRepository userRepository;

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
