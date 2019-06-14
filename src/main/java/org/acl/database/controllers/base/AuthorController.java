package org.acl.database.controllers.base;

import io.swagger.annotations.*;
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
@Api(tags = "Author Endpoints")
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
    @PostMapping(value = "/secure/author", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Add Author",
            notes = "Add a new author to the database.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Author added successfully."),
            @ApiResponse(code = 409, message = "An author with that name already exists."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    public ResponseEntity<Void> add(@RequestBody @Valid AuthorDto authorDto) {
        return authorService.add(authorDto);
    }

    /**
     * Modify an existing author.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/secure/author", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modify Author (Admin)",
            notes = "Modify an existing author.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = @AuthorizationScope(scope = "admin", description = "Administrative scope.")
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Modification completed successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request."),
            @ApiResponse(code = 404, message = "An author with the requested ID does not exist.")
    })
    public ResponseEntity<Void> modify(@RequestBody @Valid AuthorDto authorDto) {
        return authorService.modify(authorDto);
    }

    /**
     * Administrator author deletion. Users cannot delete authors.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/author/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Delete Author (Admin)",
            notes = "Delete an existing author.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = @AuthorizationScope(scope = "admin", description = "Administrative scope.")
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Deletion completed successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request."),
            @ApiResponse(code = 404, message = "An author with the requested ID does not exist.")
    })
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return authorService.delete(id);
    }

    /**
     * @param id the db id of the author to get.
     * @return an author by db id.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/author/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Author by ID", notes = "Returns an author from its database ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Author.class, message = "OK"),
            @ApiResponse(code = 404, message = "An author with the requested ID does not exist.")
    })
    public Author getById(@PathVariable("id") Long id) {
        return authorService.get(id);
    }

    /**
     * @param lastName the author's last name.
     * @return an author by last name.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/author/by_last_name/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Author by Last Name", notes = "Returns an author by last name, returns null if no Author " +
            "is found.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Author.class, message = "OK"),
            @ApiResponse(code = 404, message = "An author with the requested last name does not exist.")
    })
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
    @ApiOperation(value = "Search for Author", notes = "Runs a Lucene based search of all author's in the database.")
    public String search(@RequestBody AuthorDto authorDto) {
        return searchQueryHandlerService.searchAuthor(authorDto);
    }
}
