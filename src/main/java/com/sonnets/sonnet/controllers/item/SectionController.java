package com.sonnets.sonnet.controllers.item;

import com.sonnets.sonnet.controllers.AbstractItemController;
import com.sonnets.sonnet.persistence.dtos.base.AnnotationDto;
import com.sonnets.sonnet.persistence.dtos.prose.SectionDto;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.services.prose.SectionService;
import com.sonnets.sonnet.tools.ParseParam;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SectionController implements AbstractItemController<Section, SectionDto> {
    private final SectionService sectionService;

    @Autowired
    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
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
     * @param id the id of the section to delete.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/section/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return sectionService.delete(id);
    }

    /**
     * @param id        of the section to delete.
     * @param principal of the user making the request.
     * @return OK if good, UNAUTHORIZED if user does not own section.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/section/user_delete/{id}")
    public ResponseEntity<Void> userDelete(@PathVariable("id") Long id, Principal principal) {
        return sectionService.userDelete(id, principal);
    }

    /**
     * @param id the id of the section to get.
     * @return section by db id.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/by_id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Section getById(@PathVariable("id") Long id) {
        return sectionService.getById(id);
    }

    /**
     * @param ids a list of database ids.
     * @return a list of results.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/by_ids/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Section> getByIds(@PathVariable("ids") Long[] ids) {
        return sectionService.getByIds(ids);
    }

    /**
     * @return All sections in the db.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Section> getAll() {
        return sectionService.getAll();
    }

    /**
     * @return only the most basic details of every section in the db.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/all_simple", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllSimple() {
        return sectionService.getAllSimple();
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/all/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Section> getAllPaged(Pageable pageable) {
        return sectionService.getAllPaged(pageable);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/secure/section/all_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Section> getAllByUser(Principal principal) {
        return sectionService.getAllByUser(principal);
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
        return sectionService.modifyUser(dto, principal);
    }

    /**
     * @param bookId the id of the book.
     * @return all sections in a book by the book's db id.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/from_book/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Section> getAllFromBook(@PathVariable("id") Long bookId) {
        return sectionService.getAllFromBook(bookId);
    }

    /**
     * @param bookId the id of the book to get all sections from.
     * @return a JSON formatted string of the sections' most basic details.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/from_book_simple/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllFromBookSimple(@PathVariable("id") Long bookId) {
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
}
