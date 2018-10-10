package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.annotation_types.Dialog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * For storing dialog annotations.
 *
 * @author Josh Harkema
 */
@Repository
public interface DialogRepository extends JpaRepository<Dialog, Long> {
    Set<Dialog> findAllBySectionId(final Long sectionId);
}
