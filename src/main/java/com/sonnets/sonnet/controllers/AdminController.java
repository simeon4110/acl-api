package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.services.SonnetDetailsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * View controller for admin pages.
 *
 * @author Josh Harkema
 */
@Controller
public class AdminController {
    private static final Logger LOGGER = Logger.getLogger(AdminController.class);

    private static final String PAGE_TITLE_CONST = "pageTitle";
    private static final String PAGE_TITLE_VALUE = "Administration";
    private static final String USERS = "admin_users";
    private static final String USER_ADD = "admin_user_add";
    private static final String ALL_SONNETS = "admin_sonnets";

    private final SonnetDetailsService sonnetDetailsService;

    @Autowired
    public AdminController(SonnetDetailsService sonnetDetailsService) {
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
        LOGGER.debug("Showing admin page.");
        model.addAttribute(PAGE_TITLE_CONST, PAGE_TITLE_VALUE);

        return USERS;
    }

    @GetMapping("/admin/add")
    public String showUserAddPage(Model model) {
        LOGGER.debug("Showing user add page.");
        model.addAttribute(PAGE_TITLE_CONST, "New User");

        return USER_ADD;
    }

    /**
     * Admin review of all sonnets in the db.
     *
     * @param model the model to attach all sonnets to.
     * @return the all review page.
     */
    @GetMapping("/admin/sonnets/all")
    public String showAllSonnetsPage(Model model) {
        LOGGER.debug("Showing all sonnets page.");
        model.addAttribute("Sonnets", sonnetDetailsService.getAllSonnets());
        model.addAttribute(PAGE_TITLE_CONST, PAGE_TITLE_VALUE);

        return ALL_SONNETS;
    }

}
