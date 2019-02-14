package com.sonnets.sonnet.controllers.annotation;

import com.sonnets.sonnet.persistence.dtos.base.AnnotationDto;
import com.sonnets.sonnet.persistence.models.annotation.Dialog;
import com.sonnets.sonnet.services.annotation.DialogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for Dialog stuff.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class DialogController {
    private final DialogService dialogService;

    @Autowired
    public DialogController(DialogService dialogService) {
        this.dialogService = dialogService;
    }

    /**
     * Get all of a character's dialog objects.
     *
     * @param id the id of the character to get the dialog for.
     * @return all of the character's dialog.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/annotations/dialog/get_by_character/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Dialog> getByCharacter(@PathVariable("id") Long id) {
        return dialogService.getAllFromCharacter(id);
    }

    /**
     * Get all dialog objects.
     *
     * @return all dialog objects.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/annotations/dialog/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Dialog> getAll() {
        return dialogService.getAll();
    }

    /**
     * Delete a single dialog object.
     *
     * @param dialogId  the id of the dialog to delete.
     * @return 200 if successful.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/annotations/dialog/delete/{dialogId}")
    public ResponseEntity<Void> delete(@PathVariable("dialogId") Long dialogId) {
        return dialogService.delete(dialogId);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/annotations/dialog/add", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Dialog add(@RequestBody @Valid AnnotationDto dto) {
        return dialogService.add(dto);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/annotations/dialog/modify", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Dialog modify(@RequestBody @Valid AnnotationDto dto) {
        return dialogService.modify(dto);
    }
}
