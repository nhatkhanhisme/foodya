package com.foodya.foodya_backend.user.service;

import com.foodya.foodya_backend.shared.exception.AppException;
import com.foodya.foodya_backend.shared.exception.ErrorCode;
import com.foodya.foodya_backend.user.dto.UserProfileResponse;
import com.foodya.foodya_backend.user.model.User;
import com.foodya.foodya_backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User not found: " + username));
        return UserProfileResponse.fromEntity(user);
    }

}
