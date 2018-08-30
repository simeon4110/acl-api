package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import com.sonnets.sonnet.services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List doSearch(@RequestBody SearchDto dto) {
        return searchService.search(dto);
    }
}
