package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.services.search.SearchQueryHandlerService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * Execute a search via a raw Lucene query string.
     *
     * @return search results or an empty list.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public List doSearch(@RequestParam("query_string") String queryString) throws ParseException {
        return searchQueryHandlerService.doSearch(queryString);
    }
}
