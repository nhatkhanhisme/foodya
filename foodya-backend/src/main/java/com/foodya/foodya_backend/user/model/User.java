package com.foodya.foodya_backend.user.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table (name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @NotBlank (message = "Username is mandatory")
    @Column (unique = true, nullable = false)
    private String username;

    @NotBlank (message = "Email is mandatory")
    @Email (message = "Email should be valid")
    @Column (unique = true, nullable = false)
    private String email;

    @NotBlank (message = "Password is mandatory")
    @Size (min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank (message = "Full name is mandatory")
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private Role role = Role.CUSTOMER;

    @NotBlank (message = "Phone number is mandatory")
    @Column (unique = true)
    private String phoneNumber;

    @Column (nullable = false)
    private boolean  isPhoneNumberVerified = false;

    // Account Status
    @Column (nullable = false)
    private boolean isActive = true;

    @Column (nullable = false)
    private Boolean isEmailVerified = false;

    // Timestamps
    @CreationTimestamp
    @Column (updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column (updatable = true)
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

    @Column (nullable = false)
    private boolean accountLocked = false;

    // Profile Image URL
    private String profileImageUrl;
}
