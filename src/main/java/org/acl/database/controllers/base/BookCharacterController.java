package org.acl.database.controllers.base;

import org.acl.database.persistence.dtos.base.CharacterDto;
import org.acl.database.services.base.BookCharacterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * All character related REST endpoints are here.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class BookCharacterController {
    private final BookCharacterService bookCharacterService;

    @Autowired
    public BookCharacterController(BookCharacterService bookCharacterService) {
        this.bookCharacterService = bookCharacterService;
    }

    /**
     * Add a character.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/character/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid CharacterDto dto) {
        return bookCharacterService.add(dto);
    }

    /**
     * Modify a character (ADMIN ONLY).
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/secure/character/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid CharacterDto dto) {
        return bookCharacterService.modify(dto);
    }

    /**
     * Delete a character (ADMIN ONLY).
     *
     * @param id the id of the character to delete.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/character/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        return bookCharacterService.delete(id);
    }
}
