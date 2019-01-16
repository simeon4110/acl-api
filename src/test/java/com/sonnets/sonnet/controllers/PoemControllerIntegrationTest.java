package com.sonnets.sonnet.controllers;

import com.google.gson.Gson;
import com.sonnets.sonnet.constants.TestConstants;
import com.sonnets.sonnet.persistence.dtos.poetry.PoemDto;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.services.poem.PoemService;
import generators.PoemGenerator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * All test cases for the PoemController are here.
 *
 * @author Josh Harkema
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        properties = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class PoemControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private PoemService poemService;
    private Gson gson = new Gson();

    @BeforeClass
    public static void setup() {

    }

    @Test
    public void add() throws Exception {

    }

    @Test
    public void getPoemById() throws Exception {
        Poem testPoem = poemService.getById(1L);
        mvc.perform(get(TestConstants.BASE_URL.getStringValue() + "poems/by_id/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TestConstants.JSON_ID.getStringValue(), is(testPoem.getId().intValue())))
                .andExpect(jsonPath(TestConstants.JSON_CATEGORY.getStringValue(), is(TypeConstants.POEM)))
                .andExpect(jsonPath(TestConstants.JSON_AUTHOR_ID.getStringValue(), is(testPoem.getAuthor().getId().intValue())))
                .andExpect(jsonPath(TestConstants.JSON_AUTHOR_FIRST_NAME.getStringValue(), is(testPoem.getAuthor().getFirstName())))
                .andExpect(jsonPath(TestConstants.JSON_AUTHOR_LAST_NAME.getStringValue(), is(testPoem.getAuthor().getLastName())))
                .andExpect(jsonPath(TestConstants.JSON_TITLE.getStringValue(), is(testPoem.getTitle())))
                .andExpect(jsonPath(TestConstants.JSON_PUBLICATION_YEAR.getStringValue(), is(testPoem.getDateOfPublication())))
                .andExpect(jsonPath(TestConstants.JSON_PERIOD.getStringValue(), is(testPoem.getPeriod())))
                .andExpect(jsonPath(TestConstants.JSON_POEM_FORM.getStringValue(), is(testPoem.getForm())))
                .andExpect(jsonPath(TestConstants.JSON_TEXT.getStringValue(), is(testPoem.getText())));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void getUserPoems_userHasNoPoems() throws Exception {
        mvc.perform(get(TestConstants.BASE_URL.getStringValue() + "secure/poem/get_user_poems")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void getUserPoems_userHasPoems() throws Exception {
        PoemDto testPoemDto = new PoemDto();
        PoemGenerator.generateDto(testPoemDto);
        testPoemDto.setAuthorId("1");

        PoemDto testPoemDto2 = new PoemDto();
        PoemGenerator.generateDto(testPoemDto2);
        testPoemDto2.setAuthorId("1");

        mvc.perform(post(TestConstants.BASE_URL.getStringValue() + "secure/poem/add")
                .content(gson.toJson(testPoemDto))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        mvc.perform(post(TestConstants.BASE_URL.getStringValue() + "secure/poem/add")
                .content(gson.toJson(testPoemDto2))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        mvc.perform(get(TestConstants.BASE_URL.getStringValue() + "secure/poem/get_user_poems")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(authorities = {"USER", "ADMIN"})
    public void addPoem_goodAuth() throws Exception {
        PoemDto testPoemDto = new PoemDto();
        PoemGenerator.generateDto(testPoemDto);
        testPoemDto.setAuthorId("1");

        mvc.perform(post(TestConstants.BASE_URL.getStringValue() + "secure/poem/add")
                .content(gson.toJson(testPoemDto))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    public void addPoem_badAuth() throws Exception {
        PoemDto testPoemDto = new PoemDto();
        PoemGenerator.generateDto(testPoemDto);

        mvc.perform(post(TestConstants.BASE_URL.getStringValue() + "secure/poem/add")
                .content(gson.toJson(testPoemDto))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isForbidden());
    }

    @Test
    public void editPoemAdmin() {
    }

    @Test
    public void editPoem() {
    }

    @Test
    public void deletePoemAdmin() {
    }

    @Test
    public void deletePoem() {
    }

    @Test
    public void confirmPoem() {
    }

    @Test
    public void rejectPoem() {
    }

    @Test
    public void getPoemToConfirm() {
    }

    @Test
    public void getByIdText() {
    }

    @Test
    public void getByIdXML() {
    }

    @Test
    public void getByIdTEI() {
    }

    @Test
    public void getByIdCSV() {
    }
}
