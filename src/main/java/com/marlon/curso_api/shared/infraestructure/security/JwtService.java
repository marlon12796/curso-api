package com.marlon.curso_api.shared.infraestructure.security;

import com.marlon.curso_api.identity.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  private static final String ROLE_CLAIM = "role";
  private final JwtProperties properties;

  public JwtService(JwtProperties properties) {
    this.properties = properties;
  }

  private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
  }

  // Genera un token JWT a partir del usuario autenticado
  public String generateToken(User user) {
    return io.jsonwebtoken.Jwts.builder()
        .subject(user.email())
        .claim(ROLE_CLAIM, user.role().name())
        .issuedAt(new java.util.Date())
        .expiration(new java.util.Date(System.currentTimeMillis() + properties.expirationMs()))
        .signWith(getSigningKey())
        .compact();
  }

  public String extractEmail(String token) {
    return extractAllClaims(token).getSubject();
  }

  public String extractRole(String token) {
    return extractAllClaims(token).get(ROLE_CLAIM, String.class);
  }

  // 3. Valida si el token es legítimo y no ha vencido
  public boolean isTokenValid(String token) {
    try {
      return !isTokenExpired(token);
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isTokenExpired(String token) {
    return extractAllClaims(token).getExpiration().before(new Date());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
  }
}
