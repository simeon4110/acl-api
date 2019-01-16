package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.repositories.items.ItemRepository;
import com.sonnets.sonnet.services.exceptions.StoredProcedureQueryException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * This is for handling requests that span multiple item types.
 *
 * @author Josh Harkema
 */
@Service
public class ItemService {
    private static final Logger LOGGER = Logger.getLogger(ItemService.class);
    private final ItemRepository itemRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * @return the basic details of all items in the database.
     */
    public String getAll() {
        LOGGER.debug("Returning all items.");
        return itemRepository.getAllItems().orElseThrow(StoredProcedureQueryException::new);
    }

    /**
     * @param principal the principal of the user making the request.
     * @return the basic details of all items added by the user making the request.
     */
    public String getAllUserItems(Principal principal) {
        LOGGER.debug("Returning all items created by user " + principal.getName());
        return itemRepository.getAllUserItems(principal.getName()).orElseThrow(StoredProcedureQueryException::new);
    }
}
