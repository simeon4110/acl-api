package org.acl.database.controllers.base;

import io.swagger.annotations.*;
import org.acl.database.persistence.dtos.base.PoemDto;
import org.acl.database.persistence.dtos.base.PoemOutDto;
import org.acl.database.persistence.models.base.Poem;
import org.acl.database.services.base.PoemService;
import org.acl.database.tools.FormatTools;
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
 * Controller for all poetry related endpoints.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
@Api(tags = "Poem Endpoints")
public class PoemController implements AbstractItemController<Poem, PoemDto, PoemOutDto> {
    private final PoemService poemService;
    private static final String CACHE_ALL_SECURE = "POEM_ALL_SECURE";
    private static final String CACHE_ALL = "POEM_ALL";
    private static final String CACHE_BY_ID = "POEM_BY_ID";
    private static final String CACHE_BY_IDS = "POEM_BY_IDS";
    private static final String CACHE_BY_FORM = "POEM_BY_FORM";

    @Autowired
    public PoemController(PoemService poemService) {
        this.poemService = poemService;
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/poem", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true),
            @CacheEvict(value = CACHE_BY_FORM, key = "#dto.form")
    })
    @ApiOperation(value = "Add Poem",
            notes = "Adds a new poem to the database.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Poem added successfully."),
            @ApiResponse(code = 409, message = "A poem with that title and author already exists."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    public ResponseEntity<Void> add(@RequestBody @Valid PoemDto dto) {
        return poemService.add(dto);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/poem/{id}")
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true),
            @CacheEvict(value = CACHE_BY_IDS, allEntries = true),
            @CacheEvict(value = CACHE_BY_ID, key = "#id"),
            @CacheEvict(value = CACHE_BY_FORM, allEntries = true)
    })
    @ApiOperation(value = "Delete Poem",
            notes = "Delete an existing poem.",
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
            @ApiResponse(code = 423, message = "This poem is confirmed, to delete it contact Josh."),
            @ApiResponse(code = 404, message = "A poem with the requested ID does not exist.")
    })
    public ResponseEntity<Void> delete(@PathVariable("id") Long id, Principal principal) {
        return poemService.delete(id, principal);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_BY_ID, key = "#id")
    @ApiOperation(value = "Get Poem by ID", notes = "Returns a poem from its database ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Poem.class, message = "OK"),
            @ApiResponse(code = 404, message = "A poem with the requested ID does not exist.")
    })
    public Poem getById(@PathVariable("id") Long id) {
        return poemService.getById(id);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/by_ids/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_BY_IDS, key = "#ids")
    @ApiOperation(value = "Get Poems by List of IDs", notes = "Returns a list of poems from a list of database IDs.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Poem.class, responseContainer = "List", message = "OK"),
            @ApiResponse(code = 404, message = "A poem with the requested ID does not exist.")
    })
    public List<Poem> getByIds(@PathVariable Long[] ids) {
        return poemService.getByIds(ids);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_ALL)
    @ApiOperation(value = "Get all Public Domain Poems", notes = "Returns a list of all public domain poems in the db.")
    public List<PoemOutDto> getAll() {
        return poemService.getAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/poem/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_ALL_SECURE)
    @ApiOperation(value = "Get all Poems",
            notes = "Returns a list of all poems in the database",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            }
    )
    public List<PoemOutDto> authedUserGetAll() {
        return poemService.authedUserGetAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/all/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get All Poems Paged", notes = "Returns a paginated list of all the public domain poems in " +
            "the database.")
    public Page<Poem> getAllPaged(Pageable pageable) {
        return poemService.getAllPaged(pageable);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/poem/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get All Poems Added by User",
            notes = "Returns a list of all the poems in the database added by the user making the request.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            }
    )
    public List<Poem> getAllByUser(Principal principal) {
        return poemService.getAllByUser(principal);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/poem", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true),
            @CacheEvict(value = CACHE_BY_ID, key = "#dto.id"),
            @CacheEvict(value = CACHE_BY_FORM, key = "#dto.form")
    })
    @ApiOperation(value = "Modify Poem",
            notes = "Modify an existing poem.",
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
            @ApiResponse(code = 404, message = "A poem with the requested ID does not exist.")
    })
    public ResponseEntity<Void> modify(@RequestBody @Valid PoemDto dto, Principal principal) {
        return poemService.modify(dto, principal);
    }

    /**
     * @param form the form to get.
     * @return all poems of a specific form.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/by_form/{form}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_BY_FORM, key = "#form")
    @ApiOperation(value = "Get all Poems by Form", notes = "Returns a list of poems by form (i.e. sonnet)")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Poem.class, responseContainer = "List", message = "OK"),
            @ApiResponse(code = 404, message = "No poems matching that form are found in the db.")
    })
    public List<Poem> getAllByForm(@PathVariable("form") String form) {
        return poemService.getAllByForm(form);
    }

    /**
     * @param form the form to get.
     * @return all poems of a specific form paged.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/by_form_paged/{form}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all Poems by Form Paged", notes = "Returns a list of poems by form (i.e. sonnet)")
    public Page<Poem> getAllByFormPaged(@PathVariable("form") String form, Pageable pageable) {
        return poemService.getAllByFormPaged(form, pageable);
    }

    /**
     * @param lastName the last name of the author to com.sonnets.sonnet.search for.
     * @return a list of poems matching the last name.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/by_last_name/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all Poems by Author's Last Name", notes = "Returns a list of poems by an Author's last " +
            "name (i.e. 'Shakespeare')")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Poem.class, responseContainer = "List", message = "OK"),
            @ApiResponse(code = 404, message = "No poems matching that last name found in the db.")
    })
    public List<Poem> getByAuthorLastName(@PathVariable("lastName") String lastName) {
        lastName = FormatTools.parseParam(lastName);
        return poemService.getAllByAuthorLastName(lastName);
    }

    /**
     * @return two poems selected at random.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/two_random", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Two Random Poems", notes = "Returns two random public domain poems from the db.")
    public String getTwoRandomPoems() {
        return poemService.getTwoRandomPoems();
    }
}
