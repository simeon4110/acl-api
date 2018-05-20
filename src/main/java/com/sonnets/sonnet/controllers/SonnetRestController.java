package com.sonnets.sonnet.controllers;


import com.sonnets.sonnet.models.Sonnet;
import com.sonnets.sonnet.models.SonnetDetailsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Josh Harkema
 */
@RestController
public class SonnetRestController {
    private static final Logger logger = Logger.getLogger(LookupController.class);
    private final SonnetDetailsService sonnetDetailsService;

    @Autowired
    public SonnetRestController(SonnetDetailsService sonnetDetailsService) {
        this.sonnetDetailsService = sonnetDetailsService;
    }

    @GetMapping(value = "/sonnets/all", produces = "application/json")
    public List<Sonnet> getAllSonnets() {
        return sonnetDetailsService.getAllSonnets();
    }
}
