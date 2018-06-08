package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;
import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.services.SonnetDetailsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * View controller for Sonnet insert requests.
 *
 * @author Josh Harkema
 */
@SuppressWarnings("SameReturnValue")
@Controller
public class InputController {
    private final SonnetDetailsService sonnetDetailsService;
    private static final Logger logger = Logger.getLogger(InputController.class);

    private static final String[] PERIODS = {"1500-1550", "1550-1600", "1600-1650", "1650-1700", "1700-1750",
            "1850-1800", "1800-1850", "1850-1900", "1950-2000", "2000-present"};
    private static final String[] PUB_STMT = {"public domain", "copyrighted"};
    private static final String PAGE_TITLE = "pageTitle";
    private static final String PAGE_TITLE_TEXT = "Add Sonnet";
    private static final String SONNET_DTO = "SonnetDto"; // Name of dto attached to model.
    private static final String USERNAME = "username"; // Name of user attached to model.
    private static final String INPUT = "input"; // Name of page returned.

    @Autowired
    public InputController(SonnetDetailsService sonnetDetailsService) {
        this.sonnetDetailsService = sonnetDetailsService;
    }

    /**
     * Shows the insert page (i.e. add a sonnet to the db.)
     *
     * @param model the model to attach the SonnetDto to.
     * @return the insert page with SonnetDto attached.
     */
    @GetMapping("/insert")
    public String showInsertPage(Model model, HttpServletRequest request) {
        model.addAttribute(SONNET_DTO, new SonnetDto());
        model.addAttribute(USERNAME, request.getUserPrincipal().getName());
        model.addAttribute(PAGE_TITLE, PAGE_TITLE_TEXT);
        model.addAttribute("periods", PERIODS);
        model.addAttribute("publications", PUB_STMT);

        return INPUT;
    }

    /**
     * Handles incoming add sonnet post requests.
     *
     * @param sonnetDto the SonnetDto from the form submission.
     * @param model     the model from the request, needed to attach errors, or valid Sonnet objects.
     * @return the insert page with error or newly added sonnet attached.
     */
    @PostMapping("/insert")
    public String getInsertPOST(@Valid @ModelAttribute("SonnetDto") SonnetDto sonnetDto, BindingResult result,
                                Model model, HttpServletRequest request) {

//        // Catch validation errors.
//        if (result.hasErrors()) {
//            model.addAttribute(SONNET_DTO, sonnetDto);
//            model.addAttribute(USERNAME, request.getUserPrincipal().getName()); // Don't forget!
//            model.addAttribute(PAGE_TITLE, PAGE_TITLE_TEXT);
//            model.addAttribute("periods", PERIODS);
//            model.addAttribute("publications", PUB_STMT);
//
//            return INPUT;
//        }

        logger.debug("Adding sonnet: " + sonnetDto.toString());
        Sonnet sonnet = sonnetDetailsService.addNewSonnet(sonnetDto);

        // Catch duplicate sonnets.
        if (sonnet == null) {
            model.addAttribute("status", "exists");

            return INPUT;
        } else {
            model.addAttribute("publications", PUB_STMT);
        }

        model.addAttribute(SONNET_DTO, sonnetDto);
        model.addAttribute(USERNAME, request.getUserPrincipal().getName()); // Don't forget!
        model.addAttribute(PAGE_TITLE, PAGE_TITLE_TEXT);
        model.addAttribute("status", "success");
        model.addAttribute("periods", PERIODS);

        return INPUT;
    }
}
