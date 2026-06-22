package com.foodya.foodya_backend. user.dto;

import java.time.LocalDateTime;
import java. util.UUID;

import com.foodya.foodya_backend.user.model.Role;
import com.foodya.foodya_backend.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;

    @Builder.Default
    private Role role = Role.CUSTOMER;

    private Boolean isActive;
    private Boolean isEmailVerified;
    private Boolean isPhoneNumberVerified;

    // Profile
    private String profileImageUrl;

    // Timestamps
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Convert User entity to UserProfileResponse DTO
     */
    public static UserProfileResponse fromEntity(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user. getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .isEmailVerified(user.getIsEmailVerified())
                .isPhoneNumberVerified(user.getIsPhoneNumberVerified())
                .profileImageUrl(user.getProfileImageUrl())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
