package com.sonnets.sonnet.tests;

import com.google.gson.Gson;
import com.sonnets.sonnet.SonnetApplication;
import com.sonnets.sonnet.persistence.dtos.AuthorDto;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.services.AuthorService;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * To save effort, rather than writing individual tests I have simply chained all the tests into one long sequence.
 * The methods are for readability and serve no real function.
 *
 * @author Josh Harkema
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = SonnetApplication.class)
@Transactional
@ActiveProfiles("mvc")
public class AuthorIntegrationTest {
    private static final Logger LOGGER = Logger.getLogger(AuthorIntegrationTest.class);
    private static final String CLIENT_ID = "databaseAuthentication";
    private static final String CLIENT_SECRET = "";
    private static final String USERNAME = "jharkema";
    private static final String PASSWORD = "ToyCar11!";
    private static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    private static final String TEST_FIRST_NAME = "test.author.first";
    private static final String TEST_LAST_NAME = "test.author.last";
    @Autowired
    private AuthorService authorService;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private FilterChainProxy filterChainProxy;
    private MockMvc mockMvc;
    private Gson gson = new Gson();
    private JacksonJsonParser jsonParser = new JacksonJsonParser();
    private String token;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilter(filterChainProxy).build();
    }

    @Test
    public void addAuthorAndStartSequentialTests() throws Exception {
        LOGGER.debug("Adding new author for tests.");
        this.token = obtainAccessToken();
        AuthorDto dto = new AuthorDto();
        dto.setFirstName(TEST_FIRST_NAME);
        dto.setLastName(TEST_LAST_NAME);
        String authorString = gson.toJson(dto);

        mockMvc.perform(post("/secure/author/add").header("Authorization", "Bearer " + token)
                .contentType(CONTENT_TYPE).content(authorString).accept(CONTENT_TYPE)).andExpect(status().isOk());

        Author author = authorService.getByLastName(dto.getLastName());
        Assert.assertNotNull(author);
        Assert.assertEquals(TEST_FIRST_NAME, author.getFirstName());
        Assert.assertEquals(TEST_LAST_NAME, author.getLastName());

        addAuthorWithSameName(author);
    }

    public void addAuthorWithSameName(Author author) throws Exception {
        LOGGER.debug("Attempting to add duplicate author.");

        AuthorDto dto = new AuthorDto();
        dto.setFirstName(TEST_FIRST_NAME);
        dto.setLastName(TEST_LAST_NAME);
        String authorString = gson.toJson(dto);

        mockMvc.perform(post("/secure/author/add").header("Authorization", "Bearer " + token)
                .contentType(CONTENT_TYPE).content(authorString).accept(CONTENT_TYPE))
                .andExpect(status().isExpectationFailed());

        modifyAuthor(author);
    }

    public void modifyAuthor(Author author) throws Exception {
        AuthorDto modifyDto = new AuthorDto();
        modifyDto.setId(author.getId());
        modifyDto.setFirstName(TEST_FIRST_NAME + ".modified");
        modifyDto.setLastName(TEST_LAST_NAME + ".modified");
        String modifyAuthorString = gson.toJson(modifyDto);

        LOGGER.debug("modifyAuthor() modifying test author.");
        mockMvc.perform(post("/secure/author/modify").header("Authorization", "Bearer " + token)
                .contentType(CONTENT_TYPE).content(modifyAuthorString).accept(CONTENT_TYPE)).andExpect(status().isOk());

        Author modifyAuthor = authorService.getByLastName(modifyDto.getLastName());
        Assert.assertNotNull(modifyAuthor);
        Assert.assertEquals(modifyDto.getFirstName(), modifyAuthor.getFirstName());
        Assert.assertEquals(modifyDto.getLastName(), modifyAuthor.getLastName());

        getAuthorById(author);
    }

    public void getAuthorById(Author author) throws Exception {
        LOGGER.debug("Getting author with ID: " + author.getId());
        ResultActions result = mockMvc.perform(get("/author/get_by_id/" + author.getId())
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE));

        String resultString = result.andReturn().getResponse().getContentAsString();
        String firstName = jsonParser.parseMap(resultString).get("firstName").toString();
        String lastName = jsonParser.parseMap(resultString).get("lastName").toString();
        Assert.assertEquals(author.getFirstName(), firstName);
        Assert.assertEquals(author.getLastName(), lastName);

        getAuthorByLastName(author);
    }

    public void getAuthorByLastName(Author author) throws Exception {
        LOGGER.debug("Getting author with lastName: " + author.getLastName());

        ResultActions result = mockMvc.perform(get("/author/get_by_last_name/" + author.getLastName())
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE));

        String resultString = result.andReturn().getResponse().getContentAsString();
        String firstName = jsonParser.parseMap(resultString).get("firstName").toString();
        String lastName = jsonParser.parseMap(resultString).get("lastName").toString();
        Assert.assertEquals(author.getFirstName(), firstName);
        Assert.assertEquals(author.getLastName(), lastName);

        deleteAuthor(author);
    }

    public void deleteAuthor(Author author) throws Exception {
        LOGGER.debug("Deleteing author with ID: " + author.getId());

        mockMvc.perform(delete("/secure/author/delete/" + author.getId())
                .header("Authorization", "Bearer " + token).accept(CONTENT_TYPE))
                .andExpect(status().isOk());

        Assert.assertNull(authorService.get(author.getId().toString()));

        LOGGER.debug("Yahoo! Test sequence complete!");
    }

    private String obtainAccessToken() throws Exception {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", CLIENT_ID);
        params.add("username", USERNAME);
        params.add("password", PASSWORD);

        // @formatter:off

        ResultActions result = mockMvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic(CLIENT_ID, CLIENT_SECRET))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(CONTENT_TYPE));

        // @formatter:on

        String resultString = result.andReturn().getResponse().getContentAsString();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }

}
