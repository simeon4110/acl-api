package org.acl.database.controllers.base;

import io.swagger.annotations.*;
import org.acl.database.persistence.dtos.base.ShortStoryDto;
import org.acl.database.persistence.dtos.base.ShortStoryOutDto;
import org.acl.database.persistence.models.base.ShortStory;
import org.acl.database.services.base.ShortStoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * Controller for all short story related endpoints.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
@Api(tags = "Short Story Endpoints")
public class ShortStoryController implements AbstractItemController<ShortStory, ShortStoryDto, ShortStoryOutDto> {
    private final ShortStoryService shortStoryService;

    private static final String CACHE_ALL_SECURE = " SHORT_STORY_ALL_SECURE";
    private static final String CACHE_ALL = " SHORT_STORY_ALL";
    private static final String CACHE_BY_ID = " SHORT_STORY_BY_ID";
    private static final String CACHE_BY_IDS = " SHORT_STORY_BY_IDS";


    @Autowired
    public ShortStoryController(ShortStoryService shortStoryService) {
        this.shortStoryService = shortStoryService;
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/short_story", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true),
    })
    @ApiOperation(value = "Add Short Story",
            notes = "Adds a new short story to the database.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Short story added successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    public ResponseEntity<Void> add(@RequestBody @Valid ShortStoryDto dto) {
        return shortStoryService.add(dto);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/short_story/{id}")
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true),
            @CacheEvict(value = CACHE_BY_IDS, allEntries = true),
            @CacheEvict(value = CACHE_BY_ID, key = "#id"),
    })
    @ApiOperation(value = "Delete Short Story",
            notes = "Delete an existing short story.",
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
            @ApiResponse(code = 404, message = "A short story with the requested ID does not exist.")
    })
    public ResponseEntity<Void> delete(@PathVariable("id") Long id, Principal principal) {
        return shortStoryService.delete(id, principal);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/short_story/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_BY_ID, key = "#id")
    @ApiOperation(value = "Get Short Story by ID", notes = "Returns a short story from its database ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = ShortStory.class, message = "OK"),
            @ApiResponse(code = 404, message = "A Short Story with the requested ID does not exist.")
    })
    public ShortStory getById(@PathVariable("id") Long id) {
        return shortStoryService.getById(id);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/short_story/by_ids/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_BY_IDS, key = "#ids")
    @ApiOperation(value = "Get Short Stories by List of IDs", notes = "Returns a list of short stories from a list" +
            " of database IDs.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = ShortStory.class, responseContainer = "List", message = "OK"),
            @ApiResponse(code = 404, message = "A short story with the requested ID does not exist.")
    })
    public List<ShortStory> getByIds(@PathVariable("ids") Long[] ids) {
        return shortStoryService.getByIds(ids);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/short_story/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_ALL)
    @ApiOperation(value = "Get all Public Domain Short Stories", notes = "Returns a list of all public domain " +
            "short stories in the db.")
    public List<ShortStoryOutDto> getAll() {
        return shortStoryService.getAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/short_story/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_ALL_SECURE)
    @ApiOperation(value = "Get all Short Stories",
            notes = "Returns a list of all short stories in the database",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            }
    )
    public List<ShortStoryOutDto> authedUserGetAll() {
        return shortStoryService.authedUserGetAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/short_story/all/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get All Short Stories Paged", notes = "Returns a paginated list of all the public domain " +
            "short stories in the database.")
    public Page<ShortStory> getAllPaged(Pageable pageable) {
        return shortStoryService.getAllPaged(pageable);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/short_story/all_user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get All Short Stories Added by User",
            notes = "Returns a list of all the short stories in the database added by the user making the request.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            }
    )
    public List<ShortStory> getAllByUser(Principal principal) {
        return shortStoryService.getAllByUser(principal);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value = "/secure/short_story", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true),
            @CacheEvict(value = CACHE_BY_ID, key = "#dto.id"),
    })
    @ApiOperation(value = "Modify Short Story",
            notes = "Modify an existing short story.",
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
            @ApiResponse(code = 404, message = "A short story with the requested ID does not exist.")
    })
    public ResponseEntity<Void> modify(@RequestBody @Valid ShortStoryDto dto, Principal principal) {
        return shortStoryService.modify(dto, principal);
    }

}
