package org.acl.database.controllers.base;

import io.swagger.annotations.*;
import org.acl.database.persistence.dtos.base.BookDto;
import org.acl.database.persistence.dtos.base.BookOutDto;
import org.acl.database.persistence.models.base.Book;
import org.acl.database.services.base.BookService;
import org.acl.database.tools.FormatTools;
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
import java.util.Collections;
import java.util.List;

/**
 * All book related REST endpoints.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
@Api(tags = "Book Endpoints")
public class BookController implements AbstractItemController<Book, BookDto, BookOutDto> {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PostMapping(value = "/secure/book", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Add Book",
            notes = "Adds a new book to the database.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Book added successfully."),
            @ApiResponse(code = 409, message = "A book with that title and author already exists."),
            @ApiResponse(code = 401, message = "Unauthorized request.")
    })
    public ResponseEntity<Void> add(@RequestBody @Valid BookDto dto) {
        return bookService.add(dto);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @DeleteMapping(value = "/secure/book/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Delete Book",
            notes = "Delete an existing book.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Deletion completed successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request."),
            @ApiResponse(code = 404, message = "A book with the requested ID does not exist.")
    })
    public ResponseEntity<Void> delete(@PathVariable("id") Long id, Principal principal) {
        return bookService.delete(id, principal);
    }


    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Book by ID", notes = "Returns a book from its database ID.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Book.class, message = "OK"),
            @ApiResponse(code = 404, message = "A book with the requested ID does not exist.")
    })
    public Book getById(@PathVariable("id") Long id) {
        return bookService.getById(id);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/by_ids/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Books by IDs", notes = "Returns a list of books from a list of database IDs.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Book.class, responseContainer = "List", message = "OK"),
            @ApiResponse(code = 404, message = "A book with the requested ID does not exist.")
    })
    public List<Book> getByIds(@PathVariable("ids") Long[] ids) {
        return bookService.getByIds(ids);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get All Books", notes = "Returns a list of all the public domain books in the database.")
    public List<BookOutDto> getAll() {
        return bookService.getAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/book/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get All Books",
            notes = "Returns a list of all the books in the database.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            }
    )
    public List<BookOutDto> authedUserGetAll() {
        return bookService.authedUserGetAll();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/all/paged", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get All Books Paged", notes = "Returns a paginated list of all the public domain books in " +
            "the database.")
    public Page<Book> getAllPaged(Pageable pageable) {
        return bookService.getAllPaged(pageable);
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @GetMapping(value = "/secure/book/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get All Books Added by User",
            notes = "Returns a list of all the books in the database added by the user making the request.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            }
    )
    public List<Book> getAllByUser(Principal principal) {
        return Collections.emptyList();
    }

    @Override
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
    @PutMapping(value = "/secure/book", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Modify Book",
            notes = "Modify an existing book.",
            authorizations = {
                    @Authorization(value = "oauth",
                            scopes = {
                                    @AuthorizationScope(scope = "admin", description = "Administrative scope."),
                                    @AuthorizationScope(scope = "user", description = "User scope.")
                            }
                    )
            })
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Modification completed successfully."),
            @ApiResponse(code = 401, message = "Unauthorized request."),
            @ApiResponse(code = 404, message = "A book with the requested ID does not exist.")
    })
    public ResponseEntity<Void> modify(@RequestBody @Valid BookDto dto, Principal principal) {
        return bookService.modify(dto, principal);
    }

    /**
     * @param title of the book to find.
     * @return the book with a matching title.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @GetMapping(value = "/book/by_title/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get Book by Title", notes = "Returns a book based on its title, returns null if no match is " +
            "found.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, response = Book.class, message = "OK"),
            @ApiResponse(code = 404, message = "A book with the requested title does not exist.")
    })
    public Book getByTitle(@PathVariable("title") String title) {
        title = FormatTools.parseParam(title);
        return bookService.getBookByTitle(title);
    }
}
