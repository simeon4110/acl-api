package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.config.TestJpaConfig;
import com.sonnets.sonnet.persistence.models.Sonnet;
import com.sonnets.sonnet.services.SonnetDetailsService;
import org.apache.log4j.Logger;
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

import java.security.Principal;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests POST to /insert endpoint.
 *
 * @author Josh Harkema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
public class SonnetInsertTest {
    private static final Logger LOGGER = Logger.getLogger(SonnetInsertTest.class);
    @Autowired
    private WebApplicationContext ctx;
    @Autowired
    private SonnetDetailsService sonnetDetailsService;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    @Test
    public void testSonnetPost() throws Exception {
        String title = UUID.randomUUID().toString();
        String lastName = UUID.randomUUID().toString();
        String text = "First and only Line";

        // Create a testing principal for authenticated input requests.
        Principal testPrincipal = () -> "test_user";

        // Add a sonnet.
        mockMvc.perform(MockMvcRequestBuilders.post("/insert")
                .param("firstName", "testFirstName").param("lastName", lastName)
                .param("title", title).param("period", "1500-1550")
                .param("publicationYear", "1505").param("publicationStmt", "testStmt")
                .param("sourceDesc", "testDesc").param("addedBy", "testAdded")
                .param("text", text).principal(testPrincipal))
                .andExpect(status().isOk());

        Sonnet sonnet = sonnetDetailsService.getSonnetByTitleAndLastName(title, lastName);

        // Verify addition with db query for added sonnet.
        LOGGER.debug("Sonnet Output: " + sonnet.toString());
        Assert.assertEquals(title, sonnet.getTitle());
        Assert.assertEquals(lastName, sonnet.getLastName());

        // Add another sonnet with a different last name.
        String nextLastName = UUID.randomUUID().toString();
        mockMvc.perform(MockMvcRequestBuilders.post("/insert")
                .param("firstName", "testFirstName").param("lastName", nextLastName)
                .param("title", "").param("period", "1500-1550")
                .param("publicationYear", "1505").param("publicationStmt", "testStmt")
                .param("sourceDesc", "testDesc").param("addedBy", "testAdded")
                .param("text", text).principal(testPrincipal))
                .andExpect(status().isOk());

        Sonnet sonnet1 = sonnetDetailsService.getSonnetByTitleAndLastName(text, nextLastName);

        // Verify next sonnet with db query for added sonnet.
        LOGGER.debug("No title sonnet output: " + sonnet1.toString());
        Assert.assertEquals(text, sonnet1.getTitle());
        Assert.assertEquals(nextLastName, sonnet1.getLastName());

    }

}

