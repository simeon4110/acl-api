package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.models.SonnetDto;
import com.sonnets.sonnet.services.SonnetDetailsService;
import org.apache.log4j.Logger;
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
    private static final Logger logger = Logger.getLogger(InputController.class);

    @Autowired
    public InputController(SonnetDetailsService sonnetDetailsService) {
        this.sonnetDetailsService = sonnetDetailsService;
    }

    @GetMapping("/insert")
    public String showInsertPage(Model model) {
        model.addAttribute("SonnetDto", new SonnetDto());
        return "insert";
    }

    @PostMapping("/insert")
    public String getInsertPOST(@ModelAttribute("SonnetDto") @Valid SonnetDto sonnetDto, Model model) {
        logger.debug("Adding sonnet: " + sonnetDto.toString());
        sonnetDetailsService.addNewSonnet(sonnetDto);
        model.addAttribute("SonnetDto", new SonnetDto());
        return "insert";
    }
}
