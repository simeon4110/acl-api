package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.base.RejectDto;
import com.sonnets.sonnet.persistence.dtos.poetry.PoemDto;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.services.CorporaService;
import com.sonnets.sonnet.services.PoemService;
import com.sonnets.sonnet.tools.PoemConverter;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for all poetry related endpoints.
 *
 * @author Josh Harkema
 */
@RestController
public class PoemController {
    private static final String ALLOWED_ORIGIN = "*";
    private final PoemService poemService;
    private final CorporaService corporaService;

    @Autowired
    public PoemController(PoemService poemService, CorporaService corporaService) {
        this.poemService = poemService;
        this.corporaService = corporaService;
    }

    // Get a poem by id.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/poems/by_id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Poem getPoemById(@PathVariable("id") String id) {
        return poemService.getById(id);
    }

    // Get list of poems by list of ids.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/poems/by_ids/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Poem> getPoemsByIds(@PathVariable String[] ids) {
        return poemService.getByIds(ids);
    }

    // Get two random sonnets.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/poems/two_random", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Poem> getTwoRandomSonnets() {
        return poemService.getTwoRandomSonnets();
    }

    // Get all poems.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/poems/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Poem> getAllPoems() {
        return poemService.getAll();
    }

    // Get all poems paged.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/poems/all/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page getAllPoemsPaged(Pageable pageable) {
        return poemService.getAllPaged(pageable);
    }

    // Get all poems of specific form.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/poems/by_form/{form}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Poem> getAllByForm(@PathVariable("form") String form) {
        return poemService.getAllByForm(form);
    }

    // Get all poems of specific form paged.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/poems/by_form_paged/{form}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page getAllByFormPaged(@PathVariable("form") String form, Pageable pageable) {
        return poemService.getAllByFormPaged(form, pageable);
    }

    // Get all poems created by a user.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/poem/get_user_poems", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getUserPoems(Principal principal) {
        return poemService.getAllByUser(principal);
    }

    // Add a poem.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/poem/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addPoem(@RequestBody @Valid PoemDto dto) {
        return poemService.add(dto);
    }

    // Admin - edit any poem.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value = "/secure/poem/edit_admin", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> editPoemAdmin(@RequestBody @Valid PoemDto dto) {
        return poemService.modify(dto);
    }

    // User - edit poem created by user.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/poem/edit_user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> editPoem(@RequestBody @Valid PoemDto dto, Principal principal) {
        return poemService.modify(dto, principal);
    }

    // Admin - delete any poem.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/poem/delete_admin/{id}")
    public ResponseEntity<Void> deletePoemAdmin(@PathVariable("id") String id) {
        return poemService.deleteById(id);
    }

    // User - edit poem created by user.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/poem/delete_user/{id}")
    public ResponseEntity<Void> deletePoem(@PathVariable("id") String id, Principal principal) {
        return poemService.deleteById(id, principal);
    }

    //####################### These are endpoints for the confirmation module. #################//

    // Confirm a poem.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/poem/confirm/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> confirmPoem(@PathVariable String id, Principal principal) {
        return poemService.confirm(id, principal);
    }

    // Reject a poem.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/poem/reject", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> rejectPoem(@RequestBody @Valid RejectDto rejectDto) {
        return poemService.reject(rejectDto);
    }

    // Get an unconfirmed poem to confirm.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/poem/confirm_get", produces = MediaType.APPLICATION_JSON_VALUE)
    public Poem getPoemToConfirm(Principal principal) {
        return poemService.getPoemToConfirm(principal);
    }

    //####################### These are all corpora related endpoints. #########################//

    // Get all poems in a corpora.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @GetMapping(value = "/secure/corpora/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getAllCorperaItems(@PathVariable("id") String id) {
        return corporaService.getCorporaItems(id);
    }

    // Get all poems in a corpus paged.
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @GetMapping(value = "/secure/corpora/get_paged/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page getAllCorperaItemsPaged(@PathVariable("id") String id, Pageable pageable) {
        return corporaService.getCorporaItemsPaged(id, pageable);
    }

    //####################### These are all file return endpoints. #############################//
    // I've included all the logic in the controller because it is a simple search and attach.

    /**
     * Get selected poems as txt (lines of poetry only).
     *
     * @param ids the poems to return.
     * @return an output stream of the text.
     * @throws IOException if InputStream is broken.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/poem/txt/by_id/{ids}", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody
    byte[] getByIdText(@PathVariable("ids") String[] ids) throws IOException {
        List<Poem> poems = new ArrayList<>();
        for (String s : ids) {
            poems.add(poemService.getById(s));
        }

        String poemTXT = PoemConverter.poemsToText(poems);
        InputStream poemsOut = new ByteArrayInputStream(poemTXT.getBytes(StandardCharsets.UTF_8));

        return IOUtils.toByteArray(poemsOut);
    }

    /**
     * Return poem as XML.
     *
     * @param id the poem ID to return.
     * @return a output stream of the text.
     * @throws IOException if InputStream is broken.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/poem/xml/by_id/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    byte[] getByIdXML(@PathVariable("id") String id) throws IOException {
        Poem poem = poemService.getById(id);

        String poemXML = PoemConverter.poemToXML(poem);
        InputStream poemOut = new ByteArrayInputStream(poemXML.getBytes(StandardCharsets.UTF_8));

        return IOUtils.toByteArray(poemOut);
    }

    /**
     * Return poem as TEI.
     *
     * @param id the poem ID ot return.
     * @return an output stream of the text.
     * @throws IOException if InputStream is broken.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/poem/tei/by_id/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    byte[] getByIdTEI(@PathVariable("id") String id) throws IOException {
        Poem poem = poemService.getById(id);

        String poemTEI = PoemConverter.poemToTEI(poem);
        InputStream poemOut = new ByteArrayInputStream(poemTEI.getBytes(StandardCharsets.UTF_8));

        return IOUtils.toByteArray(poemOut);
    }

    /**
     * Return any number of poems as csv.
     *
     * @param ids a comma separated list of poem ids (i.e. "1,2,3,4")
     * @return an output stream of the csv file.
     * @throws IOException if InputStream is broken.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/poem/csv/by_ids/{ids}", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody
    byte[] getByIdCSV(@PathVariable("ids") String[] ids) throws IOException {
        List<Poem> poems = new ArrayList<>();
        for (String s : ids) {
            poems.add(poemService.getById(s));
        }

        String poemCSV = PoemConverter.poemsToCSV(poems);
        InputStream poemOut = new ByteArrayInputStream(poemCSV.getBytes(StandardCharsets.UTF_8));

        return IOUtils.toByteArray(poemOut);
    }
}
