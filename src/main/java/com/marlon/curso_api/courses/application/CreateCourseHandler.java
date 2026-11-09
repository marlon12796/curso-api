package com.marlon.curso_api.courses.application;

import com.marlon.curso_api.courses.domain.Course;
import com.marlon.curso_api.courses.domain.CourseRepository;
import com.marlon.curso_api.courses.domain.event.CourseCreatedEvent;
import java.time.LocalDateTime;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service // <-- Le dice a Spring que registre este Handler como un Bean
// inyectable
public class CreateCourseHandler {

  private final CourseRepository repository;
  private final ApplicationEventPublisher eventPublisher;

  public CreateCourseHandler(
      CourseRepository repository, ApplicationEventPublisher eventPublisher) {
    this.repository = repository;
    this.eventPublisher = eventPublisher;
  }

  // El método que ejecuta el comando (igual que en NestJS CQRS)
  public CourseResponse handle(CreateCourseCommand command) {
    // 1. Instanciamos el Aggregate Root de Dominio con sus reglas de negocio
    Course course =
        new Course(
            null, // El ID nace null
            command.title(),
            command.description(),
            command.price());
    // 2. Lo persistimos a través del repositorio
    Course savedCourse = repository.save(course);
    // 3. 🚀 DISPARAR EL EVENTO DE DOMINIO:
    eventPublisher.publishEvent(
        new CourseCreatedEvent(
            savedCourse.getId(),
            savedCourse.getTitle(),
            savedCourse.getPrice(),
            LocalDateTime.now()));

    return CourseResponse.fromAggregate(savedCourse);
  }
}
