package org.acl.database.controllers.web;

import org.acl.database.persistence.dtos.web.MessageDto;
import org.acl.database.services.web.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
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
@PropertySource("classpath:global.properties")
public class MessagingController {
    private final MessageService messageService;

    @Autowired
    public MessagingController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * @return a user's inbox.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/secure/message/get_inbox", produces = MediaType.APPLICATION_JSON_VALUE)
    public List getMessageInbox(Principal principal) {
        return messageService.getInbox(principal);
    }

    /**
     * Delete a message by it's db id.
     *
     * @param id the id of the message to delete.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('USER')")
    @DeleteMapping(value = "/secure/message/delete_message/{id}")
    public ResponseEntity<Void> deleteMessage(Principal principal, @PathVariable("id") String id) {
        return messageService.deleteMessage(principal, Long.parseLong(id));
    }

    /**
     * Send a message.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping(value = "/secure/message/send_message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sendMessage(@RequestBody @Valid MessageDto messageDto, Principal principal) {
        return messageService.sendMessage(messageDto, principal);
    }

    /**
     * Set a message to read.
     *
     * @param id the id of the message to set.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('USER')")
    @GetMapping(value = "/secure/message/read_message/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> readMessage(@PathVariable("id") String id, Principal principal) {
        return messageService.readMessage(principal, Long.parseLong(id));
    }
}
