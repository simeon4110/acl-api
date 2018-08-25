package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import com.sonnets.sonnet.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    private static final String ALLOWED_ORIGIN = "*";
    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Page doSearch(@RequestBody SearchDto dto, Pageable pageable) {
        return searchService.search(dto, pageable);
    }
}
