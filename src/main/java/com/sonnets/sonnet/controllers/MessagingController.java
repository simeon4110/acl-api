package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.MessageDto;
import com.sonnets.sonnet.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * All messaging endpoints are here. Method names are self explanatory.
 *
 * @author Josh Harkema
 */
@RestController
public class MessagingController {
    private static final String ALLOWED_ORIGIN = "*";
    private final MessageService messageService;

    @Autowired
    public MessagingController(MessageService messageService) {
        this.messageService = messageService;
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/secure/message/get_inbox", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getMessageInbox(Principal principal) {
        return messageService.getInbox(principal);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping(value = "/secure/message/delete_message/{id}")
    public ResponseEntity<Void> deleteMessage(Principal principal, @PathVariable("id") String id) {
        return messageService.deleteMessage(principal, Long.parseLong(id));
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping(value = "/secure/message/send_message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendMessage(@RequestBody @Valid MessageDto messageDto, Principal principal) {
        return messageService.sendMessage(messageDto, principal);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/secure/message/read_message/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> readMessage(@PathVariable("id") String id, Principal principal) {
        return messageService.readMessage(principal, Long.parseLong(id));
    }
}
