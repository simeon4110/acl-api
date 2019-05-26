package com.sonnets.sonnet.controllers.web;

import com.sonnets.sonnet.persistence.dtos.web.CorporaBasicOutDto;
import com.sonnets.sonnet.persistence.dtos.web.CorporaDto;
import com.sonnets.sonnet.persistence.dtos.web.CorporaItemsDto;
import com.sonnets.sonnet.persistence.models.web.Corpora;
import com.sonnets.sonnet.services.web.CorporaService;
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

    @CrossOrigin(origins = "${allowed-origin}") //
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @PostMapping(value = "/secure/corpora/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid CorporaDto dto) {
        return corporaService.add(dto);
    }

    @CrossOrigin(origins = "${allowed-origin}") //
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @GetMapping(value = "/secure/corpora/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Corpora get(@PathVariable("id") Long id) {
        return corporaService.get(id);
    }

    @CrossOrigin(origins = "${allowed-origin}") //
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @GetMapping(value = "/secure/corpora/get_basic/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CorporaBasicOutDto getBasic(@PathVariable("id") Long id) {
        return corporaService.getBasic(id);
    }

    @CrossOrigin(origins = "${allowed-origin}") //
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @PutMapping(value = "/secure/corpora/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid CorporaDto dto, Principal principal) {
        return corporaService.modify(dto, principal);
    }

    @CrossOrigin(origins = "${allowed-origin}") //
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @PutMapping(value = "/secure/corpora/add_items", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addItems(@RequestBody @Valid CorporaItemsDto dto, Principal principal) {
        return corporaService.addItems(dto, principal);
    }

    @CrossOrigin(origins = "${allowed-origin}") //
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @PutMapping(value = "/secure/corpora/remove_items", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removeItems(@RequestBody @Valid CorporaItemsDto dto, Principal principal) {
        return corporaService.removeItems(dto, principal);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @DeleteMapping(value = "/secure/corpora/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id, Principal principal) {
        return corporaService.delete(id, principal);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @GetMapping(value = "/secure/corpora/get_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Corpora> getAllUser(Principal principal) {
        return corporaService.getAllUser(principal);
    }
}
