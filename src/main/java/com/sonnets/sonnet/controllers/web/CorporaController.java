package com.sonnets.sonnet.controllers.web;

import com.sonnets.sonnet.persistence.dtos.web.CorporaDto;
import com.sonnets.sonnet.services.web.CorporaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

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
     * Modify a corpora.
     */
    @CrossOrigin(origins = "${allowed-origin}") //
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @PutMapping(value = "/secure/corpora/change_name", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modifyCorporaDetails(@RequestBody @Valid CorporaDto dto, Principal principal) {
        return corporaService.modify(dto.getId(), dto.getName(), dto.getDescription());
    }

    /**
     * Delete a corpora (OWNER ONLY)
     */
    @CrossOrigin(origins = "${allowed-origin}", methods = RequestMethod.DELETE)
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @DeleteMapping(value = "/secure/corpora/delete/{id}")
    public ResponseEntity<Void> deleteCorpus(@PathVariable("id") String id, Principal principal) {
        return corporaService.delete(id);
    }
}
