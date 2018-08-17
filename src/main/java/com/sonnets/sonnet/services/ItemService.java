package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.repositories.ItemRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Read-Only!!! This is used to get items from the db by id. It should not be used to add anything to the db.
 * :TODO: create read-only repo interface.
 *
 * @author Josh Harkema
 */
@Service
public class ItemService {
    private static final Logger LOGGER = Logger.getLogger(ItemService.class);
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item getItemById(final String id) {
        LOGGER.debug("Returning item with id: " + id);
        return getItemOrNull(id);
    }

    private Item getItemOrNull(final String id) {
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }

        return itemRepository.findById(parsedId).orElse(null);
    }
}
