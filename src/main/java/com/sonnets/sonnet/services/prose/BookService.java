package com.sonnets.sonnet.services.prose;

import com.sonnets.sonnet.persistence.dtos.prose.BookDto;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.repositories.book.BookRepository;
import com.sonnets.sonnet.services.AuthorService;
import com.sonnets.sonnet.services.exceptions.ItemNotFoundException;
import com.sonnets.sonnet.services.exceptions.StoredProcedureQueryException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tools.ParseSourceDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class deals with the components of a prose work. Characters and sections are managed here.
 *
 * @author Josh Harkema
 */
@Service
public class BookService {
    private static final Logger LOGGER = Logger.getLogger(BookService.class);
    private static final ParseSourceDetails<Book, BookDto> parseSourceDetails = new ParseSourceDetails<>();
    private final BookRepository bookRepository;
    private final AuthorService authorService;

    @Autowired
    public BookService(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    /**
     * Helper copies data from dto to book object.
     *
     * @param book   the book to copy the dto onto.
     * @param author the author of the book.
     * @param dto    the dto with the new info.
     * @return the book with the new data applied.
     */
    private static Book createOrCopyBook(Book book, Author author, BookDto dto) {
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
     * Get a book by db id.
     *
     * @param id the db id of the book.
     * @return the book.
     */
    public Book get(String id) {
        LOGGER.debug("Getting book: " + id);
        return getBookOrThrowNotFound(id);
    }

    public Book getBookByTitle(String title) {
        LOGGER.debug("Getting book by title: " + title);
        return bookRepository.findByTitle(title);
    }

    /**
     * @return all books in the db.
     */
    public List<Book> getAll() {
        LOGGER.debug("Returning all books.");
        return bookRepository.findAll();
    }

    public String getAllSimple() {
        return bookRepository.getAllBooksSimple().orElseThrow(StoredProcedureQueryException::new);
    }

    /**
     * Add a new book to the db.
     *
     * @param dto the new book's info.
     * @return OK if the book is added, BAD_REQUEST if the book already exists.
     */
    public ResponseEntity<Void> add(BookDto dto) {
        LOGGER.debug("Adding new book: " + dto.toString());
        Book book = new Book();
        Author author = authorService.getAuthorOrThrowNotFound(dto.getAuthorId());
        // Check if book does not exist exists.
        if (bookRepository.findByAuthor_IdAndTitle(Long.parseLong(dto.getAuthorId()), dto.getTitle()) == null) {
            bookRepository.saveAndFlush(createOrCopyBook(book, author, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Modify an existing book.
     *
     * @param dto the dto with the new info.
     * @return OK if the book is modified.
     */
    public ResponseEntity<Void> modify(BookDto dto) {
        LOGGER.debug("Modifying book: " + dto.toString());
        Book book = getBookOrThrowNotFound(dto.getId());
        Author author = authorService.getAuthorOrThrowNotFound(dto.getAuthorId());
        bookRepository.saveAndFlush(createOrCopyBook(book, author, dto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Remove a book from the db.
     *
     * @param id the id of the book to remove.
     * @return OK if the book is removed.
     */
    public ResponseEntity<Void> delete(String id) {
        LOGGER.debug("Deleting book: " + id);
        Book book = getBookOrThrowNotFound(id);
        bookRepository.delete(book);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Book getBookOrThrowNotFound(Long id) {
        LOGGER.debug("Getting book with id: " + id);
        return bookRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    Book getBookOrThrowNotFound(String id) {
        LOGGER.debug("Getting book with id: " + id);
        long parsedId = Long.parseLong(id);
        return bookRepository.findById(parsedId).orElseThrow(ItemNotFoundException::new);
    }

    @Async
    void save(Book book) {
        LOGGER.debug("Saving book: " + book.toString());
        CompletableFuture.runAsync(() -> bookRepository.save(book));
    }
}