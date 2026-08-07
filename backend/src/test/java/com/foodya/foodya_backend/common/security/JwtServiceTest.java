package com.foodya.foodya_backend.common.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final long ACCESS_TTL_MS = 3_600_000L;
    private static final long REFRESH_TTL_MS = 2_592_000_000L;

    private JwtService jwtService;
    private String secret;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        secret = Base64.getEncoder().encodeToString("unit-test-jwt-signing-secret-32b".getBytes());
        ReflectionTestUtils.setField(jwtService, "jwtSecret", secret);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationTime", ACCESS_TTL_MS);
        ReflectionTestUtils.setField(jwtService, "refreshExpirationTime", REFRESH_TTL_MS);
    }

    private Authentication authFor(String username) {
        return new UsernamePasswordAuthenticationToken(username, null);
    }

    private String tokenSignedWithOtherKey() {
        Key otherKey = Keys.hmacShaKeyFor("a-completely-different-secret-key".getBytes());
        return Jwts.builder()
                .setSubject("nguyenvana")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TTL_MS))
                .signWith(otherKey, SignatureAlgorithm.HS256)
                .compact();
    }

    private String expiredToken() {
        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        Date past = new Date(System.currentTimeMillis() - 10_000L);
        return Jwts.builder()
                .setSubject("nguyenvana")
                .setIssuedAt(new Date(past.getTime() - 1_000L))
                .setExpiration(past)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void generatedAccessTokenIsValidAndTypedAccess() {
        String token = jwtService.generateToken(authFor("nguyenvana"));

        assertThat(jwtService.validateToken(token)).isTrue();
        assertThat(jwtService.isTokenType(token, TokenType.ACCESS)).isTrue();
        assertThat(jwtService.isTokenType(token, TokenType.REFRESH)).isFalse();
        assertThat(jwtService.extractUsername(token)).isEqualTo("nguyenvana");
    }

    @Test
    void generatedRefreshTokenIsValidAndTypedRefresh() {
        String token = jwtService.generateRefreshToken(authFor("nguyenvana"));

        assertThat(jwtService.validateToken(token)).isTrue();
        assertThat(jwtService.isTokenType(token, TokenType.REFRESH)).isTrue();
        assertThat(jwtService.isTokenType(token, TokenType.ACCESS)).isFalse();
    }

    @Test
    void accessTokenExpiresAfterConfiguredTtl() {
        long before = System.currentTimeMillis();
        String token = jwtService.generateToken(authFor("nguyenvana"));
        long expiresAt = jwtService.extractExpirationTime(token);

        // JWT "exp" is whole seconds per spec, so jjwt truncates sub-second precision
        assertThat(expiresAt).isGreaterThanOrEqualTo(before + ACCESS_TTL_MS - 1_000L);
        assertThat(expiresAt).isLessThan(before + ACCESS_TTL_MS + 5_000L);
    }

    @Test
    void refreshTokenExpiresAfterConfiguredTtl() {
        long before = System.currentTimeMillis();
        String token = jwtService.generateRefreshToken(authFor("nguyenvana"));
        long expiresAt = jwtService.extractExpirationTime(token);

        assertThat(expiresAt).isGreaterThanOrEqualTo(before + REFRESH_TTL_MS - 1_000L);
    }

    @Test
    void rejectsExpiredToken() {
        assertThat(jwtService.validateToken(expiredToken())).isFalse();
    }

    @Test
    void rejectsTokenSignedWithDifferentKey() {
        assertThat(jwtService.validateToken(tokenSignedWithOtherKey())).isFalse();
    }

    @Test
    void rejectsMalformedToken() {
        assertThat(jwtService.validateToken("not-a-jwt")).isFalse();
    }

    @Test
    void isTokenTypeReturnsFalseForGarbageInput() {
        assertThat(jwtService.isTokenType("not-a-jwt", TokenType.ACCESS)).isFalse();
    }
}
