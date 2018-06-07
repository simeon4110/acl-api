package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.user.UserAddDto;
import com.sonnets.sonnet.persistence.dtos.user.UserModifyDto;
import com.sonnets.sonnet.security.UserDetailsServiceImpl;
import com.sonnets.sonnet.services.SonnetDetailsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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

    private static final String PAGE_TITLE = "pageTitle";
    private static final String ADD = "admin_add";
    private static final String MODIFY = "admin_modify";
    private static final String ALL_SONNETS = "admin_all";
    private final SonnetDetailsService sonnetDetailsService;

    @Autowired
    public AdminController(UserDetailsServiceImpl userDetailsService, SonnetDetailsService sonnetDetailsService) {
        this.userDetailsService = userDetailsService;
        this.sonnetDetailsService = sonnetDetailsService;
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
        model.addAttribute(PAGE_TITLE, "Administration");

        return ADD;
    }

    /**
     * Deals with new user form POSTs. Passes everything to UserDetailsService which returns the proper redirect.
     *
     * @param userAddDto the filled out UserAddDto.
     * @param model      to attach a fresh UserAddDto to.
     * @return a redirect to admin?success or admin?error depending on result.
     */
    @PostMapping("/admin/user/add")
    public String postAddUser(@ModelAttribute @Valid UserAddDto userAddDto, Model model) {
        logger.debug("Adding new user with username: " + userAddDto.getUsername());
        model.addAttribute("UserAddDto", new UserAddDto());

        return userDetailsService.addUser(userAddDto);
    }

    /**
     * Show the modify user page.
     *
     * @param model the model to add the user data to.
     * @return the user modify page.
     */
    @GetMapping("/admin/user/modify")
    public String showUserDeletePage(Model model) {
        model.addAttribute("UserModifyDto", new UserModifyDto());
        model.addAttribute("Users", userDetailsService.getAllUsers());
        model.addAttribute(PAGE_TITLE, "Modify User");

        return MODIFY;
    }

    /**
     * Handle inbound user modification POST requests.
     *
     * @param userModifyDto the form data.
     * @param model         the model to re-add user details to.
     * @return a success / error message from UserDetailsService.
     */
    @PostMapping("/admin/user/modify")
    public String postUserModify(@ModelAttribute @Valid UserModifyDto userModifyDto, Model model) {
        logger.debug("Modifying user with username: " + userModifyDto.getUsername());
        model.addAttribute("UserModifyDto", new UserModifyDto());
        model.addAttribute("Users", userDetailsService.getAllUsers());

        return userDetailsService.modifyUser(userModifyDto);
    }

    /**
     * Admin review of all sonnets in the db.
     *
     * @param model the model to attach all sonnets to.
     * @return the all review page.
     */
    @GetMapping("/admin/sonnets/all")
    public String showAllSonnetsPage(Model model) {
        model.addAttribute("Sonnets", sonnetDetailsService.getAllSonnets());
        model.addAttribute(PAGE_TITLE, "Review Records");

        return ALL_SONNETS;
    }

    /**
     * Allows admins to delete sonnets.
     *
     * @param model the model to reattach all the sonnets to.
     * @param id    the id of the sonnet ot delete.
     * @return a success / error message from sonnet details service.
     */
    @GetMapping("/admin/sonnets/all/delete/{id}")
    public String deleteSonnet(Model model, @PathVariable String id) {
        model.addAttribute("Sonnets", sonnetDetailsService.getAllSonnets());

        return sonnetDetailsService.deleteSonnetById(id);
    }

}
