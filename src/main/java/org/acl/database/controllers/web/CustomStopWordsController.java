package org.acl.database.controllers.web;

import io.swagger.annotations.*;
import org.acl.database.persistence.dtos.web.CustomStopWordsDto;
import org.acl.database.persistence.models.tools.CustomStopWords;
import org.acl.database.services.web.CustomStopWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * Handles all stop words related endpoints.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
@Api(tags = "Stop Words Endpoints")
public class CustomStopWordsController {
    private final CustomStopWordsService stopWordsService;

    @Autowired
    public CustomStopWordsController(CustomStopWordsService stopWordsService) {
        this.stopWordsService = stopWordsService;
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/stop_words", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Create a Stop Words List",
            notes = "Create a new list of custom stop words.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Stop words added successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    public ResponseEntity<Void> add(@RequestBody @Valid CustomStopWordsDto dto) {
        return stopWordsService.add(dto);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/stop_words/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a List of Stop Words",
            notes = "Returns a custom stop words list from its database id.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = CustomStopWords.class, message = "OK"),
            @ApiResponse(code = 404, message = "A stop words list with the requested ID does not exist.")
    })
    public CustomStopWords getById(@PathVariable("id") Long id) {
        return stopWordsService.getById(id);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/stop_words", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modify a List of Stop Words",
            notes = "Modify an existing list of stop words.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Modification completed successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request."),
            @ApiResponse(code = 404, message = "A stop words list with the requested ID does not exist.")
    })
    public ResponseEntity<Void> modify(@RequestBody @Valid CustomStopWordsDto dto, Principal principal) {
        return stopWordsService.modify(dto, principal);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/stop_words/{id}")
    @ApiOperation(value = "Delete a Stop Words List",
            notes = "Delete an existing list of custom stop words.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Deletion completed successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request."),
            @ApiResponse(code = 404, message = "A stop words list with the requested ID does not exist.")
    })
    public ResponseEntity<Void> delete(@PathVariable("id") Long id, Principal principal) {
        return stopWordsService.delete(id, principal);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/stop_words/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a User's Stop Words Lists",
            notes = "Get a list of all custom stop words lists created by the user making the request.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    public List<CustomStopWords> getUser(Principal principal) {
        return stopWordsService.getAllByUser(principal);
    }
}
