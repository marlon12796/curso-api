package com.marlon.curso_api.identity.domain.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {

  public EmailAlreadyRegisteredException(String email) {
    super("El email ya está registrado: " + email);
  }
}
