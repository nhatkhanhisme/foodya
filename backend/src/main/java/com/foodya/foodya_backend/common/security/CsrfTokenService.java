package com.foodya.foodya_backend.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.io.Decoders;
import lombok.SneakyThrows;

/**
 * Stateless double-submit CSRF protection for the refresh-token cookie flow.
 * The token is an HMAC of the refresh token itself (keyed with the JWT
 * signing secret, domain-separated so it can't be mistaken for a JWT), so no
 * server-side storage is needed: whoever holds the refresh token cookie can
 * recompute it, but a cross-site form/script that only rides the cookie
 * cannot, since it can't read the cookie value to derive the header.
 */
@Service
public class CsrfTokenService {

  private static final String HMAC_ALGO = "HmacSHA256";
  private static final String DOMAIN_PREFIX = "csrf:";

  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @SneakyThrows
  public String deriveToken(String refreshToken) {
    Mac mac = Mac.getInstance(HMAC_ALGO);
    mac.init(new SecretKeySpec(Decoders.BASE64.decode(jwtSecret), HMAC_ALGO));
    byte[] signature = mac.doFinal((DOMAIN_PREFIX + refreshToken).getBytes(StandardCharsets.UTF_8));
    return Base64.getUrlEncoder().withoutPadding().encodeToString(signature);
  }

  public boolean isValid(String refreshToken, String candidateToken) {
    if (!StringUtils.hasText(refreshToken) || !StringUtils.hasText(candidateToken)) {
      return false;
    }
    byte[] expected = deriveToken(refreshToken).getBytes(StandardCharsets.UTF_8);
    byte[] candidate = candidateToken.getBytes(StandardCharsets.UTF_8);
    return MessageDigest.isEqual(expected, candidate);
  }
}
