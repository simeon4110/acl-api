package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.services.search.SearchQueryHandlerService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     * Execute a search via a raw Lucene query string.
     *
     * @param queryString the raw lucene style query string to execute.
     * @param itemTypes   the object classes to search.
     * @return search results or an empty list.
     * @throws ParseException when the lucene query string is invalid.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public String doSearch(@RequestParam("query_string") String queryString,
                           @RequestParam(value = "item_types", required = false) String[] itemTypes)
            throws ParseException {
        return searchQueryHandlerService.doSearch(queryString, itemTypes);
    }
}
