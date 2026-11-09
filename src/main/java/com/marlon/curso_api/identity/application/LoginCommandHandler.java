package com.marlon.curso_api.identity.application;

import com.marlon.curso_api.identity.application.ports.PasswordHasher;
import com.marlon.curso_api.identity.domain.User;
import com.marlon.curso_api.identity.domain.UserRepository;
import com.marlon.curso_api.identity.domain.exception.InvalidCredentialsException;
import com.marlon.curso_api.shared.infraestructure.security.JwtService;
import org.springframework.stereotype.Component;

@Component
public class LoginCommandHandler {

  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;
  private final JwtService jwtService;

  public LoginCommandHandler(
      UserRepository userRepository, PasswordHasher passwordHasher, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordHasher = passwordHasher;
    this.jwtService = jwtService;
  }

  public LoginResponse handle(LoginCommand command) {
    User user =
        userRepository.findByEmail(command.email()).orElseThrow(InvalidCredentialsException::new);

    boolean validPassword = passwordHasher.matches(command.password(), user.password());

    if (!validPassword) throw new InvalidCredentialsException();

    String token = jwtService.generateToken(user);

    return new LoginResponse(token);
  }
}
