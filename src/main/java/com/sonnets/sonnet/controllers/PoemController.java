package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.base.RejectDto;
import com.sonnets.sonnet.persistence.dtos.poetry.PoemDto;
import com.sonnets.sonnet.persistence.dtos.poetry.PoemOutDto;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.services.PoemService;
import com.sonnets.sonnet.tools.ParseParam;
import com.sonnets.sonnet.tools.PoemConverter;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.PropertySource;
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
import java.util.concurrent.CompletableFuture;

/**
 * Controller for all poetry related endpoints.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class PoemController {
    private final PoemService poemService;

    @Autowired
    public PoemController(PoemService poemService) {
        this.poemService = poemService;
    }

    /**
     * @param id the id of the poem to get.
     * @return a poem.
     */
    @Cacheable(value = "poem-single", key = "#id")
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poems/by_id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Poem getPoemById(@PathVariable("id") String id) {
        return poemService.getById(id);
    }

    /**
     * @param ids the list of poem ids to get.
     * @return a list of poems.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poems/by_ids/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Poem> getPoemsByIds(@PathVariable String[] ids) {
        return poemService.getByIds(ids);
    }

    /**
     * @return two random sonnets.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poems/two_random", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PoemOutDto> getTwoRandomSonnets() {
        return poemService.getTwoRandomSonnets();
    }

    /**
     * @return all poems in the database.
     */
    @Cacheable(value = "poems-all")
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poems/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getAllPoems() {
        return poemService.getAll();
    }

    /**
     * @return all poems in the database paged.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poems/all/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page getAllPoemsPaged(Pageable pageable) {
        return poemService.getAllPaged(pageable);
    }

    /**
     * @param form the form to get.
     * @return all poems of a specific form.
     */
    @Cacheable(value = "poems-by-form", key = "#form")
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poems/by_form/{form}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Poem> getAllByForm(@PathVariable("form") String form) {
        return poemService.getAllByForm(form);
    }

    /**
     * @param form the form to get.
     * @return all poems of a specific form paged.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poems/by_form_paged/{form}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page getAllByFormPaged(@PathVariable("form") String form, Pageable pageable) {
        return poemService.getAllByFormPaged(form, pageable);
    }

    /**
     * @return all poems added by a user.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/poem/get_user_poems", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<List> getUserPoems(Principal principal) {
        return poemService.getAllByUser(principal);
    }

    /**
     * @param lastName the last name of the author to search for.
     * @return a list of poems matching the last name.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/poems/search/by_last_name/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Poem> getByAuthorLastName(@PathVariable("lastName") String lastName) {
        lastName = ParseParam.parse(lastName);
        return poemService.getAllByAuthorLastName(lastName);
    }

    /**
     * Add a poem to the db.
     */
    @CacheEvict(value = "poems-all")
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/poem/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addPoem(@RequestBody @Valid PoemDto dto) {
        return poemService.add(dto);
    }

    /**
     * Edit a poem (ADMIN ONLY).
     */
    @CacheEvict(value = "poem-single", key = "#dto.id")
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PutMapping(value = "/secure/poem/edit_admin", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> editPoemAdmin(@RequestBody @Valid PoemDto dto) {
        return poemService.modify(dto);
    }

    /**
     * Edit a poem (OWNER ONLY).
     */
    @CacheEvict(value = "poem-single", key = "#dto.id")
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/poem/edit_user", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> editPoem(@RequestBody @Valid PoemDto dto, Principal principal) {
        return poemService.modify(dto, principal);
    }

    /**
     * Delete a poem (ADMIN ONLY).
     */
    @CacheEvict(value = "poem-single", key = "#id")
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/poem/delete_admin/{id}")
    public ResponseEntity<Void> deletePoemAdmin(@PathVariable("id") String id) {
        return poemService.deleteById(id);
    }

    /**
     * Delete a poem (OWNER ONLY).
     */
    @CacheEvict(value = "poem-single", key = "id")
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/poem/delete_user/{id}")
    public ResponseEntity<Void> deletePoem(@PathVariable("id") String id, Principal principal) {
        return poemService.deleteById(id, principal);
    }

    //####################### These are endpoints for the confirmation module. #################//

    /**
     * Confirm a poem by id.
     *
     * @param id the id of the poem to confirm.
     */
    @CacheEvict(value = "poem-single", key = "id")
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/poem/confirm/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> confirmPoem(@PathVariable String id, Principal principal) {
        return poemService.confirm(id, principal);
    }

    /**
     * Reject a poem.
     */
    @CacheEvict(value = "poem-single", key = "#rejectDto.id")
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/poem/reject", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> rejectPoem(@RequestBody @Valid RejectDto rejectDto) {
        return poemService.reject(rejectDto);
    }

    /**
     * @return an unconfirmed poem.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/poem/confirm_get", produces = MediaType.APPLICATION_JSON_VALUE)
    public Poem getPoemToConfirm(Principal principal) {
        return poemService.getPoemToConfirm(principal);
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
    @CrossOrigin(origins = "${allowed-origin}")
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
    @CrossOrigin(origins = "${allowed-origin}")
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
    @CrossOrigin(origins = "${allowed-origin}")
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
    @CrossOrigin(origins = "${allowed-origin}")
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
