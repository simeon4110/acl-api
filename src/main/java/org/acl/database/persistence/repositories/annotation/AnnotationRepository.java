package org.acl.database.persistence.repositories.annotation;

import org.acl.database.persistence.models.annotation.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Josh Harkema
 */
@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
}
