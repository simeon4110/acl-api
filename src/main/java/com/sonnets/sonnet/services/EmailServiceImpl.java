package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.web.ContactDto;
import com.sonnets.sonnet.persistence.models.web.MailingList;
import com.sonnets.sonnet.persistence.repositories.MailingListRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * Boilerplate email service.
 *
 * @author Josh Harkema
 */
@Component
public class EmailServiceImpl implements EmailService {
    private static final Logger LOGGER = Logger.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;
    private static final String EMAIL_TO = "josh@joshharkema.com";
    private static final String EMAIL_SUBJECT = "Sonnet Database Query";
    private final MailingListRepository mailingListRepository;

    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender, MailingListRepository mailingListRepository) {
        this.mailSender = mailSender;
        this.mailingListRepository = mailingListRepository;
    }

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
        } catch (MailException e) {
            LOGGER.error(e);
        }
    }

    @Override
    public void sendSimpleMessageUsingTemplate(String to, String subject, SimpleMailMessage template,
                                               String... templateArgs) {
        if (template.getText() != null) {
            @SuppressWarnings("ConfusingArgumentToVarargsMethod")
            String text = String.format(template.getText(), templateArgs);
            sendSimpleMessage(to, subject, text);
        }
    }

    @Override
    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // pass 'true' to the constructor to create a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Invoice", file);

            mailSender.send(message);
        } catch (MessagingException e) {
            LOGGER.error(e);
        }
    }

    public ResponseEntity<Void> sendAboutMessage(ContactDto contactDto) {
        try {
            String message = "\nMessage from: " + contactDto.getName() +
                    "\nEmail address: " + contactDto.getEmail() +
                    "\n\nAdd to mailing list: " + contactDto.isMailingList() +
                    "\n\nMessage:\n " + contactDto.getMessage();

            // Add user to mailing list if required.
            if (contactDto.isMailingList()) {
                mailingListRepository.saveAndFlush(new MailingList(contactDto.getName(), contactDto.getEmail()));
            }

            this.sendSimpleMessage(EMAIL_TO, EMAIL_SUBJECT, message);

            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (MailException e) {
            LOGGER.error(e);

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
