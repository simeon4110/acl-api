package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import com.sonnets.sonnet.services.search.SearchQueryHandlerService;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles all general-purpose search related endpoints.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class SearchController {
    private final SearchQueryHandlerService searchQueryHandlerService;

    public SearchController(SearchQueryHandlerService searchQueryHandlerService) {
        this.searchQueryHandlerService = searchQueryHandlerService;
    }

    /**
     * Execute a search.
     *
     * @return search results or an empty list.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String doSearch(@RequestBody SearchDto dto) {
        return searchQueryHandlerService.doSearch(dto).toString();
    }
}
