package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.UserAddDto;
import com.sonnets.sonnet.security.UserDetailsServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

/**
 * View controller for admin pages.
 *
 * @author Josh Harkema
 */
@Controller
public class AdminController {
    private static final Logger logger = Logger.getLogger(AdminController.class);
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AdminController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Show the add new user page.
     *
     * @param model to attach the UserAddDto object to.
     * @return the admin page.
     */
    @GetMapping("/admin")
    public String showAdminPage(Model model) {
        model.addAttribute("UserAddDto", new UserAddDto());

        return "admin";
    }

    /**
     * Deals with new user form POSTs. Passes everything to UserDetailsService which returns the proper redirect.
     *
     * @param userAddDto the filled out UserAddDto.
     * @param model      to attach a fresh UserAddDto to.
     * @return a redirect to admin?success or admin?error depending on result.
     */
    @PostMapping("/admin/add")
    public String postAddUser(@ModelAttribute @Valid UserAddDto userAddDto, Model model) {
        logger.debug("Adding new user with username: " + userAddDto.getUsername());
        model.addAttribute("UserAddDto", new UserAddDto());

        return userDetailsService.addUser(userAddDto);
    }

}
