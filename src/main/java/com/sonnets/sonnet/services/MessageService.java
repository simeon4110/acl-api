package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.MessageDto;
import com.sonnets.sonnet.persistence.models.web.Message;
import com.sonnets.sonnet.persistence.models.web.User;
import com.sonnets.sonnet.persistence.repositories.MessageRepository;
import com.sonnets.sonnet.security.UserDetailsServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    void sendAdminMessage(MessageDto messageDto) {
        LOGGER.debug("Sending admin message: " + messageDto.toString());
        Message message = new Message();

        message.setUserFrom(messageDto.getUserFrom());
        message.setUserTo(messageDto.getUserTo());
        message.setSubject(messageDto.getSubject());
        message.setContent(messageDto.getContent());
        message.setRead(false);

        messageRepository.saveAndFlush(message);
    }

    public ResponseEntity<Void> sendMessage(MessageDto messageDto, Principal principal) {
        LOGGER.debug("Sending message: " + messageDto.toString());
        Message message = new Message();
        User userFrom = userDetailsService.loadUserObjectByUsername(messageDto.getUserFrom());
        User userTo = userDetailsService.loadUserObjectByUsername(messageDto.getUserTo());

        if (userFrom == null) {
            throw new NullPointerException("User: '" + messageDto.getUserFrom() + "' does not exist.");
        }

        if (userTo == null) {
            throw new NullPointerException("User: '" + messageDto.getUserTo() + "' does not exist.");
        }

        if (!Objects.equals(principal.getName(), userFrom.getUsername())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        message.setUserFrom(userFrom.getUsername());
        message.setUserTo(userTo.getUsername());
        message.setSubject(messageDto.getSubject());
        message.setContent(messageDto.getContent());
        message.setRead(false);

        messageRepository.saveAndFlush(message);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> readMessage(Principal principal, Long id) {
        LOGGER.debug("Setting message read: " + id);
        Optional<Message> message = messageRepository.findById(id);
        if (message.isPresent() && Objects.equals(message.get().getUserTo(), principal.getName())) {
            Message messageObj = message.get();
            messageObj.setRead(true);
            messageRepository.saveAndFlush(messageObj);

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            LOGGER.error("Either message does not exists, or request came from bad user.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<Void> deleteMessage(Principal principal, Long id) {
        LOGGER.debug("Deleting message: " + id);
        Optional<Message> message = messageRepository.findById(id);
        if (message.isPresent() && Objects.equals(message.get().getUserTo(), principal.getName())) {
            Message messageObj = message.get();
            messageRepository.delete(messageObj);

            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            LOGGER.error("Either message does not exists, or request came from bad user.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public List<Message> getInbox(Principal principal) {
        LOGGER.debug("Get inbox for user: " + principal.getName());
        return messageRepository.findAllByUserTo(principal.getName());
    }

}
