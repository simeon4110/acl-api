package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.base.AnnotationDto;
import com.sonnets.sonnet.persistence.dtos.base.RejectDto;
import com.sonnets.sonnet.persistence.dtos.prose.SectionDto;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.services.prose.SectionService;
import com.sonnets.sonnet.tools.ParseParam;
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
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Section get(@PathVariable("id") String id) {
        return sectionService.get(id);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/get_title/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getTitle(@PathVariable("id") String id) {
        return sectionService.getTitle(id);
    }

    /**
     * @return All sections in the db.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/get_all", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAll() {
        return sectionService.getAll();
    }

    /**
     * @param bookId the id of the book.
     * @return all sections in a book by the book's db id.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/get/from_book/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Section> getAllFromBook(@PathVariable("id") String bookId) {
        return sectionService.getAllFromBook(bookId);
    }

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
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/section/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid SectionDto dto) {
        return sectionService.add(dto);
    }

    /**
     * Modify a section in the db (ADMIN ONLY).
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/secure/section/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid SectionDto dto) {
        return sectionService.modify(dto);
    }

    /**
     * Modify a section in the db (OWNER ONLY).
     */
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
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/section/confirm/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> confirm(@PathVariable("id") Long id, Principal principal) {
        return sectionService.confirm(id, principal);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/section/reject", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> reject(@RequestBody @Valid RejectDto dto) {
        return sectionService.reject(dto);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/section/add_narrator", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Section addNarrator(@RequestBody @Valid AnnotationDto dto) {
        return sectionService.setNarrator(dto);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/section/remove_narrator/{sectionId}")
    public ResponseEntity<Void> deleteNarrator(@PathVariable("sectionId") Long sectionId) {
        return sectionService.deleteNarrator(sectionId);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/secure/section/annotation/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAnnotation(@PathVariable("id") Long id) {
        return sectionService.getAnnotations(id);
    }
}
