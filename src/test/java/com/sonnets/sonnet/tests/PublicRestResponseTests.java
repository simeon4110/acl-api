package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.controllers.PublicRestController;
import com.sonnets.sonnet.services.SearchService;
import com.sonnets.sonnet.services.SonnetDetailsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * MockMvc test for public REST endpoints.
 *
 * @author Josh Harkema
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@WebAppConfiguration
@ActiveProfiles("test")
public class PublicRestResponseTests {
    private static final String testIdStr = "9";
    private static final String lastName = "Shakespeare";
    private static final String firstName = "William";
    private static final String addedBy = "amdin";
    private static final String text = "love";
    private static final String title = "b2d06974";
    private static final String period = "1550-1600";

    @Autowired
    private WebApplicationContext ctx;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    /**
     * Tests to ensure all rest endpoints return valid file types.
     *
     * @throws Exception it's a test...
     */
    @Test
    public void testRestEndpoints() throws Exception {
        // JSON by_id
        mockMvc.perform(get("/sonnets/by_id/{id}", testIdStr).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        // JSON all
        mockMvc.perform(get("/sonnets/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        // JSON lastName
        mockMvc.perform(get("/sonnets/by_author_last_name/{lastName}", lastName).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        // JSON firstName
        mockMvc.perform(get("/sonnets/by_author_first_name/{firstName}", firstName).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        // JSON addedBy
        mockMvc.perform(get("/sonnets/by_added_by/{addedBy}", addedBy).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        // TXT all
        mockMvc.perform(get("/sonnets/txt/all").accept(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain"));

        // TXT id
        mockMvc.perform(get("/sonnets/txt/by_id/{ids}", testIdStr).accept(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain"));

        // TXT lastName
        mockMvc.perform(get("/sonnets/txt/by_last_name/{lastName}", lastName).accept(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain"));

        // TXT addedBy
        mockMvc.perform(get("/sonnets/txt/by_user/{username}", addedBy).accept(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain"));

        // XML id
        mockMvc.perform(get("/sonnets/xml/by_id/{id}", testIdStr).accept(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/xml"));

        // TEI id
        mockMvc.perform(get("/sonnets/tei/by_id/{id}", testIdStr).accept(MediaType.APPLICATION_XML_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/xml"));

        // CSV id
        mockMvc.perform(get("/sonnets/csv/by_ids/{ids}", testIdStr).accept(MediaType.TEXT_PLAIN_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain"));

        // Search text
        mockMvc.perform(get("/sonnets/search/text/{text}", text).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        // Search title
        mockMvc.perform(get("/sonnets/search/title/{title}", title).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        // Search period
        mockMvc.perform(get("/sonnets/search/period/{period}", period).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

    }

    @Configuration
    @EnableWebMvc
    @ComponentScan(basePackages = {"com.sonnets.sonnet"})
    @ActiveProfiles("test")
    public static class TestConfiguration {
        @Autowired
        private SonnetDetailsService sonnetDetailsService;
        @Autowired
        private SearchService searchService;

        @Bean
        public PublicRestController restControllerImpl() {
            return new PublicRestController(sonnetDetailsService, searchService);
        }
    }
}
