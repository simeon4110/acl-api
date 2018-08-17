package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.AuthorDto;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.services.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AuthorController {
    private static final String ALLOWED_ORIGIN = "*";
    private final AuthorService authorService;

    @Autowired
    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // Add an author.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/author/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid AuthorDto authorDto) {
        return authorService.add(authorDto);
    }

    // Modify an author.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/author/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid AuthorDto authorDto) {
        return authorService.modify(authorDto);
    }

    // Delete an author.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/author/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        return authorService.delete(id);
    }

    // Get an author by id.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/author/get_by_id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Author getById(@PathVariable("id") String id) {
        return authorService.get(id);
    }

    // Get an author by last name.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/author/get_by_last_name/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Author getByLastName(@PathVariable("lastName") String lastName) {
        lastName = lastName.replace('_', ' ');
        return authorService.getByLastName(lastName);
    }
}
