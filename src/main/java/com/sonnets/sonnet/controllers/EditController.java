package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.sonnet.SonnetDto;
import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.services.SonnetDetailsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for all sonnet edit requests.
 *
 * @author Josh Harkema
 */
@Controller
public class EditController {
    private static final Logger LOGGER = Logger.getLogger(EditController.class);

    private static final String[] PERIODS = {"1500-1550", "1550-1600", "1600-1650", "1650-1700", "1700-1750",
            "1750-1800", "1800-1850", "1850-1900", "1900-1950", "1950-2000", "2000-present"};
    private static final String[] PUB_STMT = {"public domain", "copyrighted"};
    private static final String PAGE_TITLE = "pageTitle";
    private static final String SONNET = "SonnetDto";
    private static final String EDIT = "edit";
    private final SonnetDetailsService sonnetDetailsService;

    @Autowired
    public EditController(SonnetDetailsService sonnetDetailsService) {
        this.sonnetDetailsService = sonnetDetailsService;
    }

    /**
     * Edit a sonnet by ID.
     *
     * @param id    the id of the sonnet to edit.
     * @param model the model with/out the sonnet object.
     * @return a html page with the sonnet data populated for editing.
     */
    @GetMapping("/edit/{id}")
    public String editSonnet(@PathVariable("id") String id, Model model,
                             HttpServletRequest request) {
        LOGGER.debug("Editing sonnet: " + id);
        Sonnet sonnet = sonnetDetailsService.getSonnetByID(id);
        SonnetDto sonnetDto = new SonnetDto(sonnet);
        model.addAttribute(SONNET, sonnetDto);
        model.addAttribute("id", sonnet.getId());
        model.addAttribute("username", request.getUserPrincipal().getName()); // Don't forget!
        model.addAttribute(PAGE_TITLE, "Modify " + sonnet.getId());
        model.addAttribute("periods", PERIODS);
        model.addAttribute("publications", PUB_STMT);

        return EDIT;
    }

    /**
     * Parse the new edited data.
     *
     * @param sonnet the sonnet's new data.
     * @param model  the model with/out the sonnet object.
     * @return an html page with the NEW sonnet data populated for editing.
     */
    @PostMapping(value = "/edit/{id}")
    public String postEditSonnet(@PathVariable("id") String id, @ModelAttribute SonnetDto sonnet, Model model,
                                 HttpServletRequest request) {
        LOGGER.debug("Posting new sonnet details for id: " + sonnet.getId());
        Sonnet newSonnet = sonnetDetailsService.updateSonnet(sonnet);
        sonnet = new SonnetDto(newSonnet);
        model.addAttribute(SONNET, sonnet);
        model.addAttribute("id", sonnet.getId());
        model.addAttribute("username", request.getUserPrincipal().getName()); // Don't forget!
        model.addAttribute(PAGE_TITLE, "Modify " + sonnet.getId());
        model.addAttribute("periods", PERIODS);
        model.addAttribute("publications", PUB_STMT);
        model.addAttribute("status", "success");

        return EDIT;
    }
}
