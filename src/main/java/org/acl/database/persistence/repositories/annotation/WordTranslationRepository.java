package org.acl.database.persistence.repositories.annotation;

import org.acl.database.persistence.models.annotation.WordTranslation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * @author Josh Harkema
 */
@Repository
public interface WordTranslationRepository extends JpaRepository<WordTranslation, Long> {
    Optional<Set<WordTranslation>> findAllByItemIdAndItemType(final Long itemId, final String itemType);
}
