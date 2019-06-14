package org.acl.database.controllers.Theater;

import io.swagger.annotations.*;
import org.acl.database.persistence.dtos.theater.PlayDto;
import org.acl.database.persistence.models.theater.Play;
import org.acl.database.services.theater.PlayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@PropertySource("classpath:global.properties")
@Api(tags = "Play Endpoints")
public class PlayController {
    private final PlayService playService;

    @Autowired
    public PlayController(PlayService playService) {
        this.playService = playService;
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/play", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Add Play",
            notes = "Add a new play to the database.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Play added successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request."),
            @ApiResponse(code = 409, message = "A play with that title, by that author already exists in the db.")
    })
    public ResponseEntity<Void> add(@RequestBody @Valid PlayDto dto) {
        return playService.add(dto);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/play", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modify Play",
            notes = "Modify an existing play.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Play modified successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    @CacheEvict(value = TheaterCacheConstants.CACHE_BY_ID, key = "#dto.id")
    public ResponseEntity<Void> modify(@RequestBody @Valid PlayDto dto, Principal principal) {
        return playService.modify(dto, principal);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/play/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Delete Play",
            notes = "Delete an existing play.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Play deleted successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    @CacheEvict(value = TheaterCacheConstants.CACHE_BY_ID, key = "#id")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id, Principal principal) {
        return playService.delete(id, principal);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/play/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = TheaterCacheConstants.CACHE_BY_ID, key = "#id")
    @ApiOperation(value = "Get Play by ID", notes = "Returns a play from its database ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Play.class, message = "OK"),
            @ApiResponse(code = 404, message = "A play with the requested ID does not exist.")
    })
    public Play getById(@PathVariable("id") Long id) {
        return playService.getById(id);
    }

}
