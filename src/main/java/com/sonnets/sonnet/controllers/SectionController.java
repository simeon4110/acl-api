package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.base.RejectDto;
import com.sonnets.sonnet.persistence.dtos.prose.SectionDto;
import com.sonnets.sonnet.persistence.models.base.Annotation;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.services.prose.SectionService;
import com.sonnets.sonnet.tools.ParseParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.PropertySource;
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
public class SectionController {
    private final SectionService sectionService;

    @Autowired
    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    /**
     * @param id the id of the section to get.
     * @return section by db id.
     */
    @Cacheable(value = "section-single", key = "#id")
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Section get(@PathVariable("id") String id) {
        return sectionService.get(id);
    }

    /**
     * @return All sections in the db.
     */
    @Cacheable(value = "section-all")
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/get_all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getAll() {
        return sectionService.getAll();
    }

    /**
     * @param bookId the id of the book.
     * @return all sections in a book by the book's db id.
     */
    @Cacheable(value = "section-book", key = "#bookId")
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/get/from_book/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Section> getAllFromBook(@PathVariable("id") String bookId) {
        return sectionService.getAllFromBook(bookId);
    }

    @Cacheable(value = "section-book-simple", key = "#bookId")
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/get/from_book_simple/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllFromBookSimple(@PathVariable("id") String bookId) {
        return sectionService.getAllFromBookSimple(bookId);
    }

    /**
     * @param lastName the last name of the author to look for.
     * @return a list of all the sections matching the author.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/search/by_last_name/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Section> getAllByAuthorLastName(@PathVariable("lastName") String lastName) {
        lastName = ParseParam.parse(lastName);
        return sectionService.getAllByAuthorLastName(lastName);
    }

    /**
     * Add a section to the db.
     */
    @CacheEvict(value = "section-all")
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/section/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid SectionDto dto) {
        return sectionService.add(dto);
    }

    /**
     * Modify a section in the db (ADMIN ONLY).
     */
    @Caching(evict = {
            @CacheEvict(value = "section-all"),
            @CacheEvict(value = "section-single", key = "#dto.id")
    })
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/secure/section/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid SectionDto dto) {
        return sectionService.modify(dto);
    }

    /**
     * Modify a section in the db (OWNER ONLY).
     */
    @Caching(evict = {
            @CacheEvict(value = "section-all"),
            @CacheEvict(value = "section-single", key = "#dto.id")
    })
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/section/user_modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modifyUser(@RequestBody @Valid SectionDto dto, Principal principal) {
        return sectionService.modify(dto, principal);
    }

    /**
     * Delete a section by id (ADMIN ONLY).
     *
     * @param id the id of the section to delete.
     */
    @Caching(evict = {
            @CacheEvict(value = "section-all"),
            @CacheEvict(value = "section-single", key = "#id")
    })
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/section/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        return sectionService.deleteById(id);
    }

    /**
     * Confirm a section by db id.
     *
     * @param id the id of the section to confirm.
     */
    @Caching(evict = {
            @CacheEvict(value = "section-all"),
            @CacheEvict(value = "section-single", key = "#id")
    })
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/section/confirm/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> confirm(@PathVariable("id") String id, Principal principal) {
        return sectionService.confirm(id, principal);
    }

    /**
     * Reject a section.
     */
    @Caching(evict = {
            @CacheEvict(value = "section-all"),
            @CacheEvict(value = "section-single", key = "#dto.id")
    })
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/section/reject", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> reject(@RequestBody @Valid RejectDto dto) {
        return sectionService.reject(dto);
    }

    /**
     * Set an annotation.
     *
     * @param body the JSON string.
     * @param id  the id of the section.
     * @return 200 if good.
     */
    @CacheEvict(value = "section-annotation", key = "#id")
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/section/annotation/set/{id}", consumes = {MediaType.TEXT_PLAIN_VALUE,
            MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> setAnnotation(@RequestBody String body, @PathVariable("id") String id) {
        return sectionService.setAnnotation(body, id);
    }

    /**
     * Get an annotation.
     *
     * @param id the id of the section.
     * @return an annotation NullPointer is thrown if it does not exist.
     */
    @Cacheable(value = "section-annotation", key = "#id")
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/section/annotation/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Annotation getAnnotation(@PathVariable("id") String id) {
        return sectionService.getAnnotation(id);
    }
}
