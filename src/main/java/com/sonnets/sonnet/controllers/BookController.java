package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.prose.BookDto;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.services.prose.BookService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class BookController {
    private static final String ALLOWED_ORIGIN = "*";
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/book/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Book get(@PathVariable("id") String id) {
        return bookService.get(id);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/book/get_all/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Book> getAll() {
        return bookService.getAll();
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/book/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid BookDto dto) {
        return bookService.add(dto);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/book/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid BookDto dto) {
        return bookService.modify(dto);
    }

    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/book/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable("id") String id) {
        return bookService.delete(id);
    }
}
