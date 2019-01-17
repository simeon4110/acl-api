package com.sonnets.sonnet.persistence.repositories.section;

import com.sonnets.sonnet.persistence.models.prose.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Josh Harkema
 */
@Repository
public interface SectionRepositoryBase extends JpaRepository<Section, Long>, SectionRepositoryStoredProcedures {
    Optional<List<Section>> findAllByAuthor_LastName(final String lastName);

    Optional<Section> findById(final Long id);

    Optional<List<Section>> findAllByCreatedBy(final String username);
}
