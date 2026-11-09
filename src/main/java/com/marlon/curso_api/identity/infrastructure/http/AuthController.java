package com.marlon.curso_api.identity.infrastructure.http;

import com.marlon.curso_api.identity.application.LoginCommand;
import com.marlon.curso_api.identity.application.LoginCommandHandler;
import com.marlon.curso_api.identity.application.LoginResponse;
import com.marlon.curso_api.identity.application.RegisterRequestCommand;
import com.marlon.curso_api.identity.application.RegisterUserCommandHandler;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final RegisterUserCommandHandler handler;
  private final LoginCommandHandler loginHandler;

  public AuthController(RegisterUserCommandHandler handler, LoginCommandHandler loginHandler) {
    this.handler = handler;
    this.loginHandler = loginHandler;
  }

  @PostMapping("/register")
  public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequestCommand command) {

    handler.handle(command);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginCommand command) {

    LoginResponse response = loginHandler.handle(command);

    return ResponseEntity.ok(response);
  }
}
