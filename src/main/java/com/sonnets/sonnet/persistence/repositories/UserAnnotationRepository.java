package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.base.UserAnnotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAnnotationRepository extends JpaRepository<UserAnnotation, Long> {
    Optional<List<UserAnnotation>> findAllByCreatedBy(final String createdBy);
}
