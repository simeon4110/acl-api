package org.acl.database.controllers.base;

import io.swagger.annotations.ApiOperation;
import org.acl.database.persistence.dtos.base.AuthorDto;
import org.acl.database.persistence.models.base.Author;
import org.acl.database.services.base.AuthorService;
import org.acl.database.services.search.SearchQueryHandlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    @ApiOperation(value = "Add Author", notes = "Add a new author to the database.")
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
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return authorService.delete(id);
    }

    /**
     * @param id the db id of the author to get.
     * @return an author by db id.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/author/get_by_id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Author getById(@PathVariable("id") Long id) {
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
     * @return the result's of the com.sonnets.sonnet.search.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PutMapping(value = "/author/search", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String search(@RequestBody AuthorDto authorDto) {
        return searchQueryHandlerService.searchAuthor(authorDto);
    }
}