package com.marlon.curso_api.courses.infraestructure.persistence;

import com.marlon.curso_api.courses.domain.Course;
import com.marlon.curso_api.courses.domain.CourseRepository;
import com.marlon.curso_api.courses.domain.CourseStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
@Primary // Le dice a Spring que inyecte este adaptador en vez de
// InMemoryCourseRepository
public class PostgresCourseRepository implements CourseRepository {

  private final SpringDataCourseRepository jpaRepository;
  private final CourseMapper mapper;
  private final JdbcClient jdbc;

  public PostgresCourseRepository(
      SpringDataCourseRepository jpaRepository, CourseMapper mapper, JdbcClient jdbc) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
    this.jdbc = jdbc;
  }

  @Override
  public Course save(Course course) {
    CourseEntity savedEntity = jpaRepository.save(mapper.toEntity(course));
    return mapper.toDomain(savedEntity);
  }

  @Override
  public Optional<Course> findById(Long id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<Course> findAll() {
    return jdbc.sql("SELECT id, title, description, price, status FROM courses")
        .query(
            (rs, rowNum) ->
                Course.reconstitute(
                    rs.getLong("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    CourseStatus.valueOf(rs.getString("status"))))
        .list();
  }
}
