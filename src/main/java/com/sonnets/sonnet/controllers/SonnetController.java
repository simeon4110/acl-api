package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.sonnet.RejectDto;
import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;
import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.services.CorperaService;
import com.sonnets.sonnet.services.SearchService;
import com.sonnets.sonnet.services.SonnetDetailsService;
import com.sonnets.sonnet.tools.ParseParam;
import com.sonnets.sonnet.tools.SonnetConverter;
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
 * Anything dealing with sonnets is here. Including confirmations and file returns.
 *
 * @author Josh Harkema
 */
@RestController
public class SonnetController {
    private final SonnetDetailsService sonnetDetailsService;
    private final SearchService searchService;
    private final CorperaService corperaService;
    private static final String ALLOWED_ORIGIN = "*";

    @Autowired
    public SonnetController(SonnetDetailsService sonnetDetailsService, SearchService searchService,
                            CorperaService corperaService) {
        this.sonnetDetailsService = sonnetDetailsService;
        this.searchService = searchService;
        this.corperaService = corperaService;
    }

    /**
     * Returns a specific sonnet.
     *
     * @param id the id to get.
     * @return a sonnet as JSON.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/by_id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Sonnet getSonnetById(@PathVariable("id") String id) {
        return sonnetDetailsService.getSonnetByID(id);
    }

    /**
     * PUBLIC.
     *
     * @return all sonnets as JSON.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sonnet> getAllSonnets() {
        return sonnetDetailsService.getAllSonnets();
    }

    /**
     * PUBLIC.
     *
     * @param pageable a valid pageRequest.
     * @return a list of paged sonnets.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/all/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page getAllSonnetsPaged(Pageable pageable) {
        return sonnetDetailsService.getAllSonnetsPaged(pageable);
    }

    /**
     * PUBLIC.
     *
     * @return two randomly selected sonnets.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/two_random", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sonnet> getTwoRandomSonnets() {
        return sonnetDetailsService.getTwoRandomSonnets();
    }

    /**
     * PRIVATE.
     *
     * @param principal the principal from the request.
     * @return all sonnets added by the user making the request.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/sonnet/get_user_sonnets", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getUserSonnets(Principal principal) {
        return sonnetDetailsService.getAllUserSonnets(principal);
    }

    /**
     * PUBLIC.
     *
     * @param ids a string array of sonnet Id's to return.
     * @return a list of sonnets by id.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/by_id/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sonnet> getSonnetByIds(@PathVariable String[] ids) {
        return sonnetDetailsService.getSonnetsByIds(ids);
    }

    /**
     * PRIVATE.
     *
     * @param sonnetDto a valid sonnet dto.
     * @return http.ok if the request is good.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/sonnet/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> addSonnet(@RequestBody @Valid SonnetDto sonnetDto, Principal principal) {
        return sonnetDetailsService.addNewSonnet(sonnetDto, principal);
    }

    /**
     * PRIVATE - Allows an administrator to delete any sonnet.
     *
     * @param id sonnet's id to delete.
     * @return http.ok if request is good.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/sonnet/delete/{id}")
    public ResponseEntity<Void> deleteSonnet(@PathVariable("id") String id) {
        return sonnetDetailsService.deleteSonnetById(id);
    }

    /**
     * PRIVATE - Allows a user to delete their own sonnets.
     *
     * @param id        the sonnet to delete.
     * @param principal the principal from the request.
     * @return http.ok if the request is good.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/user/sonnet/delete/{id}")
    public ResponseEntity<Void> deleteUserSonnet(@PathVariable("id") String id, Principal principal) {
        return sonnetDetailsService.deleteUserSonnetById(id, principal);
    }

    /**
     * PRIVATE - Allows a user to edit their own sonnets, admins can edit all.
     *
     * @param sonnetDto a valid dto.
     * @return http.ok if the request is good.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/sonnet/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> editSonnet(@RequestBody @Valid SonnetDto sonnetDto) {
        return sonnetDetailsService.updateSonnet(sonnetDto);
    }

    //####################### These are all search endpoints. ##################################//

    /**
     * PUBLIC - Search handler for front end website.
     *
     * @param firstName author's first name.
     * @param lastName  author's last name.
     * @param title     sonnet's title.
     * @param period    sonnet's period of publication.
     * @param text      search sonnet's body for this string.
     * @return a json formatted response of the results. An empty array is returned when nothing is found.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Sonnet> getSearchFromDto(@RequestParam("firstName") String firstName,
                                         @RequestParam("lastName") String lastName,
                                         @RequestParam("title") String title,
                                         @RequestParam("period") String period,
                                         @RequestParam("text") String text, Pageable pageRequest) {
        firstName = ParseParam.parse(firstName);
        lastName = ParseParam.parse(lastName);
        title = ParseParam.parse(title);
        text = ParseParam.parse(text);

        return searchService.executeSearch(ParseParam.parse(firstName), ParseParam.parse(lastName),
                ParseParam.parse(title), ParseParam.parse(period), ParseParam.parse(text), pageRequest);
    }

    /**
     * PUBLIC - This method simply counts the number of results from a search and returns their ids.
     *
     * @return a list of sonnet ids.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/search/get_result_ids", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Long> getSearchResultIds(@RequestParam("firstName") String firstName,
                                         @RequestParam("lastName") String lastName,
                                         @RequestParam("title") String title,
                                         @RequestParam("period") String period,
                                         @RequestParam("text") String text) {
        firstName = ParseParam.parse(firstName);
        lastName = ParseParam.parse(lastName);
        title = ParseParam.parse(title);
        text = ParseParam.parse(text);

        return searchService.getResultIds(firstName, lastName, title, period, text);
    }

    //####################### These are all corpora related endpoints. #########################//

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @GetMapping(value = "/secure/corpera/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sonnet> getAllCorperaSonnets(@PathVariable("id") String id) {
        return corperaService.getCorperaSonnets(id);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('USER', 'GUEST')")
    @GetMapping(value = "/secure/corpera/get_paged/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Sonnet> getAllCorperaSonnetsPaged(@PathVariable("id") String id, Pageable pageable) {
        return corperaService.getCorperaSonnetsPaged(id, pageable);
    }

    //####################### These are endpoints for the confirmation module. #################//

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/sonnet/confirm/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> confirmSonnet(@PathVariable String id, Principal principal) {
        return sonnetDetailsService.confirmSonnet(id, principal);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/sonnet/reject", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> rejectSonnet(@RequestBody @Valid RejectDto rejectDto, Principal principal) {
        return sonnetDetailsService.rejectSonnet(rejectDto, principal);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/sonnet/confirm_get", produces = MediaType.APPLICATION_JSON_VALUE)
    public Sonnet getSonnetToConfirm(Principal principal) {
        return sonnetDetailsService.getSonnetToConfirm(principal);
    }

    //####################### These are all file return endpoints. #############################//
    // I've included all the logic in the controller because it is a simple search and attach.

    /**
     * Get selected sonnets as txt (lines of poetry only).
     *
     * @param ids the sonnets to return.
     * @return an output stream of the text.
     * @throws IOException if InputStream is broken.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/txt/by_id/{ids}", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody
    byte[] getByIdText(@PathVariable("ids") String[] ids) throws IOException {
        List<Sonnet> sonnets = new ArrayList<>();
        for (String s : ids) {
            sonnets.add(sonnetDetailsService.getSonnetByID(s));
        }

        String sonnetTXT = SonnetConverter.sonnetsToText(sonnets);
        InputStream sonnetsOut = new ByteArrayInputStream(sonnetTXT.getBytes(StandardCharsets.UTF_8));

        return IOUtils.toByteArray(sonnetsOut);
    }

    /**
     * Return all sonnets by an author's last name (lines of poetry only).
     *
     * @param lastName the author's name to return.
     * @return an output stream of the text.
     * @throws IOException if InputStream is broken.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/txt/by_last_name/{lastName}", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody
    byte[] getByLastNameText(@PathVariable("lastName") String lastName) throws IOException {
        lastName = ParseParam.parse(lastName);
        List<Sonnet> sonnets = sonnetDetailsService.getSonnetsByAuthorLastName(lastName);

        String sonnetTXT = SonnetConverter.sonnetsToText(sonnets);
        InputStream sonnetsOut = new ByteArrayInputStream(sonnetTXT.getBytes(StandardCharsets.UTF_8));

        return IOUtils.toByteArray(sonnetsOut);
    }

    /**
     * Return all sonnets submitted by a user (lines of poetry only).
     *
     * @param username the username of the submitter to return.
     * @return an output stream of the text.
     * @throws IOException if InputStream is broken.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/txt/by_user/{username}", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody
    byte[] getByUserText(@PathVariable("username") String username) throws IOException {
        List<Sonnet> sonnets = sonnetDetailsService.getSonnetsByAddedBy(username);

        String sonnetTXT = SonnetConverter.sonnetsToText(sonnets);
        InputStream sonnetsOut = new ByteArrayInputStream(sonnetTXT.getBytes(StandardCharsets.UTF_8));

        return IOUtils.toByteArray(sonnetsOut);
    }

    /**
     * Return sonnet as XML.
     *
     * @param id the sonnet ID to return.
     * @return a output stream of the text.
     * @throws IOException if InputStream is broken.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/xml/by_id/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    byte[] getByIdXML(@PathVariable("id") String id) throws IOException {
        Sonnet sonnet = sonnetDetailsService.getSonnetByID(id);

        String sonnetXML = SonnetConverter.sonnetToXML(sonnet);
        InputStream sonnetOut = new ByteArrayInputStream(sonnetXML.getBytes(StandardCharsets.UTF_8));

        return IOUtils.toByteArray(sonnetOut);
    }

    /**
     * Return sonnet as TEI.
     *
     * @param id the sonnet ID ot return.
     * @return an output stream of the text.
     * @throws IOException if InputStream is broken.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/tei/by_id/{id}", produces = MediaType.APPLICATION_XML_VALUE)
    public @ResponseBody
    byte[] getByIdTEI(@PathVariable("id") String id) throws IOException {
        Sonnet sonnet = sonnetDetailsService.getSonnetByID(id);

        String sonnetTEI = SonnetConverter.sonnetToTEI(sonnet);
        InputStream sonnetOut = new ByteArrayInputStream(sonnetTEI.getBytes(StandardCharsets.UTF_8));

        return IOUtils.toByteArray(sonnetOut);
    }

    /**
     * Return any number of sonnets as csv.
     *
     * @param ids a comma separated list of sonnet ids (i.e. "1,2,3,4")
     * @return an output stream of the csv file.
     * @throws IOException if InputStream is broken.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/csv/by_ids/{ids}", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody
    byte[] getByIdCSV(@PathVariable("ids") String[] ids) throws IOException {
        List<Sonnet> sonnets = new ArrayList<>();
        for (String s : ids) {
            sonnets.add(sonnetDetailsService.getSonnetByID(s));
        }

        String sonnetCSV = SonnetConverter.sonnetsToCSV(sonnets);
        InputStream sonnetOut = new ByteArrayInputStream(sonnetCSV.getBytes(StandardCharsets.UTF_8));

        return IOUtils.toByteArray(sonnetOut);
    }

}
