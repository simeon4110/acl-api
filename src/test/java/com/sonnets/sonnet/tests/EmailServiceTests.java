package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.config.TestJpaConfig;
import com.sonnets.sonnet.services.EmailServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Tests basic email functionality. This test is manual, you must verify email receipt.
 *
 * @author Josh Harkema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class EmailServiceTests {
    @Autowired
    private EmailServiceImpl emailService;

    @Test
    public void testSimpleMessage() {
        String to = "josh@joshharkema.com";
        String subject = "Test Message";
        String text = "This is a test message from the ACL sonnet database.";

        emailService.sendSimpleMessage(to, subject, text);
    }
}
