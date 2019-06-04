package org.acl.database.persistence.repositories;

import org.acl.database.persistence.models.base.Item;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Abstract interface for Spring data.
 *
 * @author Josh Harkema
 */
@Repository
public interface ItemRepository extends CrudRepository<Item, Long> {
}
