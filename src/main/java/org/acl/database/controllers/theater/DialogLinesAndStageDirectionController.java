package org.acl.database.controllers.theater;

import io.swagger.annotations.*;
import org.acl.database.persistence.dtos.theater.DialogLinesDto;
import org.acl.database.persistence.dtos.theater.StageDirectionDto;
import org.acl.database.services.theater.DialogLinesAndStageDirectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

/**
 * Controller for handling all DialogLines and StageDirection related endpoints.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
@Api(tags = "Play (Theater) Endpoints")
public class DialogLinesAndStageDirectionController {
    private final DialogLinesAndStageDirectionService dialogLinesAndStageDirectionService;

    @Autowired
    public DialogLinesAndStageDirectionController(DialogLinesAndStageDirectionService dialogLinesAndStageDirectionService) {
        this.dialogLinesAndStageDirectionService = dialogLinesAndStageDirectionService;
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/play/lines", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = TheaterCacheConstants.CACHE_BY_ID, key = "#dto.playId")
    @ApiOperation(value = "Add Dialog Lines",
            notes = "Add a new Dialog Lines to the database. Sequencing is UBER important!!",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Dialog lines added successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    public ResponseEntity<Void> addDialogLines(@RequestBody @Valid DialogLinesDto dto) {
        return dialogLinesAndStageDirectionService.addDialogLines(dto);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/play/lines", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = TheaterCacheConstants.CACHE_BY_ID, key = "#dto.playId")
    @ApiOperation(value = "Modify Dialog Lines",
            notes = "Modify an existing Dialog Lines in the database. Sequencing is UBER important!!",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Dialog Lines modified successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    public ResponseEntity<Void> modifyDialogLines(@RequestBody @Valid DialogLinesDto dto, Principal principal) {
        return dialogLinesAndStageDirectionService.modifyDialogLines(dto, principal);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/play/stage_direction", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = TheaterCacheConstants.CACHE_BY_ID, key = "#dto.playId")
    @ApiOperation(value = "Add Stage Direction",
            notes = "Add a new Stage Direction to the database. Sequencing is UBER important!!",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Stage Direction added successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    public ResponseEntity<Void> addStageDirection(@RequestBody @Valid StageDirectionDto dto) {
        return dialogLinesAndStageDirectionService.addStageDirection(dto);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/play/stage_direction", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = TheaterCacheConstants.CACHE_BY_ID, key = "#dto.playId")
    @ApiOperation(value = "Modify Stage Direction",
            notes = "Modify an existing Stage Direction in the database. Sequencing is UBER important!!",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Stage Direction modified successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    public ResponseEntity<Void> modifyStageDirection(@RequestBody @Valid StageDirectionDto dto, Principal principal) {
        return dialogLinesAndStageDirectionService.modifyStageDirection(dto, principal);
    }
}
