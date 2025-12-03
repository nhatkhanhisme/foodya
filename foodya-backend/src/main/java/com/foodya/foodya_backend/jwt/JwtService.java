package com.foodya.foodya_backend.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
  // Logger instance
  // for debugging purposes
  private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("${app.jwt.expiration-time}")
  private Long jwtExpirationTime;

  @Value("${app.jwt.refresh-expiration-time}")
  private Long refreshExpirationTime;

  // Get signing key
  private Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
  }

  // Generate token
  public String generateToken(Authentication authentication) {
    String username = authentication.getName();
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + jwtExpirationTime);

    return Jwts.builder()
        .setSubject(username) //
        .setIssuedAt(now)
        .setExpiration(expirationDate)
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact(); // Tra ve token dang String
  }

  // Generate refresh token
  public String generateRefreshToken(Authentication authentication) {
    String username = authentication.getName();
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + refreshExpirationTime);

    return Jwts.builder()
        .setSubject(username) //
        .setIssuedAt(now)
        .setExpiration(expirationDate)
        .signWith(key(), SignatureAlgorithm.HS256)
        .compact(); // Tra ve token dang String
  }
  // Reuseable method to extract claims
  private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = Jwts
        .parserBuilder()
        .setSigningKey(key())
        .build()
        .parseClaimsJws(token) // Validate signature and parse token
        .getBody(); // return claims and get data in body
    return claimsResolver.apply(claims);

  }

  // Validate token
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
          .setSigningKey(key()) // secret key
          .build()
          .parseClaimsJws(token); // if parse is successful, token is valid
                                  // if signature is invalid, exception will be thrown
      return true;
    } catch (ExpiredJwtException ex) {
      logger.warn("JWT token is expired: {}", ex.getMessage());
    } catch (UnsupportedJwtException ex) {
      logger.error("JWT unsupported: {}", ex.getMessage());
    } catch (MalformedJwtException ex) {
      logger.error("JWT malformed: {}", ex.getMessage());
    } catch (IllegalArgumentException ex) {
      logger.error("JWT claims string is empty: {}", ex.getMessage());
    }
    return false;
  }
    // Get username from token
  public String extractUsername(String token) {
    return extractClaims(token, Claims::getSubject);
  }
  //
  public String extractRole(String token) {
    return extractClaims(token, claims -> claims.get("role", String.class));
  }
  public Long extractExpirationTime(String token) {
    return extractClaims(token, claims -> claims.getExpiration().getTime());
  }
}
