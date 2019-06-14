package org.acl.database.controllers.Theater;

import org.acl.database.persistence.dtos.theater.ActorDto;
import org.acl.database.persistence.models.theater.Actor;
import org.acl.database.services.theater.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

/**
 * Handles all actor related endpoints.
 *
 * @author Josh Harkema
 */
@RestController
public class ActorController {
    private final ActorService actorService;

    @Autowired
    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/play/actor", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid ActorDto dto) {
        return actorService.add(dto);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/play/actor/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Actor getById(@PathVariable("id") Long id) {
        return actorService.getById(id);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/play/actor", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid ActorDto dto, Principal principal) {
        return actorService.modify(dto, principal);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/play/actor", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(Long id, Principal principal) {
        return actorService.delete(id, principal);
    }

}
