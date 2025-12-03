package com.foodya.foodya_backend.user.dto;

import java.time.LocalDateTime;

import com.foodya.foodya_backend.user.model.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
  private Long id;
  private String username;
  private String email;
  private String fullName;
  private String phoneNumber;
  private Role role = Role.CUSTOMER;
  private Boolean isActive;
  private Boolean  isEmailVerified;
  private LocalDateTime lastLoginAt;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
