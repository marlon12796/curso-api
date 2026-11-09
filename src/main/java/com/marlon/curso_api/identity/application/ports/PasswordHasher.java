package com.marlon.curso_api.identity.application.ports;

public interface PasswordHasher {

  String hash(String rawPassword);

  boolean matches(String rawPassword, String hashedPassword);
}
