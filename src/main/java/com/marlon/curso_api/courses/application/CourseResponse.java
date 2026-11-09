package com.marlon.curso_api.courses.application;

import com.marlon.curso_api.courses.domain.Course;

// DTO inmutable que se enviará en formato JSON al cliente
public record CourseResponse(
    Long id, String title, String description, double price, String status) {
  // Método de mapeo limpio: convierte de Entidad de Dominio a Response
  public static CourseResponse fromAggregate(Course course) {
    return new CourseResponse(
        course.getId(),
        course.getTitle(),
        course.getDescription(),
        course.getPrice(),
        course.getStatus().name());
  }
}
