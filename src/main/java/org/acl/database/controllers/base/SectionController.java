package org.acl.database.controllers.base;

import org.acl.database.persistence.dtos.base.AnnotationDto;
import org.acl.database.persistence.dtos.base.SectionOutDto;
import org.acl.database.persistence.dtos.prose.SectionDto;
import org.acl.database.persistence.models.base.Section;
import org.acl.database.services.base.SectionService;
import org.acl.database.tools.FormatTools;
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
public class SectionController implements AbstractItemController<Section, SectionDto, SectionOutDto> {
    private final SectionService sectionService;

    @Autowired
    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/section/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid SectionDto dto) {
        return sectionService.add(dto);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/section/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return sectionService.delete(id);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/section/user_delete/{id}")
    public ResponseEntity<Void> userDelete(@PathVariable("id") Long id, Principal principal) {
        return sectionService.userDelete(id, principal);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/by_id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Section getById(@PathVariable("id") Long id) {
        return sectionService.getById(id);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/by_ids/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Section> getByIds(@PathVariable("ids") Long[] ids) {
        return sectionService.getByIds(ids);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SectionOutDto> getAll() {
        return sectionService.getAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/section/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SectionOutDto> authedUserGetAll() {
        return sectionService.authedUserGetAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/all/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Section> getAllPaged(Pageable pageable) {
        return sectionService.getAllPaged(pageable);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/section/all_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Section> getAllByUser(Principal principal) {
        return sectionService.getAllByUser(principal);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/secure/section/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid SectionDto dto) {
        return sectionService.modify(dto);
    }

    @Override
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
     * @param lastName the last name of the author to look for.
     * @return a list of all the sections matching the author.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/section/search/by_last_name/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
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
    public ResponseEntity<Void> deleteNarrator(@PathVariable("sectionId") Long sectionId) {
        return sectionService.deleteNarrator(sectionId);
    }
}