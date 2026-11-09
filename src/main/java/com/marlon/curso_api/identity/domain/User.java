package com.marlon.curso_api.identity.domain;

public class User {

  private final Long id;
  private final String email;
  private final String password;
  private final Role role;

  public User(Long id, String email, String password, Role role) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("El email es obligatorio");
    }
    this.id = id;
    this.email = email;
    this.password = password;
    this.role = role;
  }

  public User(String email, String password, Role role) {
    this(null, email, password, role);
  }

  public Long id() {
    return id;
  }

  public String email() {
    return email;
  }

  public String password() {
    return password;
  }

  public Role role() {
    return role;
  }
}
