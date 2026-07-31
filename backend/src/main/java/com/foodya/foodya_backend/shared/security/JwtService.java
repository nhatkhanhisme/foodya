package com.foodya.foodya_backend.shared.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

  private static final String CLAIM_TYPE = "typ";

  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("${app.jwt.expiration-time}")
  private Long jwtExpirationTime;

  @Value("${app.jwt.refresh-expiration-time}")
  private Long refreshExpirationTime;

  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  public String generateToken(Authentication authentication) {
    Date now = new Date();
    return Jwts.builder()
        .setSubject(authentication.getName())
        .claim(CLAIM_TYPE, TokenType.ACCESS.getValue())
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + jwtExpirationTime))
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateRefreshToken(Authentication authentication) {
    Date now = new Date();
    return Jwts.builder()
        .setSubject(authentication.getName())
        .claim(CLAIM_TYPE, TokenType.REFRESH.getValue())
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + refreshExpirationTime))
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact();
  }

  private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key())
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claimsResolver.apply(claims);
  }

  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
      return true;
    } catch (ExpiredJwtException ex) {
      log.warn("JWT expired: {}", ex.getMessage());
    } catch (UnsupportedJwtException ex) {
      log.error("JWT unsupported: {}", ex.getMessage());
    } catch (MalformedJwtException ex) {
      log.error("JWT malformed: {}", ex.getMessage());
    } catch (SignatureException ex) {
      log.error("JWT signature invalid: {}", ex.getMessage());
    } catch (IllegalArgumentException ex) {
      log.error("JWT claims empty: {}", ex.getMessage());
    }
    return false;
  }

  public boolean isTokenType(String token, TokenType type) {
    try {
      return type.getValue().equals(extractClaims(token, claims -> claims.get(CLAIM_TYPE, String.class)));
    } catch (Exception ex) {
      return false;
    }
  }

  public String extractUsername(String token) {
    return extractClaims(token, Claims::getSubject);
  }

  public Long extractExpirationTime(String token) {
    return extractClaims(token, claims -> claims.getExpiration().getTime());
  }
}
