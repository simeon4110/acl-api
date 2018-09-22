package com.sonnets.sonnet.persistence.repositories.section;

import com.sonnets.sonnet.persistence.models.prose.Section;

import java.util.List;
import java.util.Optional;

/**
 * Interface to define stored procedure methods.
 *
 * @author Josh Harkema
 */
public interface SectionRepositoryStoredProcedures {
    Optional<List<Section>> getAllSections();
}
