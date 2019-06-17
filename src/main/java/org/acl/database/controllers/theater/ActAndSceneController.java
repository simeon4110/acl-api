package org.acl.database.controllers.theater;

import io.swagger.annotations.*;
import org.acl.database.persistence.dtos.theater.ActDto;
import org.acl.database.persistence.dtos.theater.SceneDto;
import org.acl.database.persistence.models.theater.Act;
import org.acl.database.persistence.models.theater.Scene;
import org.acl.database.services.theater.ActAndSceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Combined controller for handling Act and Scene related CRUD.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
@Api(tags = "Play (Theater) Endpoints")
public class ActAndSceneController {
    private final ActAndSceneService actAndSceneService;

    @Autowired
    public ActAndSceneController(ActAndSceneService actAndSceneService) {
        this.actAndSceneService = actAndSceneService;
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/play/act", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = TheaterCacheConstants.CACHE_BY_ID, key = "#dto.playId")
    @ApiOperation(value = "Add Act",
            notes = "Add a new Act to the database.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Act added successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    public ResponseEntity<Void> addAct(@RequestBody @Valid ActDto dto) {
        return actAndSceneService.addAct(dto);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/play/act/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Act by ID", notes = "Returns an Act from its database ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Act.class, message = "OK"),
            @ApiResponse(code = 404, message = "An Act with the requested ID does not exist.")
    })
    public Act getAct(@PathVariable("id") Long id) {
        return actAndSceneService.getAct(id);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/play/scene", consumes = MediaType.APPLICATION_JSON_VALUE)
    @CacheEvict(value = TheaterCacheConstants.CACHE_BY_ID, key = "#dto.playId")
    @ApiOperation(value = "Add Scene",
            notes = "Add a new Scene to the database.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Scene added successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    public ResponseEntity<Void> addScene(@RequestBody @Valid SceneDto dto) {
        return actAndSceneService.addScene(dto);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/play/scene/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Scene by ID", notes = "Returns a Scene from its database ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Scene.class, message = "OK"),
            @ApiResponse(code = 404, message = "A scene with the requested ID does not exist.")
    })
    public Scene getScene(@PathVariable("id") Long id) {
        return actAndSceneService.getScene(id);
    }
}
