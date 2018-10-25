package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.web.MessageDto;
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
    private final EmailServiceImpl emailService;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserDetailsServiceImpl userDetailsService,
                          EmailServiceImpl emailService) {
        this.messageRepository = messageRepository;
        this.userDetailsService = userDetailsService;
        this.emailService = emailService;
    }

    /**
     * Sends a message where the sender = admin.
     *
     * @param messageDto the dto with the data.
     */
    public void sendAdminMessage(MessageDto messageDto) {
        LOGGER.debug("Sending admin message: " + messageDto.toString());
        Message message = new Message();

        message.setUserFrom(messageDto.getUserFrom());
        message.setUserTo(messageDto.getUserTo());
        message.setSubject(messageDto.getSubject());
        message.setContent(messageDto.getContent());
        message.setRead(false);

        User user = userDetailsService.loadUserObjectByUsername(messageDto.getUserTo());
        emailService.sendSimpleMessage(
                user.getEmail(), "One of your poems has been rejected.", messageDto.getContent());

        messageRepository.saveAndFlush(message);
    }

    /**
     * A generic message sender.
     *
     * @param messageDto the message data.
     * @param principal  the user sending the message.
     * @return Ok if the message is sent.
     */
    public ResponseEntity<Void> sendMessage(MessageDto messageDto, Principal principal) {
        LOGGER.debug("Sending message: " + messageDto.toString());
        Message message = new Message();
        User userFrom = userDetailsService.loadUserObjectByUsername(messageDto.getUserFrom());
        User userTo = userDetailsService.loadUserObjectByUsername(messageDto.getUserTo());
        assert userFrom != null;
        assert userTo != null;
        assert principal.getName().equals(userFrom.getUsername());

        message.setUserFrom(userFrom.getUsername());
        message.setUserTo(userTo.getUsername());
        message.setSubject(messageDto.getSubject());
        message.setContent(messageDto.getContent());
        message.setRead(false);

        messageRepository.saveAndFlush(message);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Read a message.
     *
     * @param principal the user requesting the message.
     * @param id        the id of the message to mark read.
     * @return OK if the message is read, NOT_FOUND if the message doesn't exist or the userTo != the principal.
     */
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

    /**
     * Delete a message.
     *
     * @param principal the user deleting the message.
     * @param id        the id of the message to delete.
     * @return OK if the message is read, NOT_FOUND if the message doesn't exist or the userTo != the principal.
     */
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

    /**
     * Get a user's inbox.
     *
     * @param principal the user to get the inbox of.
     * @return the user's inbox as a List of Messages or null if the list is empty.
     */
    public List<Message> getInbox(Principal principal) {
        LOGGER.debug("Get inbox for user: " + principal.getName());
        return messageRepository.findAllByUserTo(principal.getName());
    }
}
