package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.PasswordChangeDto;
import com.sonnets.sonnet.security.UserDetailsServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * View controller for user profile. Right now this only allows self-service password resets.
 *
 * @author Josh Harkema
 */
@Controller
public class ProfileController {
    private static final Logger logger = Logger.getLogger(ProfileController.class);
    private static final String PASS_DTO = "PasswordChangeDto";
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public ProfileController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Displays the password change form.
     *
     * @param model to attach the PasswordChangeDto to.
     * @return the profile page.
     */
    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        model.addAttribute(PASS_DTO, new PasswordChangeDto());
        return "profile";
    }

    /**
     * Handles post data from the form and changes the users password if CurrentPassword matches and both new password
     * fields are equal.
     *
     * @param passwordChangeDto the dto from the form POST submission.
     * @param request           the request header (for the user principal recovery).
     * @param model             the model to attach the success/error messages and a fresh dto to.
     * @return a success / error redirect based on outcome from UserDetailsService.
     */
    @PostMapping("/profile")
    public String postChangePassword(@ModelAttribute @Valid PasswordChangeDto passwordChangeDto,
                                     HttpServletRequest request, Model model) {
        logger.debug("Changing password for: " + request.getUserPrincipal().getName());
        model.addAttribute(PASS_DTO, new PasswordChangeDto());

        return userDetailsService.updatePassword(request.getUserPrincipal(), passwordChangeDto);
    }
}
