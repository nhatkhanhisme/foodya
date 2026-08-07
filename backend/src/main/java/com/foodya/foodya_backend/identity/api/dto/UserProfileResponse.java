package com.foodya.foodya_backend.identity.api.dto;

import java.time.Instant;
import java. util.UUID;

import com.foodya.foodya_backend.identity.domain.Role;
import com.foodya.foodya_backend.identity.domain.User;
import com.foodya.foodya_backend.identity.domain.UserStatus;

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

    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;
    private Boolean isEmailVerified;
    private Boolean isPhoneNumberVerified;

    private String profileImageUrl;

    private Instant lastLoginAt;
    private Instant createdAt;
    private Instant updatedAt;

    public static UserProfileResponse fromEntity(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user. getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .status(user.getStatus())
                .isEmailVerified(user.getIsEmailVerified())
                .isPhoneNumberVerified(user.getIsPhoneNumberVerified())
                .profileImageUrl(user.getProfileImageUrl())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
