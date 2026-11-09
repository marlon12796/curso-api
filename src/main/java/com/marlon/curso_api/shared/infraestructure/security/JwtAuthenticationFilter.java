package com.marlon.curso_api.shared.infraestructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private static final String BEARER_PREFIX = "Bearer ";
  private static final String ROLE_PREFIX = "ROLE_";

  private final JwtService jwtService;

  public JwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // 1. Lee el Header "Authorization"
    String authHeader = request.getHeader("Authorization");

    // Si no viene con "Bearer ", deja pasar al siguiente filtro (Spring Security
    // decidirá si la
    // ruta requería auth o no)
    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      filterChain.doFilter(request, response);
      return;
    }

    // 2. Extrae el token (quita "Bearer ")
    String token = authHeader.substring(BEARER_PREFIX.length());

    // 3. Si el token es válido, autenticamos al usuario en el contexto de Spring
    // Security
    if (jwtService.isTokenValid(token)) {
      String role = jwtService.extractRole(token);
      String email = jwtService.extractEmail(token);

      SimpleGrantedAuthority authority = new SimpleGrantedAuthority(ROLE_PREFIX + role);
      // Equivalente a: request.user = email en NestJS
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              email, null, Collections.singletonList(authority));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 4. Continúa hacia el Controller (o siguiente filtro)
    filterChain.doFilter(request, response);
  }
}
