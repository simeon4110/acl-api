package com.sonnets.sonnet.persistence.repositories.section;

import com.sonnets.sonnet.persistence.models.prose.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author Josh Harkema
 */
@Repository
public interface SectionRepositoryBase extends JpaRepository<Section, Long>, SectionRepositoryStoredProcedures {
    Optional<Section> findByProcessed(final boolean processed);

    Optional<List<Section>> findAllByAuthor_LastName(final String lastName);

    Optional<Section> findById(final Long id);

    @Query("SELECT title FROM Section s WHERE s.id = ?1")
    Optional<String> getSectionTitle(final Long id);
}
