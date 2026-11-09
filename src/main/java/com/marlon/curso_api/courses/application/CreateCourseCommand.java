package com.marlon.curso_api.courses.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

// DTO inmutable que representa la intención de crear un curso
public record CreateCourseCommand(
    @NotBlank(message = "El título es obligatorio")
        @Size(min = 3, message = "El título debe tener al menos 3 caracteres")
        String title,
    @NotBlank(message = "La descripción no puede estar vacía") String description,
    @Positive(message = "El precio debe ser un número mayor a cero") double price) {}
