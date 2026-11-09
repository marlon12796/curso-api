package com.marlon.curso_api.identity.infrastructure.persistence;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
  Optional<UserJpaEntity> findByEmail(String email);

  boolean existsByEmail(String email);
}
