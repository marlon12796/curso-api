package com.marlon.curso_api.courses.infraestructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataCourseRepository extends JpaRepository<CourseEntity, Long> {}
