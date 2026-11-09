package com.marlon.curso_api.identity.application;

import com.marlon.curso_api.identity.application.ports.PasswordHasher;
import com.marlon.curso_api.identity.domain.Role;
import com.marlon.curso_api.identity.domain.User;
import com.marlon.curso_api.identity.domain.UserRepository;
import com.marlon.curso_api.identity.domain.exception.EmailAlreadyRegisteredException;
import org.springframework.stereotype.Component;

@Component
public class RegisterUserCommandHandler {
  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;

  public RegisterUserCommandHandler(UserRepository userRepository, PasswordHasher passwordHasher) {
    this.userRepository = userRepository;
    this.passwordHasher = passwordHasher;
  }

  public void handle(RegisterRequestCommand command) {
    if (userRepository.existsByEmail(command.email())) {
      throw new EmailAlreadyRegisteredException(command.email());
    }
    String encryptedPassword = passwordHasher.hash(command.password());
    User user = new User(command.email(), encryptedPassword, Role.USER);
    userRepository.save(user);
  }
}
