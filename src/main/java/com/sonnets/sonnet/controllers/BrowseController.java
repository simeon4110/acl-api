package com.sonnets.sonnet.controllers;

import org.apache.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Handles requests to the browse page.
 *
 * @author Josh Harkema
 */
@Controller
public class BrowseController {
    private static final Logger LOGGER = Logger.getLogger(BrowseController.class);
    private static final String PAGE_TITLE_CONST = "pageTitle";
    private static final String PAGE_TITLE_VALUE = "Browse";

    /**
     * @param pageRequest the page request passed.
     * @return a paged HTML page of all the sonnets paged. Default size 20 items.
     */
    @GetMapping(value = "/browse")
    public String getPagedList(Pageable pageRequest, Model model) {
        LOGGER.fatal("Showing browse page.");
        model.addAttribute(PAGE_TITLE_CONST, PAGE_TITLE_VALUE);

        return "browse";
    }

}
