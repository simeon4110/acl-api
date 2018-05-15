package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.models.SonnetDTO;
import com.sonnets.sonnet.models.SonnetDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * View controller. This is just a prototype.
 *
 * @author Josh Harkema
 */
@Controller
public class InputController {
    private final SonnetDetailsService sonnetDetailsService;

    @Autowired
    public InputController(SonnetDetailsService sonnetDetailsService) {
        this.sonnetDetailsService = sonnetDetailsService;
    }

    // :TODO: Remove this once a real index is in place.
    @GetMapping("/")
    public String showIndex(Model model) {
        model.addAttribute("SonnetDTO", new SonnetDTO());
        return "input";
    }

    @GetMapping("/insert")
    public String showInsertPage(Model model) {
        model.addAttribute("SonnetDTO", new SonnetDTO());
        return "input";
    }

    @PostMapping("/insert")
    public String getInsertPOST(@ModelAttribute("SonnetDTO") @Valid SonnetDTO sonnetDTO, Model model) {

        sonnetDetailsService.addNewSonnet(sonnetDTO);
        model.addAttribute("SonnetDTO", new SonnetDTO());
        return "input";
    }
}
