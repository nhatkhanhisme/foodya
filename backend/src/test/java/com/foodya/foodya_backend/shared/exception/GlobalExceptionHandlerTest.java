package com.foodya.foodya_backend.shared.exception;

import com.foodya.foodya_backend.shared.response.ApiResponse;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void badCredentialsMapsTo401NotTheGeneric500Fallback() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleBadCredentials(new BadCredentialsException("bad creds"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getCode()).isEqualTo("AUTH_INVALID_CREDENTIALS");
    }

    @Test
    void disabledAccountMapsTo403() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleAccountStatus(new DisabledException("account disabled"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getCode()).isEqualTo("AUTH_ACCOUNT_BANNED");
    }

    @Test
    void lockedAccountMapsTo403() {
        ResponseEntity<ApiResponse<Void>> response =
                handler.handleAccountStatus(new LockedException("account locked"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getCode()).isEqualTo("AUTH_ACCOUNT_BANNED");
    }
}
