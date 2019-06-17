package org.acl.database.controllers.search;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.acl.database.persistence.dtos.web.SearchParamDto;
import org.acl.database.services.search.SearchQueryHandlerService;
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
@Api(tags = "Search Endpoints")
public class SearchController {
    private final SearchQueryHandlerService searchQueryHandlerService;

    public SearchController(SearchQueryHandlerService searchQueryHandlerService) {
        this.searchQueryHandlerService = searchQueryHandlerService;
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PostMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Run an Advanced Search", notes = "Execute an advanced search on the database.")
    public String doSearch(@RequestBody List<SearchParamDto> searchParams,
                           @RequestParam(value = "item_types", required = false) String[] itemTypes) {
        return searchQueryHandlerService.search(searchParams, itemTypes);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/basic_search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Run a basic search", notes = "Search string is the string you're searching for. Searches " +
            "title, text, and author's first and last name.")
    public String doBasicSearch(@RequestParam(value = "search_string") String searchString) {
        return searchQueryHandlerService.basicSearch(searchString);
    }
}
