package com.sonnets.sonnet.services.base;

import com.sonnets.sonnet.persistence.dtos.prose.BookDto;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Book;
import com.sonnets.sonnet.persistence.repositories.AuthorRepository;
import com.sonnets.sonnet.persistence.repositories.book.BookRepository;
import com.sonnets.sonnet.services.AbstractItemService;
import com.sonnets.sonnet.services.exceptions.ItemNotFoundException;
import com.sonnets.sonnet.services.exceptions.StoredProcedureQueryException;
import com.sonnets.sonnet.services.search.SearchQueryHandlerService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.ParseSourceDetails;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class deals with the components of a prose work. Characters and sections are managed here.
 *
 * @author Josh Harkema
 */
@Service
public class BookService implements AbstractItemService<Book, BookDto> {
    private static final Logger LOGGER = Logger.getLogger(BookService.class);
    private static final ParseSourceDetails<Book, BookDto> parseSourceDetails = new ParseSourceDetails<>();
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final SearchQueryHandlerService queryHandlerService;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository,
                       SearchQueryHandlerService queryHandlerService) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.queryHandlerService = queryHandlerService;
    }

    /**
     * Helper copies data from dto to book object.
     *
     * @param book   the book to copy the dto onto.
     * @param author the author of the book.
     * @param dto    the dto with the new info.
     * @return the book with the new data applied.
     */
    private static Book createOrUpdateFromDto(Book book, Author author, BookDto dto) {
        book.setAuthor(author);
        book.setTitle(dto.getTitle());
        book = parseSourceDetails.parse(book, dto);
        book.setPeriod(dto.getPeriod());
        book.setCategory(TypeConstants.BOOK);
        book.setType(dto.getType());
        book.setSections(new ArrayList<>());
        book.setBookCharacters(new ArrayList<>());
        return book;
    }

    /**
     * @param dto the new book's info.
     * @return OK if the book is added, BAD_REQUEST if the book already exists.
     */
    public ResponseEntity<Void> add(BookDto dto) {
        LOGGER.debug("Adding new book: " + dto.toString());
        Book book = new Book();
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        // Check if book does not exist exists.
        if (bookRepository.findByAuthor_IdAndTitle(dto.getAuthorId(), dto.getTitle()).isEmpty()) {
            bookRepository.saveAndFlush(createOrUpdateFromDto(book, author, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.CONFLICT);
    }

    /**
     * @param id the id of the book to remove.
     * @return OK if the book is removed.
     */
    public ResponseEntity<Void> delete(Long id) {
        LOGGER.debug("Deleting book: " + id);
        Book book = bookRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        bookRepository.delete(book);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param id        the id of the book to remove.
     * @param principal the user making the request.
     * @return OK if removed, NOT_AUTHORIZED if user does not own book.
     */
    public ResponseEntity<Void> userDelete(Long id, Principal principal) {
        LOGGER.debug("Deleting book (USER): " + id);
        Book book = bookRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        if (book.getCreatedBy().equals(principal.getName())) {
            bookRepository.delete(book);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * @param id the db id of the book.
     * @return the book.
     */
    @Transactional(readOnly = true)
    public Book getById(Long id) {
        LOGGER.debug("Getting book: " + id);
        return bookRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * @param ids the db ids of the books to get.
     * @return a list of books.
     */
    @Transactional
    public List<Book> getByIds(Long[] ids) {
        LOGGER.debug("Getting books by ids: " + Arrays.toString(ids));
        List<Book> output = new ArrayList<>();
        for (Long id : ids) {
            Book book = bookRepository.findById(id).orElseThrow(ItemNotFoundException::new);
            output.add(book);
        }
        return output;
    }

    /**
     * @return all books in the db.
     */
    @Transactional
    public List<Book> getAll(Principal principal) {
        LOGGER.debug("Returning all books.");
        return bookRepository.findAll();
    }

    /**
     * @return a JSON array of only the most basic details of every book in the db.
     */
    public String getAllSimple(Principal principal) {
        if (principal != null) {
            return bookRepository.getAllBooksSimple().orElseThrow(StoredProcedureQueryException::new);
        } else {
            return bookRepository.getAllBooksSimplePDO().orElseThrow(StoredProcedureQueryException::new);
        }
    }

    /**
     * @param pageable from the request.
     * @return a page of all books in the database.
     */
    @Transactional(readOnly = true)
    public Page<Book> getAllPaged(Principal principal, Pageable pageable) {
        if (principal != null) {
            return bookRepository.findAll(pageable);
        } else {
            return bookRepository.findAllByIsPublicDomain(true, pageable)
                    .orElseThrow(ItemNotFoundException::new);
        }
    }

    /**
     * @param principal of the user making the request.
     * @return all books added by the user.
     */
    @Transactional(readOnly = true)
    public List<Book> getAllByUser(Principal principal) {
        return bookRepository.findAllByCreatedBy(principal.getName()).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * @param dto the dto with the new info.
     * @return OK if the book is modified.
     */
    public ResponseEntity<Void> modify(BookDto dto) {
        LOGGER.debug("Modifying book: " + dto.toString());
        Book book = bookRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        bookRepository.saveAndFlush(createOrUpdateFromDto(book, author, dto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> modifyUser(BookDto dtoType, Principal principal) {
        return null;
    }

    public List getBookByTitle(String title) {
        LOGGER.debug("Getting book by title: " + title);
        return queryHandlerService.findBookByTitle(title);
    }
}