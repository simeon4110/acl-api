package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.corpera.CorperaDto;
import com.sonnets.sonnet.persistence.dtos.corpera.CorperaModifyDto;
import com.sonnets.sonnet.persistence.dtos.corpera.CorperaSonnetDto;
import com.sonnets.sonnet.services.CorperaService;
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
    private final CorperaService corperaService;

    @Autowired
    public CorporaController(CorperaService corperaService) {
        this.corperaService = corperaService;
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN) //
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/secure/corpera/my_corpera", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getUserCorpera(Principal principal) {
        return corperaService.getUserCorpera(principal);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN) //
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping(value = "/secure/corpera/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createCorpera(@RequestBody @Valid CorperaDto corperaDto, Principal principal) {
        return corperaService.createCorpera(corperaDto, principal);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN) //
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping(value = "/secure/corpera/add_sonnet", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addSonnetToCorpera(@RequestBody @Valid CorperaSonnetDto modifySonnetsDto) {
        return corperaService.addSonnets(modifySonnetsDto.getCorperaId(), modifySonnetsDto.getSonnetId());
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN) //
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping(value = "/secure/corpera/remove_sonnet", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removeSonnetFromCorpera(@RequestBody @Valid CorperaSonnetDto modifySonnetsDto) {
        return corperaService.removeSonnets(modifySonnetsDto.getCorperaId(), modifySonnetsDto.getSonnetId());
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN) //
    @PreAuthorize("hasAuthority('USER')")
    @PutMapping(value = "/secure/corpera/change_name", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> ModifyCorperaDetails(@RequestBody @Valid CorperaModifyDto modifyDto) {
        return corperaService.modify(modifyDto.getCorperaId(), modifyDto.getName(), modifyDto.getDescription());
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN, methods = RequestMethod.DELETE)
    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping(value = "/secure/corpera/delete/{id}")
    public ResponseEntity<Void> deleteCorpus(@PathVariable("id") String id) {
        return corperaService.delete(id);
    }
}
