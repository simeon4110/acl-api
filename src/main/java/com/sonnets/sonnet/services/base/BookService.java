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

    @Autowired
    public BookService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
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

    @Override
    @Transactional
    public ResponseEntity<Void> add(BookDto dto) {
        LOGGER.debug("Adding new book: " + dto.toString());
        Book book = new Book();
        try {
            Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
            // Check if book does not exist exists.
            if (bookRepository.findByAuthor_IdAndTitle(dto.getAuthorId(), dto.getTitle()).isEmpty()) {
                bookRepository.saveAndFlush(createOrUpdateFromDto(book, author, dto));
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        } catch (ItemNotFoundException e) {
            LOGGER.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Void> delete(Long id) {
        LOGGER.debug("Deleting book: " + id);
        try {
            Book book = bookRepository.findById(id).orElseThrow(ItemNotFoundException::new);
            bookRepository.delete(book);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ItemNotFoundException e) {
            LOGGER.error(e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @Transactional
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

    @Override
    @Transactional(readOnly = true)
    public Book getById(Long id) {
        LOGGER.debug("Getting book: " + id);
        return bookRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getByIds(Long[] ids) {
        LOGGER.debug("Getting books by ids: " + Arrays.toString(ids));
        List<Book> output = new ArrayList<>();
        for (Long id : ids) {
            Book book = bookRepository.findById(id).orElseThrow(ItemNotFoundException::new);
            output.add(book);
        }
        return output;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAll() {
        LOGGER.debug("Returning all books. NOAUTH");
        return bookRepository.findAllByIsPublicDomain(true).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> authedUserGetAll() {
        LOGGER.debug("Returning all books. AUTH");
        return bookRepository.findAll();
    }

    @Override
    @Transactional
    public String getAllSimple() {
        LOGGER.debug("Returning all books simple. NOAUTH.");
        return bookRepository.getAllBooksSimplePDO().orElseThrow(StoredProcedureQueryException::new);
    }

    @Override
    @Transactional
    public String authedUserGetAllSimple() {
        LOGGER.debug("Returning all books simple. AUTH.");
        return bookRepository.getAllBooksSimple().orElseThrow(StoredProcedureQueryException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> getAllPaged(Pageable pageable) {
        LOGGER.debug("Returning all books paged.");
        return bookRepository.findAllByIsPublicDomain(true, pageable)
                .orElseThrow(ItemNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllByUser(Principal principal) {
        LOGGER.debug("Returning all books added by user: " + principal.getName());
        return bookRepository.findAllByCreatedBy(principal.getName()).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> modify(BookDto dto) {
        LOGGER.debug("Modifying book (ADMIN): " + dto.toString());
        Book book = bookRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        bookRepository.saveAndFlush(createOrUpdateFromDto(book, author, dto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> modifyUser(BookDto dto, Principal principal) {
        LOGGER.debug("Modifying book (USER):" + dto.toString());
        Book book = bookRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);
        if (!book.getCreatedBy().equals(principal.getName())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } else {
            Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
            bookRepository.saveAndFlush(createOrUpdateFromDto(book, author, dto));
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param title title of the book to get.
     * @return the book, if found, throws ItemNotFoundException otherwise.
     */
    public Book getBookByTitle(String title) {
        LOGGER.debug("Getting book by title: " + title);
        return bookRepository.findByTitle(title).orElseThrow(ItemNotFoundException::new);
    }
}