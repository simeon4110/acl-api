package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.annotation_types.Dialog;
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
    Set<Dialog> findAllBySectionId(final Long sectionId);

    Optional<List<Dialog>> findAllByItemId(final Long itemId);

    @Override
    Optional<Dialog> findById(Long aLong);
}
