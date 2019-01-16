package com.sonnets.sonnet.persistence.repositories.section;

import java.util.Optional;

/**
 * Interface to define stored procedure methods.
 *
 * @author Josh Harkema
 */
public interface SectionRepositoryStoredProcedures {
    Optional<String> getBookSectionsSimple(final Long bookId);

    Optional<String> getAllSectionsSimple();
}
