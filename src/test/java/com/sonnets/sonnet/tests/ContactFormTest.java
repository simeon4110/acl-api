package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.config.TestJpaConfig;
import com.sonnets.sonnet.persistence.models.MailingList;
import com.sonnets.sonnet.persistence.repositories.MailingListRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the contact form POST handler and mail sender.
 *
 * @author Josh Harkema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
public class ContactFormTest {
    @Autowired
    private WebApplicationContext ctx;
    @Autowired
    private MailingListRepository mailingListRepository;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    @Test
    public void testContactForm() throws Exception {
        String name = "JUnit Test";
        String email = "test_user@test.com";
        String message = "This is a test of the contact form system.";

        mockMvc.perform(MockMvcRequestBuilders.post("/about/contact").param("name", name)
                .param("email", email).param("message", message).param("mailingList", "true"))
                .andExpect(status().isOk());
        MailingList list = mailingListRepository.findByEmail(email);

        Assert.assertEquals(email, list.getEmail());
    }
}
