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
        model.addAttribute("SonnetDto", new SonnetDto());
        model.addAttribute("username", request.getUserPrincipal().getName());

        return "input";
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
        if (result.hasErrors()) {
            model.addAttribute("SonnetDto", sonnetDto);
            model.addAttribute("username", request.getUserPrincipal().getName()); // Don't forget!

            return "input";
        }

        logger.debug("Adding sonnet: " + sonnetDto.toString());
        Sonnet sonnet = sonnetDetailsService.addNewSonnet(sonnetDto);

        // Catch duplicate sonnets.
        if (sonnet == null) {
            model.addAttribute("error", "It looks like this sonnet already exists. Please search for a " +
                    "sonnet by clicking 'Search' in the menu above before attempting to add to the database. To edit " +
                    "a sonnet in the database, simply click the 'edit' link under its card once you have searched " +
                    "the database for it. If " +
                    "you have received this message in error, please email joshua.harkema@ucalgary.ca");
        } else {
            model.addAttribute("SonnetDto", sonnetDto);
            model.addAttribute("username", request.getUserPrincipal().getName()); // Don't forget!
        }

        return "input";
    }
}
