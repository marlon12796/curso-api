package com.marlon.curso_api.courses.domain.event;

import java.time.LocalDateTime;

public record CourseCreatedEvent(
    Long courseId, String title, double price, LocalDateTime occurredOn) {}
