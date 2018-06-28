package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.ContactDto;
import com.sonnets.sonnet.services.EmailServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * Handles the about page and POST contact requests.
 *
 * @author Josh Harkema
 */
@Controller
public class AboutController {
    private static final Logger LOGGER = Logger.getLogger(AboutController.class);
    private static final String PAGE_TITLE_CONST = "pageTitle";
    private static final String PAGE_TITLE_VALUE = "About";
    private static final String ABOUT = "about";
    private static final String EMAIL_TO = "ullyot@ucalgary.ca";
    private static final String EMAIL_SUBJECT = "Sonnet Database Query";
    private final EmailServiceImpl emailService;

    public AboutController(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/about")
    public String showAboutPage(Model model) {
        LOGGER.debug("Showing about page.");
        model.addAttribute(PAGE_TITLE_CONST, PAGE_TITLE_VALUE);
        model.addAttribute("ContactDto", new ContactDto());

        return ABOUT;
    }

    /**
     * Handle inbound contact forms.
     *
     * @param contactDto a valid contact dto.
     * @param result     the validation result.
     * @param model      to attach things to.
     * @return an error message or a success message.
     */
    @PostMapping("/about/contact")
    public String handleContactForm(@Valid @ModelAttribute ContactDto contactDto, BindingResult result,
                                    Model model) {
        if (result.hasErrors()) {
            model.addAttribute(PAGE_TITLE_CONST, PAGE_TITLE_VALUE);
            model.addAttribute(contactDto);

            return ABOUT;
        }

        // Compose message.
        String message = "Message from: " + contactDto.getName() +
                "\nEmail address: " + contactDto.getEmail() +
                "\n\nAdd to mailing list: " + contactDto.isMailingList() +
                "\n\nMessage:\n " + contactDto.getMessage();

        emailService.sendSimpleMessage(EMAIL_TO, EMAIL_SUBJECT, message);
        model.addAttribute("ContactDto", new ContactDto());
        model.addAttribute("status", "success");

        return ABOUT;
    }
}
