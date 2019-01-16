package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.base.AuthorDto;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.services.AuthorService;
import com.sonnets.sonnet.services.search.SearchQueryHandlerService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Handles all author related REST endpoints.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class AuthorController {
    private final AuthorService authorService;
    private final SearchQueryHandlerService searchQueryHandlerService;

    @Autowired
    public AuthorController(AuthorService authorService, SearchQueryHandlerService searchQueryHandlerService) {
        this.authorService = authorService;
        this.searchQueryHandlerService = searchQueryHandlerService;
    }

    /**
     * Add an author to the db.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/author/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid AuthorDto authorDto) {
        return authorService.add(authorDto);
    }

    /**
     * Modify an existing author.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/author/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid AuthorDto authorDto) {
        return authorService.modify(authorDto);
    }

    /**
     * Administrator author deletion. Users cannot delete authors.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/author/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        return authorService.delete(id);
    }

    /**
     * @param id the db id of the author to get.
     * @return an author by db id.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/author/get_by_id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Author getById(@PathVariable("id") String id) {
        return authorService.get(id);
    }

    /**
     * @param lastName the author's last name.
     * @return an author by last name.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/author/get_by_last_name/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Author getByLastName(@PathVariable("lastName") String lastName) {
        lastName = lastName.replace('_', ' ');
        return authorService.getByLastName(lastName);
    }

    /**
     * Search endpoint specific to author objects.
     *
     * @return the result's of the search.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/author/search", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List search(@RequestBody AuthorDto authorDto) throws ParseException {
        return searchQueryHandlerService.searchAuthor(authorDto);
    }
}
