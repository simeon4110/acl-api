package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.repositories.items.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

/**
 * Simple service for merging item types added by a user.
 *
 * @author Josh Harkema
 */
@Service
public class UserItemsService {
    private final ItemRepository itemRepository;

    @Autowired
    public UserItemsService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Returns the item id, type, title, author.first_name AS first_name, and author.last_name AS last_name
     * of every section and poem the user has added to the db.
     *
     * @param principal the principal of the user making the request.
     * @return a JSON formatted string.
     */
    public String getUserItems(Principal principal) {
        return itemRepository.getItemsByUser(principal.getName());
    }
}
