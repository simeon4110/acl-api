package com.sonnets.sonnet.controllers;


import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.services.SonnetDetailsService;
import com.sonnets.sonnet.tools.SonnetConverter;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
public class RestControllerImpl {
    private static final Logger logger = Logger.getLogger(RestControllerImpl.class);
    private final SonnetDetailsService sonnetDetailsService;

    @Autowired
    public RestControllerImpl(SonnetDetailsService sonnetDetailsService) {
        this.sonnetDetailsService = sonnetDetailsService;
    }

    /**
     * @return all sonnets as JSON.
     */
    @GetMapping(value = "/sonnets/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sonnet> getAllSonnets() {
        logger.debug("Returning all sonnets.");
        return sonnetDetailsService.getAllSonnets();
    }

    /**
     * @param ids a string array of sonnet Id's to return.
     * @return a list of sonnets by id.
     */
    @GetMapping(value = "/sonnets/by_id/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sonnet> getSonnetByIds(@PathVariable String[] ids) {
        logger.debug("Returning sonnets with ids: " + Arrays.toString(ids));
        return sonnetDetailsService.getSonnetsByIds(ids);
    }

    /**
     * @param lastName author's last name.
     * @return a list of sonnets filtered by author's last name.
     */
    @GetMapping(value = "/sonnets/by_author_last_name/{lastName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sonnet> getSonnetByAuthorLastName(@PathVariable String lastName) {
        logger.debug("Returning sonnets with author's last name: " + lastName);
        return sonnetDetailsService.getSonnetsByAuthorLastName(lastName);
    }

    /**
     * @param firstName author's first name.
     * @return a list of sonnets filtered by author's first name.
     */
    @GetMapping(value = "/sonnets/by_author_first_name/{firstName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sonnet> getSonnetByAuthorFirstName(@PathVariable String firstName) {
        logger.debug("Returning sonnets with author's first name: " + firstName);
        return sonnetDetailsService.getSonnetsByAuthorFirstName(firstName);
    }

    /**
     * @param addedBy the username of the sonnet's contributor.
     * @return a list of all a user's sonnets.
     */
    @GetMapping(value = "/sonnets/by_added_by/{addedBy}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Sonnet> getAllByAddedBy(@PathVariable String addedBy) {
        return sonnetDetailsService.getSonnetsByAddedBy(addedBy);
    }

    /**
     * Get all lines of poetry as txt.
     *
     * @return an output stream of the text.
     * @throws IOException
     */
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
     * @throws IOException
     */
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
     * @throws IOException
     */
    @GetMapping(value = "/sonnets/txt/by_last_name/{lastName}", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody
    byte[] getByLastNameText(@PathVariable("lastName") String lastName) throws IOException {
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
     * @throws IOException
     */
    @GetMapping(value = "/sonnets/txt/by_user/{username}", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody
    byte[] getByUserText(@PathVariable("username") String username) throws IOException {
        List<Sonnet> sonnets = sonnetDetailsService.getSonnetsByAddedBy(username);

        String sonnetTXT = SonnetConverter.sonnetsToText(sonnets);
        InputStream sonnetsOut = new ByteArrayInputStream(sonnetTXT.getBytes(StandardCharsets.UTF_8));

        return IOUtils.toByteArray(sonnetsOut);
    }

}
