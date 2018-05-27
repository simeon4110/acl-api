package com.sonnets.sonnet.controllers;


import com.sonnets.sonnet.models.Sonnet;
import com.sonnets.sonnet.services.SonnetDetailsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping(value = "/sonnets/all", produces = "application/json")
    public List<Sonnet> getAllSonnets() {
        logger.debug("Returning all sonnets.");
        return sonnetDetailsService.getAllSonnets();
    }

    /**
     * @param ids a string array of sonnet Id's to return.
     * @return a list of sonnets by id.
     */
    @GetMapping(value = "/sonnets/by_id/{ids}", produces = "application/json")
    public List<Sonnet> getSonnetByIds(@PathVariable String[] ids) {
        logger.debug("Returning sonnets with ids: " + Arrays.toString(ids));
        return sonnetDetailsService.getSonnetsByIds(ids);
    }

    /**
     * @param lastName author's last name.
     * @return a list of sonnets filtered by author's last name.
     */
    @GetMapping(value = "/sonnets/by_author_last_name/{lastName}", produces = "application/json")
    public List<Sonnet> getSonnetByAuthorLastName(@PathVariable String lastName) {
        logger.debug("Returning sonnets with author's last name: " + lastName);
        return sonnetDetailsService.getSonnetsByAuthorLastName(lastName);
    }

    /**
     * @param firstName author's first name.
     * @return a list of sonnets filtered by author's first name.
     */
    @GetMapping(value = "/sonnets/by_author_first_name/{firstName}", produces = "application/json")
    public List<Sonnet> getSonnetByAuthorFirstName(@PathVariable String firstName) {
        logger.debug("Returning sonnets with author's first name: " + firstName);
        return sonnetDetailsService.getSonnetsByAuthorFirstName(firstName);
    }

}
