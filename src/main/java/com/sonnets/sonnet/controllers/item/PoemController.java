package com.sonnets.sonnet.controllers.item;

import com.sonnets.sonnet.controllers.AbstractItemController;
import com.sonnets.sonnet.persistence.dtos.poetry.PoemDto;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.services.poem.PoemService;
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
 * Controller for all poetry related endpoints.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class PoemController implements AbstractItemController<Poem, PoemDto> {
    private final PoemService poemService;

    @Autowired
    public PoemController(PoemService poemService) {
        this.poemService = poemService;
    }

    /**
     * Add a poem to the db.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/poem/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid PoemDto dto) {
        return poemService.add(dto);
    }

    /**
     * Delete a poem (ADMIN ONLY).
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/poem/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return poemService.delete(id);
    }

    /**
     * Delete a poem (OWNER ONLY).
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/poem/user_delete/{id}")
    public ResponseEntity<Void> userDelete(@PathVariable("id") Long id, Principal principal) {
        return poemService.userDelete(id, principal);
    }

    /**
     * @param id the id of the poem to get.
     * @return a poem.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/by_id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Poem getById(@PathVariable("id") Long id) {
        return poemService.getById(id);
    }

    /**
     * @param ids the list of poem ids to get.
     * @return a list of poems.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/by_ids/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Poem> getByIds(@PathVariable Long[] ids) {
        return poemService.getByIds(ids);
    }

    /**
     * @return all poems in the database.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Poem> getAll() {
        return poemService.getAll();
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/all_simple", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllSimple() {
        return poemService.getAllSimple();
    }

    /**
     * @return all poems in the database paged.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poem/all/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Poem> getAllPaged(Pageable pageable) {
        return poemService.getAllPaged(pageable);
    }

    /**
     * @return all poems added by a user.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/poem/all_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Poem> getAllByUser(Principal principal) {
        return poemService.getAllByUser(principal);
    }

    /**
     * Edit a poem (ADMIN ONLY).
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value = "/secure/poem/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid PoemDto dto) {
        return poemService.modify(dto);
    }

    /**
     * Edit a poem (OWNER ONLY).
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/poem/modify_user", consumes = MediaType.APPLICATION_JSON_VALUE)
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
        lastName = ParseParam.parse(lastName);
        return poemService.getAllByAuthorLastName(lastName);
    }
}
