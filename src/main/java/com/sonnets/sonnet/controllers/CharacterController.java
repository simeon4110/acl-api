package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.prose.CharacterDto;
import com.sonnets.sonnet.services.prose.CharacterService;
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
public class CharacterController {
    private final CharacterService characterService;

    @Autowired
    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    /**
     * Add a character.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/character/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid CharacterDto dto) {
        return characterService.add(dto);
    }

    /**
     * Modify a character (ADMIN ONLY).
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/secure/character/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid CharacterDto dto) {
        return characterService.modify(dto);
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
        return characterService.delete(id);
    }
}
