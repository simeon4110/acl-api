package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.prose.BookDto;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.services.prose.BookService;
import com.sonnets.sonnet.tools.ParseParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * All book related REST endpoints.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * @param id the id of the book.
     * @return a book.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Book get(@PathVariable("id") String id) {
        return bookService.get(id);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/get_by_title/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Book getByTitle(@PathVariable("title") String title) {
        title = ParseParam.parse(title);
        return bookService.getBookByTitle(title);
    }

    /**
     * Returns the title of a book from its ide.
     *
     * @param id the db id of the book to get the title for.
     * @return the book's title.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/get_title/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getTitle(@PathVariable("id") String id) {
        return bookService.getTitle(id);
    }
    /**
     * @return a list of all books in the db.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/get_all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Book> getAll() {
        return bookService.getAll();
    }

    /**
     * @return a JSON formatted string of just the basic details of all books in the db.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/get_all_simple", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllSimple() {
        return bookService.getBooksSimple();
    }

    /**
     * Add a book.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/book/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid BookDto dto) {
        return bookService.add(dto);
    }

    /**
     * Modify a book.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/book/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid BookDto dto) {
        return bookService.modify(dto);
    }

    /**
     * Delete a book. (ADMIN ONLY).
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/book/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        return bookService.delete(id);
    }

    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/get_characters/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBookCharacters(@PathVariable("id") String id) {
        return bookService.getBookCharacters(id);
    }
}
