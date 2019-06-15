package org.acl.database.controllers.base;

import io.swagger.annotations.*;
import org.acl.database.persistence.dtos.base.AnnotationDto;
import org.acl.database.persistence.dtos.base.SectionOutDto;
import org.acl.database.persistence.dtos.prose.SectionDto;
import org.acl.database.persistence.models.base.Section;
import org.acl.database.services.base.SectionService;
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
 * All the section related REST endpoints are here.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
@Api(tags = "Section Endpoints")
public class SectionController implements AbstractItemController<Section, SectionDto, SectionOutDto> {
    private final SectionService sectionService;
    private static final String CACHE_ALL_SECURE = "SECTION_ALL_SECURE";
    private static final String CACHE_ALL = "SECTION_ALL";
    private static final String CACHE_BY_BOOK = "SECTION_BY_BOOK";
    private static final String CACHE_BY_ID = "SECTION_BY_ID";

    @Autowired
    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/section", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true),
            @CacheEvict(value = CACHE_BY_BOOK, key = "#dto.bookId"),
    })
    @ApiOperation(value = "Add Section",
            notes = "Add a new section to the database.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Section added successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    public ResponseEntity<Void> add(@RequestBody @Valid SectionDto dto) {
        return sectionService.add(dto);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/section/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true),
            @CacheEvict(value = CACHE_BY_BOOK, allEntries = true),
            @CacheEvict(value = CACHE_BY_ID, key = "#id")
    })
    @ApiOperation(value = "Delete Section",
            notes = "Delete an existing section from the database.",
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
            @ApiResponse(code = 404, message = "A section with the requested ID does not exist.")
    })
    public ResponseEntity<Void> delete(@PathVariable("id") Long id, Principal principal) {
        return sectionService.delete(id, principal);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_BY_ID, key = "#id")
    @ApiOperation(value = "Get Section by ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Section.class, message = "OK"),
            @ApiResponse(code = 404, message = "A section with the requested ID does not exist.")
    })
    public Section getById(@PathVariable("id") Long id) {
        return sectionService.getById(id);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/by_ids/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get a List of Sections by IDs.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Section.class, responseContainer = "List", message = "OK"),
            @ApiResponse(code = 404, message = "A section with the requested ID does not exist.")
    })
    public List<Section> getByIds(@PathVariable("ids") Long[] ids) {
        return sectionService.getByIds(ids);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_ALL)
    @ApiOperation(value = "Get all Public Domain Sections.")
    public List<SectionOutDto> getAll() {
        return sectionService.getAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/section/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_ALL_SECURE)
    @ApiOperation(value = "Get all Sections.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    public List<SectionOutDto> authedUserGetAll() {
        return sectionService.authedUserGetAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/all/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all Sections Paged.")
    public Page<Section> getAllPaged(Pageable pageable) {
        return sectionService.getAllPaged(pageable);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/section/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all Sections added by User.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    public List<Section> getAllByUser(Principal principal) {
        return sectionService.getAllByUser(principal);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/section", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true),
            @CacheEvict(value = CACHE_BY_BOOK, key = "#dto.bookId"),
            @CacheEvict(value = CACHE_BY_ID, key = "#dto.id")
    })
    @ApiOperation(value = "Modify Section (Admin)",
            notes = "Modify an existing section.",
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
            @ApiResponse(code = 404, message = "A section with the requested ID does not exist.")
    })
    public ResponseEntity<Void> modify(@RequestBody @Valid SectionDto dto, Principal principal) {
        return sectionService.modify(dto, principal);
    }

    /**
     * @param bookId the id of the book.
     * @return all sections in a book by the book's db id.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/from_book/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_BY_BOOK, key = "#bookId")
    @ApiOperation(value = "Get all Sections from Book")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Section.class, responseContainer = "List", message = "OK"),
            @ApiResponse(code = 404, message = "A book with the requested ID does not exist.")
    })
    public List<Section> getAllFromBook(@PathVariable("id") Long bookId) {
        return sectionService.getAllFromBook(bookId);
    }

    /**
     * @param lastName the last name of the author to look for.
     * @return a list of all the sections matching the author.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/search/by_last_name/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all Sections by Author's Last Name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Section.class, responseContainer = "List", message = "OK"),
            @ApiResponse(code = 404, message = "No sections by authors with that last name exist.")
    })
    public List<Section> getAllByAuthorLastName(@PathVariable("lastName") String lastName) {
        lastName = FormatTools.parseParam(lastName);
        return sectionService.getAllByAuthorLastName(lastName);
    }

    /**
     * Add a narrator (BookCharacter) to a given section.
     *
     * @param dto the dto with the details.
     * @return 200 if successful.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/section/add_narrator", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true),
            @CacheEvict(value = CACHE_BY_BOOK, allEntries = true),
            @CacheEvict(value = CACHE_BY_ID, key = "#dto.id")
    })
    public Section addNarrator(@RequestBody @Valid AnnotationDto dto) {
        return sectionService.setNarrator(dto);
    }

    /**
     * Remove a narrator (BookCharacter) from a given section.
     *
     * @param sectionId the id of the section to remove the narrator from.
     * @return 200 if successful.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/section/remove_narrator/{sectionId}")
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true),
            @CacheEvict(value = CACHE_BY_BOOK, allEntries = true),
            @CacheEvict(value = CACHE_BY_ID, key = "#sectionId")
    })
    public ResponseEntity<Void> deleteNarrator(@PathVariable("sectionId") Long sectionId) {
        return sectionService.deleteNarrator(sectionId);
    }
}
