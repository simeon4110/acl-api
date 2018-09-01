package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.base.ItemDto;
import com.sonnets.sonnet.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Generic controller for dealing with all Item subclasses.
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
     * @return list of items, see ItemDto for formatting instructions of JSON for request.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PostMapping(value = "/items/get", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List getItems(@RequestBody ItemDto dto) {
        return itemService.getItems(dto);
    }
}
