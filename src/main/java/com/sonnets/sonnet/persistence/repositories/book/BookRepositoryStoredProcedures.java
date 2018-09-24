package com.sonnets.sonnet.persistence.repositories.book;

import java.util.Optional;

/**
 * Interface to define store procedure methods.
 *
 * @author Josh Harkema
 */
public interface BookRepositoryStoredProcedures {
    Optional<String> getBookTitle(final Long bookId);
}
