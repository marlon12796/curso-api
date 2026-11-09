package com.marlon.curso_api.shared.infraestructure.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
  }

  @Bean // 👈 Le dice a Spring: "El objeto devuelto aquí será el filtro oficial
  // de
  // seguridad"
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable()) // Sin CSRF en APIs REST
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.GET, "/courses/**")
                    .permitAll() // GET
                    .requestMatchers("/auth/**")
                    .permitAll() // /auth es público
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .exceptionHandling(
            exceptions ->
                exceptions
                    .authenticationEntryPoint(
                        (request, response, exception) -> {
                          HttpStatus status = HttpStatus.UNAUTHORIZED;

                          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                          response.setContentType("application/json");
                          response.setCharacterEncoding("UTF-8");

                          response
                              .getWriter()
                              .write(
                                  """
              {
                "status": "%s",
                "error": "Unauthorized",
                "message": "Debes autenticarte"
              }
              """
                                      .formatted(status));
                        })
                    .accessDeniedHandler(
                        (request, response, exception) -> {
                          HttpStatus status = HttpStatus.FORBIDDEN;
                          response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                          response.setContentType("application/json");
                          response
                              .getWriter()
                              .write(
                                  """
              {
                "status": "%s",
                "error": "Forbidden",
                "message": "No tienes permisos suficientes"
              }
              """
                                      .formatted(status));
                        }))
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
