package org.acl.database.persistence.repositories.annotation;

import org.acl.database.persistence.models.annotation.Dialog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * For storing dialog annotations.
 *
 * @author Josh Harkema
 */
@Repository
public interface DialogRepository extends JpaRepository<Dialog, Long> {
    Set<Dialog> findAllBySectionIdOrderByCharacterOffsetBeginAsc(final Long sectionId);

    Optional<List<Dialog>> findAllByItemIdOrderByCharacterOffsetBeginAsc(final Long itemId);

    @Override
    Optional<Dialog> findById(Long aLong);
}
