package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.security.UserDetailsServiceImpl;
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
    private static final String USERS = "admin_users";
    private static final String USER_ADD = "admin_user_add";
    private static final String ALL_SONNETS = "admin_sonnets";
    private static final String USER_REPORTS = "admin_user_reports";

    private final SonnetDetailsService sonnetDetailsService;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public AdminController(SonnetDetailsService sonnetDetailsService, UserDetailsServiceImpl userDetailsService) {
        this.sonnetDetailsService = sonnetDetailsService;
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
        LOGGER.debug("Showing admin page.");
        model.addAttribute(PAGE_TITLE_CONST, "User Management");

        return USERS;
    }

    /**
     * Shows HTML for the Admin panel's "New User" tab.
     *
     * @param model to attach stuff to.
     * @return the user_add page.
     */
    @GetMapping("/admin/add")
    public String showUserAddPage(Model model) {
        LOGGER.debug("Showing user add page.");
        model.addAttribute(PAGE_TITLE_CONST, "New User");

        return USER_ADD;
    }

    /**
     * Shows HTML for the Admin panel's "User Reports" tab.
     *
     * @param model to attach stuff to.
     * @return the user_reports page.
     */
    @GetMapping("/admin/reports")
    public String showReportsPage(Model model) {
        LOGGER.debug("Showing admin reports page.");
        model.addAttribute(PAGE_TITLE_CONST, "Reports");
        model.addAttribute("Users", userDetailsService.getAllUsers());

        return USER_REPORTS;
    }

    /**
     * Admin review of all sonnets in the db.
     *
     * @param model the model to attach all sonnets to.
     * @return the all review page.
     */
    @GetMapping("/admin/sonnets")
    public String showAllSonnetsPage(Model model) {
        LOGGER.debug("Showing all sonnets page.");
        model.addAttribute("Sonnets", sonnetDetailsService.getAllSonnets());
        model.addAttribute(PAGE_TITLE_CONST, "All Sonnets");

        return ALL_SONNETS;
    }

}
