package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.models.SonnetDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Josh Harkema
 */
@Controller
public class LookupController {
    private final SonnetDetailsService sonnetDetailsService;

    @Autowired
    public LookupController(SonnetDetailsService sonnetDetailsService) {
        this.sonnetDetailsService = sonnetDetailsService;
    }

    @GetMapping("/lookup")
    public String showLookupPage(Model model) {
        model.addAttribute("sonnets", sonnetDetailsService.getAllSonnets());
        return "lookup";
    }
}
