package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Endpoints for all requests that span multiple item types.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * @return basic details of all items in the database.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/item/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAll() {
        return itemService.getAll();
    }

    /**
     * @param principal of the user making the request.
     * @return basic details of all items added by the user making the request.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/item/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllUser(Principal principal) {
        return itemService.getAllUserItems(principal);
    }
}
