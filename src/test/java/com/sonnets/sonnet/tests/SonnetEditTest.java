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
 * Tests POST to /edit endpoint.
 *
 * @author Josh Harkema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
public class SonnetEditTest {
    private static final Logger logger = Logger.getLogger(SonnetEditTest.class);
    private static final String SONNET_ID = "9"; // It this sonnet no longer exists, the test will fail.
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
    public void testEditPost() throws Exception {
        Sonnet sonnet = sonnetDetailsService.getSonnetByID(SONNET_ID);
        String newTitle = UUID.randomUUID().toString();

        Principal testPrincipal = () -> "test_user";

        mockMvc.perform(MockMvcRequestBuilders.post("/edit/" + SONNET_ID)
                .param("id", sonnet.getId().toString()).param("firstName", sonnet.getFirstName())
                .param("lastName", sonnet.getLastName()).param("title", newTitle)
                .param("period", "1500-1550")
                .param("publicationYear", sonnet.getPublicationYear().toString())
                .param("sourceDesc", sonnet.getSourceDesc()).param("addedBy", sonnet.getAddedBy())
                .param("text", sonnet.getTextPretty()).principal(testPrincipal))
                .andExpect(status().isOk());

        Sonnet editedSonnet = sonnetDetailsService.getSonnetByID(SONNET_ID);

        logger.debug("Sonnet Output: " + editedSonnet.toString());
        Assert.assertEquals(newTitle, editedSonnet.getTitle());
    }

}
