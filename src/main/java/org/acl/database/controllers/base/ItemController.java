package org.acl.database.controllers.base;

import org.acl.database.persistence.models.base.Item;
import org.acl.database.services.base.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

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
     * @param principal of the user making the request.
     * @return basic details of all items added by the user making the request.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/item/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Item> getAllUser(Principal principal) {
        return itemService.getAllUserItems(principal);
    }
}
