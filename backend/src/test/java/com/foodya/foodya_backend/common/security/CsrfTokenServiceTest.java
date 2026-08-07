package com.foodya.foodya_backend.common.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class CsrfTokenServiceTest {

    private CsrfTokenService csrfTokenService;

    @BeforeEach
    void setUp() {
        csrfTokenService = new CsrfTokenService();
        String secret = Base64.getEncoder().encodeToString("unit-test-signing-secret-value".getBytes());
        ReflectionTestUtils.setField(csrfTokenService, "jwtSecret", secret);
    }

    @Test
    void derivesTheSameTokenForTheSameRefreshToken() {
        String a = csrfTokenService.deriveToken("refresh-token-abc");
        String b = csrfTokenService.deriveToken("refresh-token-abc");
        assertThat(a).isEqualTo(b);
    }

    @Test
    void derivesDifferentTokensForDifferentRefreshTokens() {
        String a = csrfTokenService.deriveToken("refresh-token-abc");
        String b = csrfTokenService.deriveToken("refresh-token-xyz");
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void isValidWhenCandidateMatchesDerivedToken() {
        String refreshToken = "refresh-token-abc";
        String derived = csrfTokenService.deriveToken(refreshToken);
        assertThat(csrfTokenService.isValid(refreshToken, derived)).isTrue();
    }

    @Test
    void isInvalidWhenCandidateDoesNotMatch() {
        String refreshToken = "refresh-token-abc";
        assertThat(csrfTokenService.isValid(refreshToken, "some-other-value")).isFalse();
    }

    @Test
    void isInvalidWhenCandidateIsTamperedDerivedToken() {
        String refreshToken = "refresh-token-abc";
        String derived = csrfTokenService.deriveToken(refreshToken);
        String tampered = derived.substring(0, derived.length() - 1) + (derived.endsWith("A") ? "B" : "A");
        assertThat(csrfTokenService.isValid(refreshToken, tampered)).isFalse();
    }

    @Test
    void isInvalidWhenEitherSideIsBlank() {
        String refreshToken = "refresh-token-abc";
        String derived = csrfTokenService.deriveToken(refreshToken);

        assertThat(csrfTokenService.isValid(null, derived)).isFalse();
        assertThat(csrfTokenService.isValid(refreshToken, null)).isFalse();
        assertThat(csrfTokenService.isValid("", derived)).isFalse();
        assertThat(csrfTokenService.isValid(refreshToken, "")).isFalse();
    }
}
