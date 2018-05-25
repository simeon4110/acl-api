package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.services.SonnetDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Josh Harkema
 */
@Controller
public class BrowseController {
    private final SonnetDetailsService sonnetDetailsService;

    @Autowired
    public BrowseController(SonnetDetailsService sonnetDetailsService) {
        this.sonnetDetailsService = sonnetDetailsService;
    }

    /**
     * @param pageRequest the page request passed.
     * @return a paged json of all the sonnets paged. Default size 20 objs.
     */
    @GetMapping(value = "/browse")
    public String getPagedList(Pageable pageRequest, Model model) {
        model.addAttribute("page", sonnetDetailsService.getAllSonnetsPaged(pageRequest));
        return "browse";
    }

}
