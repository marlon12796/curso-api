package com.marlon.curso_api.courses.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CourseTest {

  @Test
  void shouldStartAsDraft() {
    Course course =
        new Course(null, "Spring Boot desde cero", "Curso práctico de Spring Boot", 199.99);

    assertEquals(CourseStatus.DRAFT, course.getStatus());
  }

  @Test
  void shouldPublishCourse() {
    Course course =
        new Course(null, "Spring Boot desde cero", "Curso práctico de Spring Boot", 199.99);

    course.publish();

    assertEquals(CourseStatus.PUBLISHED, course.getStatus());
  }

  @Test
  void shouldRejectBlankTitle() {
    assertThrows(IllegalArgumentException.class, () -> new Course(null, "", "Descripción", 100));
  }

  @Test
  void shouldRejectNegativePrice() {
    assertThrows(
        IllegalArgumentException.class, () -> new Course(null, "Curso válido", "Descripción", -50));
  }
}
