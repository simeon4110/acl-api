package org.acl.database.controllers.base;

import org.acl.database.SonnetApplication;
import org.acl.database.helpers.JsonHelper;
import org.acl.database.persistence.dtos.base.AuthorDto;
import org.acl.database.persistence.dtos.base.PoemDto;
import org.acl.database.persistence.models.base.Author;
import org.acl.database.persistence.models.base.Poem;
import org.acl.database.services.base.AuthorService;
import org.acl.database.services.base.PoemService;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for poem controller.
 *
 * @author Josh Harkema
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SonnetApplication.class})
@AutoConfigureMockMvc
public class PoemControllerIntegrationTest {
    private static final Logger LOGGER = Logger.getLogger(PoemControllerIntegrationTest.class);
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PoemService poemService;
    @Autowired
    private AuthorService authorService;

    private static PoemDto getTestDto(final String title) {
        PoemDto dto = new PoemDto();
        dto.setPlaceOfPublication("test_place");
        dto.setPublisher("test_publisher");
        dto.setAuthorId(1L);
        dto.setTitle(title);
        dto.setPublicDomain(false);
        dto.setPeriod("test_period");
        dto.setForm("test_sonnet");
        dto.setText("This is a test\nof the sonnet controller\nit is not valid data.");
        return dto;
    }

    /**
     * This method must always be first. It adds the author the other tests depend on.
     * This method also runs all the important 'get' tests in order.
     */
    @Test
    @WithMockUser(authorities = {"USER"})
    public void addPoem() throws Exception {
        // A new author must be added.
        AuthorDto authorDto = new AuthorDto();
        authorDto.setFirstName("poem_author");
        authorDto.setLastName("poem_author");
        authorService.add(authorDto);
        Author author = authorService.get(1L);

        String title = UUID.randomUUID().toString();
        addTestPoem(title);
        addTestPoem(UUID.randomUUID().toString()); // add a second poem.
        Poem poem = poemService.getById(1L);

        assertEquals(title, poem.getTitle());
        assertEquals(author.getFirstName(), poem.getAuthor().getFirstName());
        assertEquals(author.getLastName(), poem.getAuthor().getLastName());
        LOGGER.debug(String.format("Poem added. Title: %s", title));

        getPoemById();
        getPoemsByIds();
        getAllPoems();
        getAllPoemsAuthed();
        getAllPoemsByUser();
    }

    public void getPoemById() throws Exception {
        Poem poem = poemService.getById(1L);
        MvcResult result = mockMvc.perform(get("/poem/by_id/1"))
                .andExpect(status().isOk())
                .andReturn();

        Poem poemResult = JsonHelper.fromJsonResult(result, Poem.class);
        LOGGER.debug(poemResult);
        assertEquals(poem.getId(), poemResult.getId());
        assertEquals(poem.getTitle(), poemResult.getTitle());
    }

    public void getPoemsByIds() throws Exception {
        MvcResult result = mockMvc.perform(get("/poem/by_ids/1,2"))
                .andExpect(status().isOk())
                .andReturn();

        List poems = JsonHelper.fromJsonResult(result, List.class);
        LOGGER.debug(poems);
        assertEquals(2, poems.size());
    }

    public void getAllPoems() throws Exception {
        MvcResult result = mockMvc.perform(get("/poem/all"))
                .andExpect(status().isOk())
                .andReturn();

        List poems = JsonHelper.fromJsonResult(result, List.class);
        LOGGER.debug(poems);
        assertEquals(0, poems.size());
    }

    public void getAllPoemsAuthed() throws Exception {
        MvcResult result = mockMvc.perform(get("/secure/poem/all"))
                .andExpect(status().isOk())
                .andReturn();

        List poems = JsonHelper.fromJsonResult(result, List.class);
        LOGGER.debug(poems);
        assertEquals(2, poems.size());
    }

    public void getAllPoemsByUser() throws Exception {
        MvcResult result = mockMvc.perform(get("/secure/poem/all_user"))
                .andExpect(status().isOk())
                .andReturn();

        List poems = JsonHelper.fromJsonResult(result, List.class);
        LOGGER.debug(poems);
        assertEquals(2, poems.size());
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    public void deletePoem() throws Exception {
        mockMvc.perform(delete("/secure/poem/delete/1"))
                .andExpect(status().isOk());
        LOGGER.debug("Poem deleted.");
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void deletePoemBadAuth() throws Exception {
        addTestPoem(UUID.randomUUID().toString());

        mockMvc.perform(delete("/secure/poem/delete/2"))
                .andExpect(status().isForbidden());
        LOGGER.debug("Poem delete not authorized.");
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void deleteUserPoem() throws Exception {
        String title = UUID.randomUUID().toString();
        addTestPoem(title);
        // this is brittle af, but the tests must be run in sequence.
        mockMvc.perform(delete("/secure/poem/user_delete/2"))
                .andExpect(status().isOk());
    }

    private void addTestPoem(final String title) throws Exception {
        PoemDto dto = getTestDto(title);
        mockMvc.perform(post("/secure/poem/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(JsonHelper.toJson(dto))).andExpect(status().isOk());
    }
}