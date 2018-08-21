package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.base.RejectDto;
import com.sonnets.sonnet.persistence.dtos.prose.SectionDto;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.services.prose.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SectionController {
    private static final String ALLOWED_ORIGIN = "*";
    private final SectionService sectionService;

    @Autowired
    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/section/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Section get(@PathVariable("id") String id) {
        return sectionService.get(id);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/section/get/from_book/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Section> getAllFromBook(@PathVariable("id") String bookId) {
        return sectionService.getAllFromBook(bookId);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/section/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid SectionDto dto) {
        return sectionService.add(dto);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/secure/section/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid SectionDto dto) {
        return sectionService.modify(dto);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/section/user_modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modifyUser(@RequestBody @Valid SectionDto dto, Principal principal) {
        return sectionService.modify(dto, principal);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/section/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        return sectionService.deleteById(id);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/section/confirm/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> confirm(@PathVariable("id") String id, Principal principal) {
        return sectionService.confirm(id, principal);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/section/reject", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> reject(@RequestBody @Valid RejectDto dto) {
        return sectionService.reject(dto);
    }
}
