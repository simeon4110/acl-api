package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.CorporaDto;
import com.sonnets.sonnet.services.CorporaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
public class CorporaController {
    private static final String ALLOWED_ORIGIN = "*";
    private final CorporaService corporaService;

    @Autowired
    public CorporaController(CorporaService corporaService) {
        this.corporaService = corporaService;
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN) //
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @GetMapping(value = "/secure/corpera/my_corpera", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getUserCorpera(Principal principal) {
        return corporaService.getUserCorpera(principal);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN) //
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @PostMapping(value = "/secure/corpera/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createCorpera(@RequestBody @Valid CorporaDto corporaDto, Principal principal) {
        return corporaService.createCorpera(corporaDto);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN) //
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @PutMapping(value = "/secure/corpera/add_sonnet", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addItemsToCorpera(@RequestBody @Valid CorporaDto dto) {
        return corporaService.addItems(dto.getId(), dto.getSonnetIds());
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN) //
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @PutMapping(value = "/secure/corpera/remove_sonnet", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removeItemsFromCorpera(@RequestBody @Valid CorporaDto dto) {
        return corporaService.removeItems(dto.getId(), dto.getSonnetIds());
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN) //
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @PutMapping(value = "/secure/corpera/change_name", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modifyCorperaDetails(@RequestBody @Valid CorporaDto dto) {
        return corporaService.modify(dto.getId(), dto.getName(), dto.getDescription());
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN, methods = RequestMethod.DELETE)
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @DeleteMapping(value = "/secure/corpera/delete/{id}")
    public ResponseEntity<Void> deleteCorpus(@PathVariable("id") String id) {
        return corporaService.delete(id);
    }
}
