package org.acl.database.controllers.base;

import org.acl.database.persistence.dtos.base.ConfirmationDto;
import org.acl.database.services.base.ConfirmationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

/**
 * Handles all endpoints for confirmation related CRUD.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class ConfirmationController {
    private final ConfirmationService confirmationService;

    @Autowired
    public ConfirmationController(ConfirmationService confirmationService) {
        this.confirmationService = confirmationService;
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/confirmation/get_poem", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPoemToConfirm(Principal principal) {
        return confirmationService.getPoemToConfirm(principal);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/confirmation/confirm_poem")
    public ResponseEntity<Void> confirmPoem(@RequestBody @Valid ConfirmationDto dto, Principal principal) {
        return confirmationService.confirmPoem(dto, principal);
    }

    @CrossOrigin
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/confirmation/reject_poem")
    public ResponseEntity<Void> rejectPoem(@RequestBody @Valid ConfirmationDto dto, Principal principal) {
        return confirmationService.rejectPoem(dto, principal);
    }
}
