package com.marlon.curso_api.courses.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.marlon.curso_api.courses.domain.Course;
import com.marlon.curso_api.courses.domain.CourseRepository;
import com.marlon.curso_api.courses.domain.CourseStatus;
import com.marlon.curso_api.courses.domain.event.CourseCreatedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class CreateCourseHandlerTest {

  @Mock private CourseRepository repository;

  @Mock private ApplicationEventPublisher eventPublisher;

  @InjectMocks private CreateCourseHandler handler;

  @Test
  void shouldCreateCourseAndPublishEvent() {
    CreateCourseCommand command =
        new CreateCourseCommand("Spring Boot desde cero", "Curso práctico de Spring Boot", 199.99);

    Course savedCourse =
        new Course(1L, command.title(), command.description(), command.price(), CourseStatus.DRAFT);

    when(repository.save(any(Course.class))).thenReturn(savedCourse);

    CourseResponse response = handler.handle(command);

    assertEquals(1L, response.id());
    assertEquals(command.title(), response.title());
    assertEquals(command.description(), response.description());
    assertEquals(command.price(), response.price());
    assertEquals("DRAFT", response.status());

    verify(repository).save(any(Course.class));

    ArgumentCaptor<CourseCreatedEvent> eventCaptor =
        ArgumentCaptor.forClass(CourseCreatedEvent.class);
    verify(eventPublisher).publishEvent(eventCaptor.capture());

    CourseCreatedEvent event = eventCaptor.getValue();
    assertEquals(1L, event.courseId());
    assertEquals(command.title(), event.title());
    assertEquals(command.price(), event.price());
  }

  @Test
  void shouldRejectNegativePrice() {
    CreateCourseCommand command =
        new CreateCourseCommand("Curso inválido", "Descripción del curso", -50);

    assertThrows(IllegalArgumentException.class, () -> handler.handle(command));

    verifyNoInteractions(repository, eventPublisher);
  }

  @Test
  void shouldRejectBlankTitle() {
    CreateCourseCommand command = new CreateCourseCommand("", "Descripción del curso", 50);

    assertThrows(IllegalArgumentException.class, () -> handler.handle(command));

    verifyNoInteractions(repository, eventPublisher);
  }
}
