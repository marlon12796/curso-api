package com.marlon.curso_api.identity.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.marlon.curso_api.identity.application.ports.PasswordHasher;
import com.marlon.curso_api.identity.domain.Role;
import com.marlon.curso_api.identity.domain.User;
import com.marlon.curso_api.identity.domain.UserRepository;
import com.marlon.curso_api.identity.domain.exception.InvalidCredentialsException;
import com.marlon.curso_api.shared.infraestructure.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoginCommandHandlerTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordHasher passwordHasher;

  @Mock private JwtService jwtService;

  @InjectMocks private LoginCommandHandler handler;

  @Test
  void shouldLoginAndGenerateJwt() {
    LoginCommand command = new LoginCommand("user@test.com", "123456");
    User user = new User(1L, command.email(), "hashed-password", Role.USER);

    when(userRepository.findByEmail(command.email())).thenReturn(Optional.of(user));
    when(passwordHasher.matches(command.password(), user.password())).thenReturn(true);
    when(jwtService.generateToken(user)).thenReturn("jwt-token");

    LoginResponse response = handler.handle(command);

    assertEquals("jwt-token", response.token());
    verify(jwtService).generateToken(user);
  }

  @Test
  void shouldRejectUnknownEmail() {
    LoginCommand command = new LoginCommand("unknown@test.com", "123456");

    when(userRepository.findByEmail(command.email())).thenReturn(Optional.empty());

    assertThrows(InvalidCredentialsException.class, () -> handler.handle(command));
  }

  @Test
  void shouldRejectInvalidPassword() {
    LoginCommand command = new LoginCommand("user@test.com", "wrong-password");
    User user = new User(1L, command.email(), "hashed-password", Role.USER);

    when(userRepository.findByEmail(command.email())).thenReturn(Optional.of(user));
    when(passwordHasher.matches(command.password(), user.password())).thenReturn(false);

    assertThrows(InvalidCredentialsException.class, () -> handler.handle(command));
  }
}
