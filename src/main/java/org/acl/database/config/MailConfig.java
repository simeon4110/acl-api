package org.acl.database.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration for GMAIL smtp connection.
 *
 * @author Josh Harkema
 */
@SuppressWarnings("SpellCheckingInspection")
@Configuration
public class MailConfig {
    private static final String HOST = "smtp.gmail.com";
    private static final int PORT = 587;
    private static final String USER_NAME = "acriticismlab@gmail.com";
    private static final String PASS = "9mguEYJEz@TE54$c";

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(HOST);
        mailSender.setPort(PORT);

        mailSender.setUsername(USER_NAME);
        mailSender.setPassword(PASS);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
