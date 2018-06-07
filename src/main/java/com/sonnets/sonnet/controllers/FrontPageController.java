package com.sonnets.sonnet.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Josh Harkema
 */
@Controller
public class FrontPageController {
    @SuppressWarnings("SameReturnValue")
    @GetMapping("")
    public String showFrontPage(Model model) {
        model.addAttribute("pageTitle", "Home");

        return "index";
    }
}
