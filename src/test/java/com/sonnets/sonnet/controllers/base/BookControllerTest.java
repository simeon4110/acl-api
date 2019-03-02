package com.sonnets.sonnet.controllers.base;

import com.sonnets.sonnet.helpers.JsonHelper;
import com.sonnets.sonnet.persistence.dtos.base.AuthorDto;
import com.sonnets.sonnet.persistence.dtos.prose.BookDto;
import com.sonnets.sonnet.persistence.models.base.Book;
import com.sonnets.sonnet.services.base.BookService;
import org.apache.log4j.Logger;
import org.junit.Before;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the com.sonnets.sonnet.controllers.base.BookController.
 *
 * @author Josh Harkema
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = {"spring.profiles.active=test"})
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class BookControllerTest {
    private static final Logger LOGGER = Logger.getLogger(BookControllerTest.class);
    private static final String BOOK_TITLE = "test-title";
    private static final String BOOK_PERIOD = "test-period";
    private static final String BOOK_TYPE = "test-type";
    private static final String BOOK_PLACE_OF_PUB = "test-place";
    private static final String BOOK_PUBLISHER = "test-publisher";
    @Autowired
    private MockMvc mvc;
    @Autowired
    private BookService bookService;

    private static BookDto getBookDto() {
        BookDto dto = new BookDto();
        dto.setAuthorId(1L);
        dto.setTitle(BOOK_TITLE);
        dto.setPeriod(BOOK_PERIOD);
        dto.setType(BOOK_TYPE);
        dto.setPlaceOfPublication(BOOK_PLACE_OF_PUB);
        dto.setPublisher(BOOK_PUBLISHER);
        return dto;
    }

    @Before
    public void setUp() throws Exception {
        // Add an author for all book tests (books need authors).
        AuthorDto dto = new AuthorDto();
        dto.setFirstName("test-author-first");
        dto.setLastName("test-author-last");
        mvc.perform(post("/secure/author/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(JsonHelper.toJson(dto)));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void addBook() throws Exception {
        BookDto dto = getBookDto();
        add(dto).andExpect(status().isOk());

        Book book = bookService.getBookByTitle(BOOK_TITLE);
        assertEquals(BOOK_TITLE, book.getTitle());
        assertEquals(BOOK_PERIOD, book.getPeriod());
        assertEquals(BOOK_TYPE, book.getType());
        assertEquals(BOOK_PLACE_OF_PUB, book.getPlaceOfPublication());
        assertEquals(BOOK_PUBLISHER, book.getPublisher());

        LOGGER.debug("add() result: " + book.toString());
    }

    @Test
    public void addBookNotAuthorized() throws Exception {
        // Should fail as not authorized.
        BookDto dto = getBookDto();
        add(dto).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void addBookBadAuthor() throws Exception {
        // Should fail as not found.
        BookDto dto = getBookDto();
        dto.setAuthorId(5L);
        add(dto).andExpect(status().isNotFound());
    }

    private ResultActions add(final BookDto bookDto) throws Exception {
        return mvc.perform(post("/secure/book/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(JsonHelper.toJson(bookDto)));
    }
}