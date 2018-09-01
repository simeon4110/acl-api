package com.sonnets.sonnet.services.prose;

import com.sonnets.sonnet.persistence.dtos.prose.BookDto;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.repositories.BookRepository;
import com.sonnets.sonnet.services.helpers.GetObjectOrThrowNullException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * This class deals with the components of a prose work. Characters and sections are managed here.
 *
 * @author Josh Harkema
 */
@Service
public class BookService {
    private static final Logger LOGGER = Logger.getLogger(BookService.class);
    private final GetObjectOrThrowNullException getObjectOrNull;
    private final BookRepository bookRepository;

    @Autowired
    public BookService(GetObjectOrThrowNullException getObjectOrNull, BookRepository bookRepository) {
        this.getObjectOrNull = getObjectOrNull;
        this.bookRepository = bookRepository;
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
        book.setPublicationYear(dto.getPublicationYear());
        book.setPublicationStmt(dto.getPublicationStmt());
        book.setSourceDesc(dto.getSourceDesc());
        book.setPeriod(dto.getPeriod());
        book.setCategory(dto.getCategory());
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
        return getObjectOrNull.book(id);
    }

    /**
     * @return all books in the db.
     */
    public List<Book> getAll() {
        LOGGER.debug("Returning all books.");
        return bookRepository.findAll();
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
        Author author = getObjectOrNull.author(dto.getAuthorId());
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
        Book book = getObjectOrNull.book(dto.getId().toString());
        Author author = getObjectOrNull.author(dto.getAuthorId());
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
        Book book = getObjectOrNull.book(id);
        bookRepository.delete(book);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}