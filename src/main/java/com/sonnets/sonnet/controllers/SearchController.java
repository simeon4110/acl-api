package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.web.SearchParamDto;
import com.sonnets.sonnet.services.search.SearchQueryHandlerService;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public String doSearch(@RequestBody List<SearchParamDto> searchParams,
                           @RequestParam(value = "item_types", required = false) String[] itemTypes) {
        return searchQueryHandlerService.search(searchParams, itemTypes);
    }
}
