package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.services.SonnetDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Josh Harkema
 */
@Controller
public class FrontPageController {
    private static final String PAGE_TITLE = "Home";
    private final SonnetDetailsService sonnetDetailsService;

    @Autowired
    public FrontPageController(SonnetDetailsService sonnetDetailsService) {
        this.sonnetDetailsService = sonnetDetailsService;
    }

    @SuppressWarnings("SameReturnValue")
    @GetMapping("")
    public String showFrontPage(Model model) {
        model.addAttribute("pageTitle", PAGE_TITLE);
        model.addAttribute("page", sonnetDetailsService.getTwoRandomSonnets());

        return "index";
    }
}
