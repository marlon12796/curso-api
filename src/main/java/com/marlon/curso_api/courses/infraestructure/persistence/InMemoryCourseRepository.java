package com.marlon.curso_api.courses.infraestructure.persistence;

import com.marlon.curso_api.courses.domain.Course;
import com.marlon.curso_api.courses.domain.CourseRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository // <-- Le dice a Spring: "Yo soy quien implementa CourseRepository"
public class InMemoryCourseRepository implements CourseRepository {
  private final Map<Long, Course> database = new HashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1); // Generador de IDs autoincremental

  @Override
  public Course save(Course course) {
    // Generamos el ID si es nuevo (como hace la base de datos)
    Long id = (course.getId() != null) ? course.getId() : idGenerator.getAndIncrement();
    Course persistedCourse =
        new Course(id, course.getTitle(), course.getDescription(), course.getPrice());
    database.put(id, persistedCourse);
    return persistedCourse;
  }

  @Override
  public Optional<Course> findById(Long id) {
    return Optional.ofNullable(database.get(id));
  }

  @Override
  public List<Course> findAll() {
    return new ArrayList<>(database.values());
  }
}
