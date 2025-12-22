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
@Schema(description = "JWT authentication response containing access and refresh tokens")
public class JwtAuthResponse {

    @Schema(
        description = "JWT access token for API authentication",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNjk..."
    )
    private String accessToken;

    @Schema(
        description = "JWT refresh token for obtaining new access tokens",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNjk..."
    )
    private String refreshToken;

    @Schema(description = "Token type", example = "Bearer", defaultValue = "Bearer")
    private String tokenType;

    @Schema(description = "Token expiration time in milliseconds", example = "3600000")
    private Long expiresIn;
}
