package com.foodya.foodya_backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType="Bearer";
    private Long expiresIn; // access token expiration time in milliseconds

    public JwtAuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
