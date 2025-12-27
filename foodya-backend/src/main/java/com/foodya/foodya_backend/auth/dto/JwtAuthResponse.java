package com.foodya.foodya_backend.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JWT authentication response")
public class JwtAuthResponse {

    @Schema(
        description = "JWT access token",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String accessToken;

    @Schema(
        description = "JWT refresh token",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
    )
    private String refreshToken;

    @Schema(
        description = "Token type",
        example = "Bearer",
        defaultValue = "Bearer"
    )
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(
        description = "Access token expiration time in milliseconds",
        example = "3600000"
    )
    private Long expiresIn;

    @Schema(
        description = "Refresh token expiration time in milliseconds",
        example = "604800000"
    )
    private Long refreshTokenExpiresIn;

    @Schema(
        description = "User ID",
        example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private String userId;

    @Schema(
        description = "Username",
        example = "nguyenvana"
    )
    private String username;

    @Schema(
        description = "User role",
        example = "CUSTOMER"
    )
    private String role;
}
