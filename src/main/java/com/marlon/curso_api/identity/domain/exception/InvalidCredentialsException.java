package com.marlon.curso_api.identity.domain.exception;

public class InvalidCredentialsException extends RuntimeException {

  public InvalidCredentialsException() {
    super("Credenciales inválidas");
  }
}
