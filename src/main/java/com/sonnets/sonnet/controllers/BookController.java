package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.prose.BookDto;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.services.SearchService;
import com.sonnets.sonnet.services.prose.BookService;
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
    private final SearchService searchService;

    @Autowired
    public BookController(BookService bookService, SearchService searchService) {
        this.bookService = bookService;
        this.searchService = searchService;
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

    /**
     * @return a list of books (search results) or nothing.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PostMapping(value = "/book/search", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List search(@RequestBody BookDto bookDto) {
        return searchService.searchBooks(bookDto);
    }
}
