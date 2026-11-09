package com.marlon.curso_api.identity.application;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginCommand(
    @NotBlank(message = "El email es obligatorio") @Email(message = "El email no es válido")
        String email,
    @NotBlank(message = "La contraseña es obligatoria") String password) {}
