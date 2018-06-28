package com.sonnets.sonnet.services;

import org.springframework.mail.SimpleMailMessage;

/**
 * @author Josh Harkema
 */
public interface EmailService {
    void sendSimpleMessage(String to, String subject, String text);

    void sendSimpleMessageUsingTemplate(String to, String subject, SimpleMailMessage template, String... templateArgs);

    void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment);
}
