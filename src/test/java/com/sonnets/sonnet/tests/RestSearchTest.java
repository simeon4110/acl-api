package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.config.TestJpaConfig;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestJpaConfig.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class RestSearchTest {
    private static final Logger LOGGER = Logger.getLogger(RestSearchTest.class);

    @Autowired
    private WebApplicationContext ctx;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    @Test
    public void testSearchEndpoint() throws Exception {
        // Known search with known results.
        String firstName = "William";
        String lastName = "Shakespeare";
        String title = "SONNET";
        String period = "1600-1650";
        String text = "Beloved";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/sonnets/search")
                .param("firstName", firstName).param("lastName", lastName)
                .param("title", title).param("period", period).param("text", text))
                .andExpect(status().isOk()).andReturn();

        LOGGER.debug("JSON Result: " + result.getResponse());
    }
}
