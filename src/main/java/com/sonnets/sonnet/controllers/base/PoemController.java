package com.sonnets.sonnet.controllers.base;

import com.sonnets.sonnet.persistence.dtos.base.PoemDto;
import com.sonnets.sonnet.persistence.dtos.base.PoemOutDto;
import com.sonnets.sonnet.persistence.models.base.Poem;
import com.sonnets.sonnet.services.base.PoemService;
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
import tools.FormatTools;

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
public class PoemController implements AbstractItemController<Poem, PoemDto, PoemOutDto> {
    private final PoemService poemService;
    private static final String CACHE_ALL_SECURE = "POEM_ALL_SECURE";
    private static final String CACHE_ALL = "POEM_ALL";
    private static final String CACHE_BY_ID = "POEM_BY_ID";
    private static final String CACHE_BY_IDS = "POEM_BY_IDS";

    @Autowired
    public PoemController(PoemService poemService) {
        this.poemService = poemService;
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/poem/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true)
    })
    public ResponseEntity<Void> add(@RequestBody @Valid PoemDto dto) {
        return poemService.add(dto);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/poem/delete/{id}")
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true),
            @CacheEvict(value = CACHE_BY_IDS, allEntries = true),
            @CacheEvict(value = CACHE_BY_ID, key = "#id")
    })
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return poemService.delete(id);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/poem/user_delete/{id}")
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true),
            @CacheEvict(value = CACHE_BY_IDS, allEntries = true),
            @CacheEvict(value = CACHE_BY_ID, key = "#id")
    })
    public ResponseEntity<Void> userDelete(@PathVariable("id") Long id, Principal principal) {
        return poemService.userDelete(id, principal);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/by_id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_BY_ID, key = "#id")
    public Poem getById(@PathVariable("id") Long id) {
        return poemService.getById(id);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/by_ids/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_BY_IDS, key = "#ids")
    public List<Poem> getByIds(@PathVariable Long[] ids) {
        return poemService.getByIds(ids);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_ALL)
    public List<PoemOutDto> getAll() {
        return poemService.getAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/poem/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = CACHE_ALL_SECURE)
    public List<PoemOutDto> authedUserGetAll() {
        return poemService.authedUserGetAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/all/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Poem> getAllPaged(Pageable pageable) {
        return poemService.getAllPaged(pageable);
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/poem/all_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Poem> getAllByUser(Principal principal) {
        return poemService.getAllByUser(principal);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value = "/secure/poem/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true),
            @CacheEvict(value = CACHE_BY_ID, key = "#dto.id")

    })
    public ResponseEntity<Void> modify(@RequestBody @Valid PoemDto dto) {
        return poemService.modify(dto);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/poem/modify_user", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Caching(evict = {
            @CacheEvict(value = CACHE_ALL_SECURE, allEntries = true),
            @CacheEvict(value = CACHE_ALL, allEntries = true),
            @CacheEvict(value = CACHE_BY_ID, key = "#dto.id")
    })
    public ResponseEntity<Void> modifyUser(@RequestBody @Valid PoemDto dto, Principal principal) {
        return poemService.modifyUser(dto, principal);
    }

    /**
     * @param form the form to get.
     * @return all poems of a specific form.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/by_form/{form}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Poem> getAllByForm(@PathVariable("form") String form) {
        return poemService.getAllByForm(form);
    }

    /**
     * @param form the form to get.
     * @return all poems of a specific form paged.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/by_form_paged/{form}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Poem> getAllByFormPaged(@PathVariable("form") String form, Pageable pageable) {
        return poemService.getAllByFormPaged(form, pageable);
    }

    /**
     * @param lastName the last name of the author to search for.
     * @return a list of poems matching the last name.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/search/by_last_name/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Poem> getByAuthorLastName(@PathVariable("lastName") String lastName) {
        lastName = FormatTools.parseParam(lastName);
        return poemService.getAllByAuthorLastName(lastName);
    }

    /**
     * @return two poems selected at random.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/two_random", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getTwoRandomPoems() {
        return poemService.getTwoRandomPoems();
    }
}
