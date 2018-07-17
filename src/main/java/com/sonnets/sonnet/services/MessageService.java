package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.MessageDto;
import com.sonnets.sonnet.persistence.models.MessageImpl;
import com.sonnets.sonnet.persistence.repositories.MessageRepository;
import com.sonnets.sonnet.security.UserDetailsServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

/**
 * Handles CRUD for messages.
 *
 * @author Josh Harkema
 */
@Service
public class MessageService {
    private static final Logger LOGGER = Logger.getLogger(MessageService.class);
    private final MessageRepository messageRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserDetailsServiceImpl userDetailsService) {
        this.messageRepository = messageRepository;
        this.userDetailsService = userDetailsService;
    }

    public List getMessagesTo(Principal principal) {
        LOGGER.debug("Getting all messages to: " + principal.getName());
        try {
            return messageRepository.findAllByToUser(principal.getName());
        } catch (Exception e) {
            LOGGER.error(e);
            return Collections.emptyList();
        }
    }

    public List getMessagesFrom(Principal principal) {
        LOGGER.debug("Getting all messages from: " + principal.getName());
        return messageRepository.findAllByFromUser(principal.getName());
    }

    public ResponseEntity<Void> sendMessage(Principal principal, MessageDto messageDto) {
        LOGGER.debug("Sending message from: " + principal.getName() + " to: " + messageDto.getTo());

        MessageImpl message = new MessageImpl();
        message.setFromUser(principal.getName());
        message.setToUser(messageDto.getTo());
        message.setIsRead(false);
        message.setMessageContent(messageDto.getMessage());
        messageRepository.saveAndFlush(message);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
