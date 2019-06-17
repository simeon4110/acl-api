package org.acl.database.services.base;

import org.acl.database.persistence.dtos.base.BookDto;
import org.acl.database.persistence.dtos.base.BookOutDto;
import org.acl.database.persistence.models.TypeConstants;
import org.acl.database.persistence.models.base.Author;
import org.acl.database.persistence.models.base.Book;
import org.acl.database.persistence.models.base.Section;
import org.acl.database.persistence.repositories.AuthorRepository;
import org.acl.database.persistence.repositories.BookRepository;
import org.acl.database.persistence.repositories.SectionRepositoryBase;
import org.acl.database.security.UserDetailsServiceImpl;
import org.acl.database.services.AbstractItemService;
import org.acl.database.services.exceptions.ItemNotFoundException;
import org.acl.database.tools.ParseSourceDetails;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class BookService implements AbstractItemService<Book, BookDto, BookOutDto> {
    private static final Logger LOGGER = Logger.getLogger(BookService.class);
    private static final ParseSourceDetails<Book, BookDto> parseSourceDetails = new ParseSourceDetails<>();
    private final BookRepository bookRepository;
    private final SectionRepositoryBase sectionRepositoryBase;
    private final AuthorRepository authorRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public BookService(BookRepository bookRepository, SectionRepositoryBase sectionRepositoryBase,
                       AuthorRepository authorRepository, UserDetailsServiceImpl userDetailsService) {
        this.bookRepository = bookRepository;
        this.sectionRepositoryBase = sectionRepositoryBase;
        this.authorRepository = authorRepository;
        this.userDetailsService = userDetailsService;
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
        if (book.getSections() == null) {
            book.setSections(new ArrayList<>());
        }
        if (book.getBookCharacters() == null) {
            book.setBookCharacters(new ArrayList<>());
        }
        return book;
    }

    @Override
    @Transactional
    public ResponseEntity<Void> add(BookDto dto) {
        LOGGER.debug("Adding new book: " + dto.toString());
        Book book = new Book();
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        // Check if book does not exist exists.
        if (bookRepository.findByAuthor_IdAndTitle(dto.getAuthorId(), dto.getTitle()).isEmpty()) {
            bookRepository.saveAndFlush(createOrUpdateFromDto(book, author, dto));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Void> delete(Long id, Principal principal) {
        LOGGER.debug("Deleting book: " + id);
        Book book = bookRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        if (principal.getName().equals(book.getCreatedBy()) || userDetailsService.userIsAdmin(principal)) {
            bookRepository.delete(book);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
    public List<BookOutDto> getAll() {
        LOGGER.debug("Returning all books. NOAUTH");
        return bookRepository.getAllPublicDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookOutDto> authedUserGetAll() {
        LOGGER.debug("Returning all books. AUTH");
        return bookRepository.getAll();
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
        return bookRepository.findAllByCreatedBy(principal.getName())
                .orElseThrow(ItemNotFoundException::new);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> modify(BookDto dto, Principal principal) {
        LOGGER.debug("Modifying book (ADMIN): " + dto.toString());
        Book book = bookRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);
        if (principal.getName().equals(book.getCreatedBy()) || userDetailsService.userIsAdmin(principal)) {
            Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
            bookRepository.saveAndFlush(createOrUpdateFromDto(book, author, dto));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Transactional
    public ResponseEntity<Void> addSection(long bookId, long sectionId) {
        LOGGER.debug("Adding section to book: " + bookId);
        Book book = bookRepository.findById(bookId).orElseThrow(ItemNotFoundException::new);
        Section section = sectionRepositoryBase.findById(sectionId).orElseThrow(ItemNotFoundException::new);
        List<Section> sections = book.getSections();
        sections.add(section);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * @param title title of the book to get.
     * @return the book, if found, throws ItemNotFoundException otherwise.
     */
    @Transactional(readOnly = true)
    public Book getBookByTitle(String title) {
        LOGGER.debug("Getting book by title: " + title);
        return bookRepository.findByTitle(title).orElse(null);
    }
}