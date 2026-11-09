package com.marlon.curso_api.shared.infraestructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.marlon.curso_api.identity.domain.Role;
import com.marlon.curso_api.identity.domain.User;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

  private static final String SECRET = "una-clave-secreta-de-prueba-con-mas-de-32-caracteres";

  @Test
  void shouldGenerateTokenWithEmailAndRole() {
    JwtService jwtService = new JwtService(new JwtProperties(SECRET, 86_400_000));
    User user = new User(1L, "admin@test.com", "hashed-password", Role.ADMIN);

    String token = jwtService.generateToken(user);

    assertEquals(user.email(), jwtService.extractEmail(token));
    assertEquals(user.role().name(), jwtService.extractRole(token));
    assertTrue(jwtService.isTokenValid(token));
  }

  @Test
  void shouldRejectExpiredToken() {
    JwtService jwtService = new JwtService(new JwtProperties(SECRET, -1));
    User user = new User(1L, "user@test.com", "hashed-password", Role.USER);

    String token = jwtService.generateToken(user);

    assertFalse(jwtService.isTokenValid(token));
  }

  @Test
  void shouldRejectInvalidToken() {
    JwtService jwtService = new JwtService(new JwtProperties(SECRET, 86_400_000));

    assertFalse(jwtService.isTokenValid("token-invalido"));
  }
}
