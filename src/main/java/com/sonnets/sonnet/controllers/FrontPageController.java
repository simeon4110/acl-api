package com.sonnets.sonnet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Josh Harkema
 */
@Controller
public class FrontPageController {
    private static final String PAGE_TITLE = "Home";

    @SuppressWarnings("SameReturnValue")
    @GetMapping("")
    public String showFrontPage(Model model) {
        model.addAttribute("pageTitle", PAGE_TITLE);

        return "index";
    }
}
