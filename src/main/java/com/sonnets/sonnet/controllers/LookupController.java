package com.sonnets.sonnet.controllers;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * This controller handles all the search functionality, the xml conversion functionality, and the csv conversion
 * functionality. This should probably be in more than one class.
 *
 * @author Josh Harkema
 */
@SuppressWarnings("SameReturnValue")
@Controller
public class LookupController {
    private static final Logger LOGGER = Logger.getLogger(LookupController.class);

    private static final String PAGE_TITLE = "pageTitle";
    private static final String LOOKUP = "lookup";


    /**
     * This is terrible, but I don't want to screw with passing POST requests around. Deal with it.
     *
     * @return a ModelAndView of the search form and search results.
     */
    @GetMapping(name = LOOKUP, value = "/lookup")
    public String showSearchPage(Model model) {
        LOGGER.debug("Showing lookup page.");
        model.addAttribute(PAGE_TITLE, "Search");

        return LOOKUP;
    }

    /**
     * Shows the selection page for the multi selector.
     *
     * @param model the model to attach all the sonnet objects to.
     * @return the multi selector page.
     */
    @GetMapping("/lookup/csv")
    public String showSelectionPage(Model model) {
        LOGGER.debug("Showing manual selection page.");
        model.addAttribute(PAGE_TITLE, "Manual Selection");
        return "select";
    }
}
