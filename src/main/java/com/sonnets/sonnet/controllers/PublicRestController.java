package com.sonnets.sonnet.controllers;


import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.services.SearchService;
import com.sonnets.sonnet.services.SonnetDetailsService;
import com.sonnets.sonnet.tools.ParseParam;
import com.sonnets.sonnet.tools.SonnetConverter;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * REST handler for sonnet db searches via api interface.
 *
 * @author Josh Harkema
 */
@RestController
public class PublicRestController {
    private static final Logger LOGGER = Logger.getLogger(PublicRestController.class);
    private final SonnetDetailsService sonnetDetailsService;
    private final SearchService searchService;

    private static final String ALLOWED_ORIGIN = "http://127.0.0.1:4200";

    @Autowired
    public PublicRestController(SonnetDetailsService sonnetDetailsService, SearchService searchService) {
        this.sonnetDetailsService = sonnetDetailsService;
        this.searchService = searchService;
    }

    /**
     * @return all sonnets as JSON.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sonnet> getAllSonnets() {
        LOGGER.debug("Returning all sonnets.");
        return sonnetDetailsService.getAllSonnets();
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/two_random", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sonnet> getTwoRandomSonnets() {
        LOGGER.debug("Returning two random sonnets.");
        return sonnetDetailsService.getTwoRandomSonnets();
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/all/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page getAllSonnetsPaged(Pageable pageable) {
        LOGGER.debug("Returning page request: " + pageable);

        return sonnetDetailsService.getAllSonnetsPaged(pageable);
    }

    /**
     * @param ids a string array of sonnet Id's to return.
     * @return a list of sonnets by id.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/by_id/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sonnet> getSonnetByIds(@PathVariable String[] ids) {
        LOGGER.debug("Returning sonnets with ids: " + Arrays.toString(ids));
        return sonnetDetailsService.getSonnetsByIds(ids);
    }

    /**
     * @param period the period to search for.
     * @return a list of the search results or null.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/search/period/{period}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getSearchByPeriod(@PathVariable String period) {
        return searchService.searchByPeriod(period);
    }

    /**
     * Search handler for front end website.
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

        LOGGER.debug("\nREST search for: " + "\nfirst name: " + firstName + "\nlast name: " + lastName +
                "\ntitle: " + title + "\nperiod: " + period + "\ntext: " + text);

        return searchService.executeSearch(ParseParam.parse(firstName), ParseParam.parse(lastName),
                ParseParam.parse(title), ParseParam.parse(period), ParseParam.parse(text), pageRequest);
    }

    /**
     * Get all lines of poetry as txt.
     *
     * @return an output stream of the text.
     * @throws IOException if InputStream is broken.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/sonnets/txt/all", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody
    byte[] getAllText() throws IOException {
        List<Sonnet> sonnets = sonnetDetailsService.getAllSonnets();
        String sonnetTXT = SonnetConverter.sonnetsToText(sonnets);
        InputStream sonnetsOut = new ByteArrayInputStream(sonnetTXT.getBytes(StandardCharsets.UTF_8));

        return IOUtils.toByteArray(sonnetsOut);
    }

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
