package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.user.PasswordChangeDto;
import com.sonnets.sonnet.security.UserDetailsServiceImpl;
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
 * View controller for user profile. Right now this only allows self-service password resets.
 *
 * @author Josh Harkema
 */
@Controller
public class UserController {
    private static final Logger LOGGER = Logger.getLogger(UserController.class);

    private static final String PAGE_TITLE = "pageTitle";
    private static final String PASS_DTO = "PasswordChangeDto";
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public UserController(UserDetailsServiceImpl userDetailsService) {
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
        model.addAttribute(PAGE_TITLE, "Reset Password");

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
    public String postChangePassword(@ModelAttribute(PASS_DTO) @Valid PasswordChangeDto passwordChangeDto,
                                     BindingResult result, HttpServletRequest request, Model model) {
        LOGGER.debug("Changing password for: " + request.getUserPrincipal().getName());

        if (result.hasErrors()) { // Catch password validation errors.
            LOGGER.debug("Password validation error: " + result.getAllErrors().toString());
            model.addAttribute(PASS_DTO, passwordChangeDto);

            return "profile";
        } else {
            model.addAttribute(PASS_DTO, new PasswordChangeDto());

            return userDetailsService.updatePassword(request.getUserPrincipal(), passwordChangeDto);
        }
    }

    /**
     * Page shows all sonnets added to the db by currently logged in user.
     *
     * @param model   where to attach stuff.
     * @param request to get the user principal.
     * @return the user_sonnets page.
     */
    @GetMapping("/profile/my_sonnets")
    public String showUserSonnets(Model model, HttpServletRequest request) {
        model.addAttribute("username", request.getUserPrincipal().getName());
        model.addAttribute(PAGE_TITLE, "My Sonnets");

        return "user_sonnets";
    }

    /**
     * NOT IMPLEMENTED. ONLY A PLACEHOLDER.
     *
     * @param model to attach stuff to.
     * @return the user_classify page.
     */
    @GetMapping("/profile/classify")
    public String showVerifyPage(Model model) {
        model.addAttribute(PAGE_TITLE, "Classify Items");

        return "user_classify";
    }
}
