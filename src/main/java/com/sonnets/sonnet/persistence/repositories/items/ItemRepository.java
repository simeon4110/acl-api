package com.sonnets.sonnet.persistence.repositories.items;

import com.sonnets.sonnet.persistence.models.base.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Abstract interface for Spring data.
 *
 * @author Josh Harkema
 */
@Repository
public interface ItemRepository extends CrudRepository<Item, Long>, ItemRepositoryStoredProcedures {
}
