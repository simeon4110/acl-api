package com.sonnets.sonnet.persistence.repositories;

import com.sonnets.sonnet.persistence.models.base.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Josh Harkema
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}