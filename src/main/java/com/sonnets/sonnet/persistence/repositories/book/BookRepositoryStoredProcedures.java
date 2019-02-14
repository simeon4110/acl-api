package com.sonnets.sonnet.persistence.repositories.book;

import java.util.Optional;

/**
 * Interface to define store procedure methods.
 *
 * @author Josh Harkema
 */
public interface BookRepositoryStoredProcedures {
    /**
     * @return the basic details of every book in the database.
     */
    Optional<String> getAllBooksSimple();

    /**
     * @return the basic details of every public domain book in the database.
     */
    Optional<String> getAllBooksSimplePDO();
}
