package com.marlon.curso_api.courses.domain;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
  //
  Course save(Course course);

  // Busca un curso por su ID (devuelve Optional por si no existe)
  Optional<Course> findById(Long id);

  List<Course> findAll();
}
