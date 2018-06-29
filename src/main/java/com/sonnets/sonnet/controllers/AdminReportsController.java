package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.services.SonnetDetailsService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class AdminReportsController {
    private static final Logger LOGGER = Logger.getLogger(AdminReportsController.class);
    private final SonnetDetailsService sonnetDetailsService;

    @Autowired
    public AdminReportsController(SonnetDetailsService sonnetDetailsService) {
        this.sonnetDetailsService = sonnetDetailsService;
    }

    @GetMapping(value = "/admin/reports/user_and_data", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getUserReportByDate(@RequestParam String userName,
                                    @RequestParam Date after,
                                    @RequestParam Date before) {
        LOGGER.debug("Returning user report for user " + userName + " after " + after + " before " + before);

        return sonnetDetailsService.getSonnetsByAddedByAndDate(userName, after, before);
    }
}
