package com.sonnets.sonnet.tests;

import com.sonnets.sonnet.controllers.RestControllerImpl;
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
    @Autowired
    private WebApplicationContext ctx;
    private MockMvc mockMvc;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx).build();
    }

    /**
     * Tests to ensure all rest endpoints return valid files.
     *
     * @throws Exception it's a test...
     */
    @Test
    public void testRestEndpoints() throws Exception {
        mockMvc.perform(get("/sonnets/by_id/{id}", testIdStr).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        mockMvc.perform(get("/sonnets/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        mockMvc.perform(get("/sonnets/by_author_last_name/{lastName}", lastName).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"));

        mockMvc.perform(get("/sonnets/by_author_first_name/{firstName}", firstName).accept(MediaType.APPLICATION_JSON))
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

        @Bean
        public RestControllerImpl restControllerImpl() {
            return new RestControllerImpl(sonnetDetailsService);
        }
    }
}