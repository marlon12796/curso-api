package com.marlon.curso_api.identity.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.marlon.curso_api.identity.application.ports.PasswordHasher;
import com.marlon.curso_api.identity.domain.User;
import com.marlon.curso_api.identity.domain.UserRepository;
import com.marlon.curso_api.identity.domain.exception.EmailAlreadyRegisteredException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisterUserCommandHandlerTest {

  @Mock private UserRepository userRepository;

  @Mock private PasswordHasher passwordHasher;

  @InjectMocks private RegisterUserCommandHandler handler;

  @Test
  void shouldRegisterUserWithHashedPassword() {
    RegisterRequestCommand command = new RegisterRequestCommand("user@test.com", "123456");

    when(userRepository.existsByEmail(command.email())).thenReturn(false);
    when(passwordHasher.hash(command.password())).thenReturn("hashed-password");

    handler.handle(command);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(userCaptor.capture());

    User savedUser = userCaptor.getValue();
    assertEquals(command.email(), savedUser.email());
    assertEquals("hashed-password", savedUser.password());
    verify(passwordHasher).hash(command.password());
  }

  @Test
  void shouldRejectDuplicatedEmail() {
    RegisterRequestCommand command = new RegisterRequestCommand("user@test.com", "123456");

    when(userRepository.existsByEmail(command.email())).thenReturn(true);

    assertThrows(EmailAlreadyRegisteredException.class, () -> handler.handle(command));

    verify(passwordHasher, never()).hash(any(String.class));
    verify(userRepository, never()).save(any(User.class));
  }
}
