package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.web.CorporaDto;
import com.sonnets.sonnet.persistence.dtos.web.CorporaItemsDto;
import com.sonnets.sonnet.persistence.models.web.Corpora;
import com.sonnets.sonnet.services.CorporaService;
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
 * Handles all corpora related REST endpoints.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class CorporaController {
    private final CorporaService corporaService;

    @Autowired
    public CorporaController(CorporaService corporaService) {
        this.corporaService = corporaService;
    }

    /**
     * @return all of a user's corpora.
     */
    @CrossOrigin(origins = "${allowed-origin}") //
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'GUEST')")
    @GetMapping(value = "/secure/corpora/my_corpora", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getUserCorpora(Principal principal) {
        return corporaService.getUserCorpora(principal);
    }

    /**
     * @param id the corpora's id.
     * @return a single corpora by id.
     */
    @CrossOrigin(origins = "${allowed-origin}") //
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'GUEST')")
    @GetMapping(value = "/secure/corpora/get_by_id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Corpora getById(@PathVariable("id") String id) {
        return corporaService.getSingle(id);
    }

    /**
     * Create a new corpora.
     */
    @CrossOrigin(origins = "${allowed-origin}") //
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'GUEST')")
    @PostMapping(value = "/secure/corpora/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createCorpora(@RequestBody @Valid CorporaDto corporaDto, Principal principal) {
        return corporaService.createCorpora(corporaDto);
    }

    /**
     * Add items to a corpora.
     */
    @CrossOrigin(origins = "${allowed-origin}") //
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'GUEST')")
    @PutMapping(value = "/secure/corpora/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addItemsToCorpora(@RequestBody @Valid CorporaItemsDto dto) {
        return corporaService.addItems(dto);
    }

    /**
     * Remove items from a corpora.
     */
    @CrossOrigin(origins = "${allowed-origin}") //
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @PutMapping(value = "/secure/corpora/remove", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removeItemsFromCorpora(@RequestBody @Valid CorporaItemsDto dto) {
        return corporaService.removeItems(dto);
    }

    /**
     * Modify a corpora.
     */
    @CrossOrigin(origins = "${allowed-origin}") //
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @PutMapping(value = "/secure/corpora/change_name", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modifyCorporaDetails(@RequestBody @Valid CorporaDto dto) {
        return corporaService.modify(dto.getId(), dto.getName(), dto.getDescription());
    }

    /**
     * Delete a corpora (OWNER ONLY)
     */
    @CrossOrigin(origins = "${allowed-origin}", methods = RequestMethod.DELETE)
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @DeleteMapping(value = "/secure/corpora/delete/{id}")
    public ResponseEntity<Void> deleteCorpus(@PathVariable("id") String id) {
        return corporaService.delete(id);
    }
}
