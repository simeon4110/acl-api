package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.services.SonnetDetailsService;
import com.sonnets.sonnet.tools.Pager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

/**
 * Handles requests to the browse page.
 *
 * @author Josh Harkema
 */
@Controller
public class BrowseController {
    private final SonnetDetailsService sonnetDetailsService;
    private static final Logger logger = Logger.getLogger(BrowseController.class);

    // Constants.
    private static final String PAGE_TITLE = "pageTitle";
    private static final int[] PAGE_SIZES = {5, 10, 20, 50};
    private static final int BUTTONS_TO_SHOW = 5;
    private static final String DEFAULT_SORT = "lastName";
    private static final String[][] SORT_BY = {{"firstName", "First Name"}, {DEFAULT_SORT, "Last Name"},
            {"title", "Title"}, {"publicationYear", "Publication Year"}};

    @Autowired
    public BrowseController(SonnetDetailsService sonnetDetailsService) {
        this.sonnetDetailsService = sonnetDetailsService;
    }

    /**
     * @param pageRequest the page request passed.
     * @return a paged HTML page of all the sonnets paged. Default size 20 items.
     */
    @SuppressWarnings("SameReturnValue")
    @GetMapping(value = "/browse")
    public String getPagedList(Pageable pageRequest, Model model) {
        int pageSize = pageRequest.getPageSize();
        String[] sort = pageRequest.getSort().toString().split(":");
        Page<Sonnet> sonnets = sonnetDetailsService.getAllSonnetsPaged(pageRequest);

        // Catch unsorted requests and set the default.
        String sortBy = sort[0].replace(':', ' ').trim();
        if (Objects.equals(sortBy, "UNSORTED")) {
            sortBy = DEFAULT_SORT;
        }

        // One is subtracted to fix strange offset issues.
        Pager pager = new Pager(sonnets.getTotalPages() - 1, pageRequest.getPageNumber(), BUTTONS_TO_SHOW);

        logger.debug("Page size: " + pageSize);
        logger.debug("Sort by: " + sortBy);

        model.addAttribute("page", sonnets);
        model.addAttribute("pageSizes", PAGE_SIZES);
        model.addAttribute("selectedPageSize", pageSize);
        model.addAttribute("pager", pager);
        model.addAttribute("selectedSortBy", sortBy);
        model.addAttribute("sortBy", SORT_BY);
        model.addAttribute(PAGE_TITLE, "Browse");
        return "browse";
    }

}
