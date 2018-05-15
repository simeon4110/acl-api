package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.models.SonnetDetailsService;
import com.sonnets.sonnet.models.SonnetDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * View controller. This is just a prototype. Handles all current Sonnet insert methods for the front end.
 *
 * @author Josh Harkema
 */
@SuppressWarnings("SameReturnValue")
@Controller
public class InputController {
    private final SonnetDetailsService sonnetDetailsService;

    @Autowired
    public InputController(SonnetDetailsService sonnetDetailsService) {
        this.sonnetDetailsService = sonnetDetailsService;
    }

    @GetMapping("/insert")
    public String showInsertPage(Model model) {
        model.addAttribute("SonnetDto", new SonnetDto());
        return "input";
    }

    @PostMapping("/insert")
    public String getInsertPOST(@ModelAttribute("SonnetDTO") @Valid SonnetDto sonnetDto, Model model) {
        sonnetDetailsService.addNewSonnet(sonnetDto);
        model.addAttribute("SonnetDto", new SonnetDto());
        return "input";
    }
}
