package com.sonnets.sonnet.persistence.repositories.poem;

import java.util.Optional;

/**
 * @author Josh Harkema
 */
public interface PoemRepositoryStoredProcedures {
    /**
     * @return the basic details of all poems.
     */
    Optional<String> getAllPoemsSimple();

    /**
     * @return the basic details of only public domain poems.
     */
    Optional<String> getAllPoemsSimplePDO();

    /**
     * @return two randomly selected poems.
     */
    Optional<String> getTwoRandomPoems();
}
