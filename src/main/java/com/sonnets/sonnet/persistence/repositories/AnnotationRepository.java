package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.base.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
}
