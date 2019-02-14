package com.sonnets.sonnet.persistence.repositories.item;

import java.util.Optional;

/**
 * Interface to define store procedure methods.
 *
 * @author Josh Harkema
 */
public interface ItemRepositoryStoredProcedures {
    Optional<String> getAllUserItems(final String userName);

    Optional<String> getAllItems();
}
