package com.sonnets.sonnet.persistence.repositories.annotation;

import com.sonnets.sonnet.persistence.models.annotation.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Josh Harkema
 */
@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
}
