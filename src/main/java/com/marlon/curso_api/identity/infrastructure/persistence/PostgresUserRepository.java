package com.marlon.curso_api.identity.infrastructure.persistence;

import com.marlon.curso_api.identity.domain.User;
import com.marlon.curso_api.identity.domain.UserRepository;
import java.util.Optional;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class PostgresUserRepository implements UserRepository {

  private final UserJpaRepository repository;
  private final UserPersistenceMapper mapper;

  public PostgresUserRepository(UserJpaRepository repository, UserPersistenceMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public User save(User user) {
    UserJpaEntity entity = mapper.toEntity(user);
    UserJpaEntity saved = repository.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return repository.findByEmail(email).map(mapper::toDomain);
  }

  @Override
  public boolean existsByEmail(String email) {
    return repository.existsByEmail(email);
  }
}
