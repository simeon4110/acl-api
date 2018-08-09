package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.ContactDto;
import com.sonnets.sonnet.services.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class EmailController {
    private static final String ALLOWED_ORIGIN = "*";
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
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PostMapping(value = "/about/send_message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> aboutEmailHandler(@RequestBody @Valid ContactDto contactDto) {
        return emailService.sendAboutMessage(contactDto);
    }

}
