package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.web.ContactDto;
import com.sonnets.sonnet.services.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Handles all email message sending REST endpoints.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class EmailController {
    private final EmailServiceImpl emailService;

    @Autowired
    public EmailController(EmailServiceImpl emailService) {
        this.emailService = emailService;
    }

    /**
     * Handles inbound POST requests from the 'About' form.
     *
     * @param contactDto a valid contactDto with.
     * @return success / failure response entity.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PostMapping(value = "/about/send_message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> aboutEmailHandler(@RequestBody @Valid ContactDto contactDto) {
        return emailService.sendAboutMessage(contactDto);
    }

}
