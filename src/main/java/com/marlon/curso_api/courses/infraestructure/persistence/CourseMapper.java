package com.marlon.curso_api.courses.infraestructure.persistence;

import com.marlon.curso_api.courses.domain.Course;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {

  // Dominio -> Entidad JPA (para guardar en PostgreSQL)
  public CourseEntity toEntity(Course domain) {
    return new CourseEntity(
        domain.getId(),
        domain.getTitle(),
        domain.getDescription(),
        domain.getPrice(),
        domain.getStatus());
  }

  public Course toDomain(CourseEntity entity) {
    // 👈 AQUÍ OCURRE LA RECONSTITUCIÓN:
    return Course.reconstitute(
        entity.getId(),
        entity.getTitle(),
        entity.getDescription(),
        entity.getPrice(),
        entity.getStatus());
  }
}
