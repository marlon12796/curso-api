package com.marlon.curso_api.courses.application;

import com.marlon.curso_api.courses.domain.CourseRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetAllCoursesQueryHandler {

  private final CourseRepository repository;

  public GetAllCoursesQueryHandler(CourseRepository repository) {
    this.repository = repository;
  }

  public List<CourseResponse> handle() {
    return repository.findAll().stream().map(CourseResponse::fromAggregate).toList();
  }
}
