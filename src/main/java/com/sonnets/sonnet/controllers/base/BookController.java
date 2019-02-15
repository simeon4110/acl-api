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

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/book/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> add(@RequestBody @Valid BookDto dto) {
        return bookService.add(dto);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/secure/book/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return bookService.delete(id);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/book/user_delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> userDelete(@PathVariable("id") Long id, Principal principal) {
        return bookService.userDelete(id, principal);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/by_id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Book getById(@PathVariable("id") Long id) {
        return bookService.getById(id);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/by_ids/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Book> getByIds(@PathVariable("ids") Long[] ids) {
        return bookService.getByIds(ids);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Book> getAll() {
        return bookService.getAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/book/all", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<Book> authedUserGetAll() {
        return bookService.authedUserGetAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/all_simple", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getAllSimple() {
        return bookService.getAllSimple();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/book/all_simple", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String authedUserGetAllSimple() {
        return bookService.authedUserGetAllSimple();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/all/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<Book> getAllPaged(Pageable pageable) {
        return bookService.getAllPaged(pageable);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/book/all_user", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Book> getAllByUser(Principal principal) {
        return null;
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/book/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(@RequestBody @Valid BookDto dto) {
        return bookService.modify(dto);
    }

    @Override
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
    public List getByTitle(@PathVariable("title") String title) {
        title = ParseParam.parse(title);
        return bookService.getBookByTitle(title);
    }
}
