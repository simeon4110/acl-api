package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.services.SearchService;
import com.sonnets.sonnet.services.SonnetDetailsService;
import com.sonnets.sonnet.tools.Pager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

/**
 * This controller handles all the search functionality, the xml conversion functionality, and the csv conversion
 * functionality. This should probably be in more than one class.
 *
 * @author Josh Harkema
 */
@SuppressWarnings("SameReturnValue")
@Controller
public class LookupController {
    private final SonnetDetailsService sonnetDetailsService;
    private final SearchService searchService;
    private static final Logger logger = Logger.getLogger(LookupController.class);

    private static final String PAGE_TITLE = "pageTitle";
    private static final String[] PERIODS = {"", "1500-1550", "1550-1600", "1600-1650", "1650-1700", "1700-1750",
            "1750-1800", "1800-1850", "1850-1900", "1950-2000", "2000-present"};
    private static final String LOOKUP = "lookup";
    private static final String SONNET = "Sonnet";
    private static final String PAGER = "pager";
    private static final String PAGE = "page";
    private static final int BUTTONS_TO_SHOW = 5;

    @Autowired
    public LookupController(SonnetDetailsService sonnetDetailsService, SearchService searchService) {
        this.sonnetDetailsService = sonnetDetailsService;
        this.searchService = searchService;
    }

    /**
     * This is terrible, but I don't want to screw with passing POST requests around. Deal with it.
     *
     * @param sonnet      the sonnet object containing lookup params.
     * @param model       the model to get/add the sonnets to.
     * @param pageRequest the pageable request data.
     * @return a ModelAndView of the search form and search results.
     */
    @GetMapping(value = "/lookup", name = LOOKUP)
    public ModelAndView showSearchPage(@ModelAttribute Sonnet sonnet, @ModelAttribute ModelMap model,
                                       Pageable pageRequest) {
        Page<Sonnet> sonnets;
        Pager pager;

        try {
            sonnets = searchService.search(sonnet, pageRequest);
            // One is subtracted to fix strange offset issues.
            pager = new Pager(sonnets.getTotalPages() - 1, pageRequest.getPageNumber(), BUTTONS_TO_SHOW);
            model.addAttribute(PAGER, pager);
            model.addAttribute("periods", PERIODS);
            model.addAttribute(PAGE, sonnets);
            model.addAttribute(PAGE_TITLE, "Search");
        } catch (NullPointerException e) {
            logger.error(e);
        }

        model.addAttribute(SONNET, sonnet);
        logger.debug("Returning search page: " + model.toString());

        return new ModelAndView(LOOKUP, model);
    }

    /**
     * Shows the selection page for the multi selector.
     *
     * @param model the model to attach all the sonnet objects to.
     * @return the multi selector page.
     */
    @GetMapping("/lookup/csv")
    public String showSelectionPage(Model model) {
        model.addAttribute("sonnets", sonnetDetailsService.getAllSonnets());
        model.addAttribute(PAGE_TITLE, "Manual Selection");
        return "select";
    }
}
