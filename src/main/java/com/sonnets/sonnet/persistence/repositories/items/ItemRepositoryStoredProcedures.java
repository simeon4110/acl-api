package com.sonnets.sonnet.persistence.repositories.items;

/**
 * Interface to define store procedure methods.
 *
 * @author Josh Harkema
 */
public interface ItemRepositoryStoredProcedures {
    String getItemsByUser(final String userName);
}
