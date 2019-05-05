package com.sonnets.sonnet.controllers.base;

import com.sonnets.sonnet.SonnetApplication;
import com.sonnets.sonnet.helpers.JsonHelper;
import com.sonnets.sonnet.persistence.dtos.base.AuthorDto;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.services.base.AuthorService;
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
import org.springframework.test.web.servlet.ResultActions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the AuthorController.
 *
 * @author Josh Harkema
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SonnetApplication.class})
@AutoConfigureMockMvc
public class AuthorControllerIntegrationTest {
    private static final Logger LOGGER = Logger.getLogger(AuthorControllerIntegrationTest.class);
    private static final String FIRST_NAME = "test-first-name";
    private static final String LAST_NAME = "test-last-name";
    @Autowired
    private MockMvc mvc;
    @Autowired
    private AuthorService authorService;

    private static AuthorDto getTestDto() {
        AuthorDto dto = new AuthorDto();
        dto.setFirstName(FIRST_NAME);
        dto.setLastName(LAST_NAME);
        return dto;
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void add() throws Exception {
        AuthorDto dto = getTestDto();
        add(dto).andExpect(status().isOk());

        Author author = authorService.getByLastName(LAST_NAME);
        assertEquals(FIRST_NAME, author.getFirstName());
        assertEquals(LAST_NAME, author.getLastName());

        LOGGER.debug("add() result: " + author.toString());
    }

    @Test
    public void addNotAuthorized() throws Exception {
        // Should fail as not authorized.
        AuthorDto dto = getTestDto();
        add(dto).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void addDuplicate() throws Exception {
        // Should fail as duplicate
        AuthorDto dto = getTestDto();
        add(dto).andExpect(status().isOk());

        AuthorDto dto2 = getTestDto();
        add(dto2).andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void modify() throws Exception {
        // Create and add initial author.
        AuthorDto dto = getTestDto();
        add(dto).andExpect(status().isOk());

        // Retrieve the initial author.
        Author author = authorService.getByLastName(LAST_NAME);

        // Modify the initial author.
        AuthorDto modifyDto = new AuthorDto();
        modifyDto.setId(author.getId());
        modifyDto.setFirstName(author.getFirstName());
        modifyDto.setLastName(author.getLastName() + "-modified");

        // Preform the put request for modification.
        mvc.perform(put("/secure/author/modify")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(JsonHelper.toJson(modifyDto)))
                .andExpect(status().isOk());

        // Verify modification.
        Author authorResult = authorService.getByLastName(modifyDto.getLastName());
        assertEquals(modifyDto.getLastName(), authorResult.getLastName());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void modifyNotFound() throws Exception {
        AuthorDto dto = getTestDto();
        dto.setId(1L);

        mvc.perform(put("/secure/author/modify")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(JsonHelper.toJson(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void modifyNotAuthorized() throws Exception {
        AuthorDto dto = getTestDto();
        dto.setId(1L);

        mvc.perform(put("/secure/author/modify")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(JsonHelper.toJson(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {"USER", "ADMIN"})
    public void deleteAuthor() throws Exception {
        // Create an author for return data.
        AuthorDto dto = getTestDto();
        add(dto).andExpect(status().isOk());

        // Get the author object and delete by id.
        Author author = authorService.getByLastName(dto.getLastName());
        mvc.perform(delete("/secure/author/delete/" + author.getId()))
                .andExpect(status().isOk());

        // Ensure author has been deleted.
        assertNull(authorService.getByLastName(dto.getLastName()));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void deleteAuthorUserNotAuthorized() throws Exception {
        // Create an author for return data.
        AuthorDto dto = getTestDto();
        add(dto).andExpect(status().isOk());

        // Get the author object and delete by id.
        Author author = authorService.getByLastName(dto.getLastName());
        mvc.perform(delete("/secure/author/delete/" + author.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER", "ADMIN"})
    public void deleteAuthorNotFound() throws Exception {
        mvc.perform(delete("/secure/author/delete/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void getById() throws Exception {
        // Create an author for return data.
        AuthorDto dto = getTestDto();
        add(dto).andExpect(status().isOk());

        // Get the author object and get by id.
        Author author = authorService.getByLastName(dto.getLastName());
        MvcResult result = mvc.perform(get("/author/get_by_id/" + author.getId()))
                .andExpect(status().isOk())
                .andReturn();

        Author authorResult = JsonHelper.fromJsonResult(result, Author.class);
        assertEquals(author.getId(), authorResult.getId());
        assertEquals(author.getFirstName(), authorResult.getFirstName());
        assertEquals(author.getLastName(), authorResult.getLastName());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void getByIdNotFound() throws Exception {
        // Run a get request on a bad ID and ensure the response is empty.
        mvc.perform(get("/author/get_by_id/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void getByLastName() throws Exception {
        // Create an author for return data.
        AuthorDto author = getTestDto();
        add(author).andExpect(status().isOk());

        // Run mock get request.
        MvcResult result = mvc.perform(get("/author/get_by_last_name/" + LAST_NAME))
                .andExpect(status().isOk())
                .andReturn();

        // Verify response data.
        Author authorResult = JsonHelper.fromJsonResult(result, Author.class);
        assertEquals(FIRST_NAME, authorResult.getFirstName());
        assertEquals(LAST_NAME, authorResult.getLastName());

        LOGGER.debug("getByLastName() result: " + authorResult.toString());
    }

    private ResultActions add(final AuthorDto authorDto) throws Exception {
        return mvc.perform(post("/secure/author/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(JsonHelper.toJson(authorDto)));
    }
}