package org.acl.database.controllers.base;

import org.acl.database.helpers.JsonHelper;
import org.acl.database.persistence.dtos.base.AuthorDto;
import org.acl.database.persistence.dtos.prose.BookDto;
import org.acl.database.persistence.models.base.Author;
import org.acl.database.persistence.models.base.Book;
import org.acl.database.persistence.repositories.AuthorRepository;
import org.acl.database.services.base.BookService;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private static final String AUTHOR_LAST_NAME = UUID.randomUUID().toString();
    @Autowired
    private MockMvc mvc;
    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorRepository authorRepository;

    private Author author;

    private BookDto getBookDto() {
        BookDto dto = new BookDto();
        dto.setAuthorId(this.author.getId());
        dto.setTitle(BOOK_TITLE);
        dto.setPeriod(BOOK_PERIOD);
        dto.setType(BOOK_TYPE);
        dto.setPublicDomain(true);
        dto.setPlaceOfPublication(BOOK_PLACE_OF_PUB);
        dto.setPublisher(BOOK_PUBLISHER);
        return dto;
    }

    @Before
    public void setUp() {
        Author author = new Author();
        author.setFirstName("test-first-name");
        author.setLastName(AUTHOR_LAST_NAME);
        author = this.authorRepository.saveAndFlush(author);

        AuthorDto authorDto = new AuthorDto();
        authorDto.setFirstName("test-first-name");
        authorDto.setLastName(AUTHOR_LAST_NAME);

        this.author = author;
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void add() throws Exception {
        add(getBookDto()).andExpect(status().isOk());

        Book book = bookService.getBookByTitle(BOOK_TITLE);
        assertEquals(BOOK_TITLE, book.getTitle());
        assertEquals(BOOK_PERIOD, book.getPeriod());
        assertEquals(BOOK_TYPE, book.getType());
        assertEquals(BOOK_PLACE_OF_PUB, book.getPlaceOfPublication());
        assertEquals(BOOK_PUBLISHER, book.getPublisher());

        LOGGER.debug("add() result: " + book.toString());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void getBookById() throws Exception {
        add(getBookDto()).andExpect(status().isOk());

        MvcResult result = mvc.perform(get("/book/by_id/1"))
                .andExpect(status().isOk())
                .andReturn();

        Book book = JsonHelper.fromJsonResult(result, Book.class);
        assertEquals(BOOK_TITLE, book.getTitle());
        assertEquals(BOOK_PERIOD, book.getPeriod());
        assertEquals(BOOK_TYPE, book.getType());
        assertEquals(BOOK_PLACE_OF_PUB, book.getPlaceOfPublication());
        assertEquals(BOOK_PUBLISHER, book.getPublisher());
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

//    @Test
//    @WithMockUser(authorities = {"USER"})
//    public void addDuplicate() throws Exception {
//        BookDto dto = getBookDto();
//        add(dto).andExpect(status().isOk());
//
//        BookDto duplicateDto = getBookDto();
//        add(duplicateDto).andExpect(status().isConflict());
//    }


    @Test
    @WithMockUser(authorities = {"USER", "ADMIN"})
    public void modify() throws Exception {
        add(getBookDto()).andExpect(status().isOk());
        Book book = bookService.getBookByTitle(BOOK_TITLE);

        BookDto modifyDto = getBookDto();
        modifyDto.setId(book.getId());
        modifyDto.setTitle(BOOK_TITLE + "-modified");

        mvc.perform(put("/secure/book/modify")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(JsonHelper.toJson(modifyDto)))
                .andExpect(status().isOk());

        Book bookToVerify = bookService.getById(book.getId());
        assertEquals(modifyDto.getTitle(), bookToVerify.getTitle());
        LOGGER.debug("Modify Result: " + bookToVerify.toString());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void modifyNotAuthorized() throws Exception {
        add(getBookDto()).andExpect(status().isOk());
        Book book = bookService.getBookByTitle(BOOK_TITLE);

        BookDto modifyDto = getBookDto();
        modifyDto.setId(book.getId());
        modifyDto.setTitle(BOOK_TITLE + "-modified");

        mvc.perform(put("/secure/book/modify")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(JsonHelper.toJson(modifyDto)))
                .andExpect(status().isUnauthorized());
    }

    private ResultActions add(final BookDto bookDto) throws Exception {
        LOGGER.debug("BOOK DTO: " + bookDto);
        return mvc.perform(post("/secure/book/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(JsonHelper.toJson(bookDto)));
    }
}