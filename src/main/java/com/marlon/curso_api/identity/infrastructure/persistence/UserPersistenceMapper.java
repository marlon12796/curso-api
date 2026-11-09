package com.marlon.curso_api.identity.infrastructure.persistence;

import com.marlon.curso_api.identity.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

  public UserJpaEntity toEntity(User domain) {
    return new UserJpaEntity(domain.id(), domain.email(), domain.password(), domain.role());
  }

  public User toDomain(UserJpaEntity entity) {
    return new User(entity.getId(), entity.getEmail(), entity.getPassword(), entity.getRole());
  }
}
