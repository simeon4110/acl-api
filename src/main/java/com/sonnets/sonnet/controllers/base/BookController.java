package com.sonnets.sonnet.controllers.base;

import com.sonnets.sonnet.helpers.ParseParam;
import com.sonnets.sonnet.persistence.dtos.prose.BookDto;
import com.sonnets.sonnet.persistence.models.base.Book;
import com.sonnets.sonnet.services.base.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * All book related REST endpoints.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class BookController implements AbstractItemController<Book, BookDto> {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
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
     * Delete a book. (ADMIN ONLY).
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/book/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return bookService.delete(id);
    }

    /**
     * @param id        of the book to delete.
     * @param principal the user making the request.
     * @return OK if deleted, NOT_AUTHORIZED if user does not own book.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/book/user_delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> userDelete(@PathVariable("id") Long id, Principal principal) {
        return bookService.userDelete(id, principal);
    }

    /**
     * @param id the id of the book.
     * @return a book.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/by_id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Book getById(@PathVariable("id") Long id) {
        return bookService.getById(id);
    }

    /**
     * @param ids the db ids of the books to get.
     * @return a list of books.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/by_ids/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Book> getByIds(@PathVariable("ids") Long[] ids) {
        return bookService.getByIds(ids);
    }

    /**
     * @return a list of all books in the db.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Book> getAll(Principal principal) {
        return bookService.getAll(principal);
    }

    /**
     * @return a JSON formatted string of just the basic details of all books in the db.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/all_simple", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllSimple(Principal principal) {
        return bookService.getAllSimple(principal);
    }

    /**
     * @param pageable from the request.
     * @return all the books in the db paged.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/all/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Book> getAllPaged(Principal principal, Pageable pageable) {
        return bookService.getAllPaged(principal, pageable);
    }

    /**
     * @param principal of the user making the request.
     * @return a list of all book's added by the user.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/secure/book/all_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Book> getAllByUser(Principal principal) {
        return null;
    }

    /**
     * @param dto with the modifications.
     * @return OK if modified.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/book/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid BookDto dto) {
        return bookService.modify(dto);
    }

    /**
     * @param dto       the dto with the modifications.
     * @param principal of the user making the request.
     * @return OK if modified, NOT_AUTHORIZED if user does not own book.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/secure/book/modify_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modifyUser(BookDto dto, Principal principal) {
        return bookService.modifyUser(dto, principal);
    }

    /**
     * @param title of the book to find.
     * @return the book with a matching title.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/get_by_title/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Book getByTitle(@PathVariable("title") String title) {
        title = ParseParam.parse(title);
        return bookService.getBookByTitle(title);
    }
}
