package org.acl.database.controllers.Theater;

import io.swagger.annotations.*;
import org.acl.database.persistence.dtos.theater.ActorDto;
import org.acl.database.persistence.models.theater.Actor;
import org.acl.database.services.theater.ActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
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
@PropertySource("classpath:global.properties")
@Api(tags = "Play (Theater) Endpoints")
public class ActorController {
    private final ActorService actorService;

    @Autowired
    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/play/actor", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Add Actor",
            notes = "Add a new Actor to the database.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Actor added successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    public ResponseEntity<Void> add(@RequestBody @Valid ActorDto dto) {
        return actorService.add(dto);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/play/actor/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Actor by ID", notes = "Returns an Actor from its database ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Actor.class, message = "OK"),
            @ApiResponse(code = 404, message = "An Actor with the requested ID does not exist.")
    })
    public Actor getById(@PathVariable("id") Long id) {
        return actorService.getById(id);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/play/actor", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modify Actor",
            notes = "Modify an existing Actor in the database.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Actor modified successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    public ResponseEntity<Void> modify(@RequestBody @Valid ActorDto dto, Principal principal) {
        return actorService.modify(dto, principal);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/play/actor", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Delete Actor",
            notes = "Delete an existing Actor from the database.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Actor deleted successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    public ResponseEntity<Void> delete(Long id, Principal principal) {
        return actorService.delete(id, principal);
    }

}
