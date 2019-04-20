package com.sonnets.sonnet.services.base;

import com.sonnets.sonnet.config.LuceneConfig;
import com.sonnets.sonnet.persistence.dtos.base.AnnotationDto;
import com.sonnets.sonnet.persistence.dtos.prose.SectionDto;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Book;
import com.sonnets.sonnet.persistence.models.base.Section;
import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import com.sonnets.sonnet.persistence.repositories.AuthorRepository;
import com.sonnets.sonnet.persistence.repositories.BookCharacterRepository;
import com.sonnets.sonnet.persistence.repositories.book.BookRepository;
import com.sonnets.sonnet.persistence.repositories.section.SectionRepositoryBase;
import com.sonnets.sonnet.services.AbstractItemService;
import com.sonnets.sonnet.services.exceptions.AnnotationTypeMismatchException;
import com.sonnets.sonnet.services.exceptions.ItemAlreadyConfirmedException;
import com.sonnets.sonnet.services.exceptions.ItemNotFoundException;
import com.sonnets.sonnet.services.exceptions.StoredProcedureQueryException;
import com.sonnets.sonnet.services.search.SearchConstants;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import search.SearchRepository;
import tools.ParseSourceDetails;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Deals with everything related to sections.
 *
 * @author Josh Harkema
 */
@Service
public class SectionService implements AbstractItemService<Section, SectionDto> {
    private static final Logger LOGGER = Logger.getLogger(SectionService.class);
    private static final ParseSourceDetails<Section, SectionDto> parseSourceDetails = new ParseSourceDetails<>();
    private final SectionRepositoryBase sectionRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookCharacterRepository bookCharacterRepository;
    private final TaskExecutor executor;

    @Autowired
    public SectionService(SectionRepositoryBase sectionRepository, BookRepository bookRepository,
                          AuthorRepository authorRepository, BookCharacterRepository bookCharacterRepository,
                          @Qualifier("threadPoolTaskExecutor") TaskExecutor executor) {
        this.sectionRepository = sectionRepository;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookCharacterRepository = bookCharacterRepository;
        this.executor = executor;
    }

    /**
     * Copy data from a dto onto a section object.
     *
     * @param section the Section to copy the data onto.
     * @param author  the author of the section.
     * @param book    the book the section is in.
     * @param dto     the dto with the new data.
     * @return the Section with the new data copied.
     */
    private static Section createOrCopySection(Section section, Author author, Book book, SectionDto dto) {
        section.setCategory(TypeConstants.SECTION);
        section.setAuthor(author);
        section.setTitle(dto.getTitle());
        section.setDescription(dto.getDescription());
        section = parseSourceDetails.parse(section, dto);
        section.setPeriod(book.getPeriod());
        section.setText(dto.getText());
        section.setParentId(dto.getBookId());
        section.setParentTitle(book.getTitle());
        return section;
    }

    /**
     * @param book    the book to add the section to.
     * @param section the section to add.
     * @return the book with the section added.
     */
    private static Book addBookSection(Book book, Section section) {
        List<Section> sections = book.getSections();
        sections.add(section);
        book.setSections(sections);
        return book;
    }

    private static void addNewSearchDocument(final Section section) {
        LOGGER.debug("Updating section's search document...");
        Document document = SearchRepository.parseCommonFields(new Document(), section);
        document.add(new StringField(SearchConstants.PARENT_ID, section.getParentId().toString(), Field.Store.YES));
        document.add(new TextField(SearchConstants.PARENT_TITLE, section.getParentTitle(), Field.Store.YES));
        document.add(LuceneConfig.getTextField(section.getText()));
        SearchRepository.addDocument(document, TypeConstants.SECTION);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> add(SectionDto dto) {
        LOGGER.debug("Adding new section: " + dto.toString());
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        Book book = bookRepository.findById(dto.getBookId()).orElseThrow(ItemAlreadyConfirmedException::new);
        Section section = createOrCopySection(new Section(), author, book, dto);

        Section out = sectionRepository.save(section);
        bookRepository.save(addBookSection(book, out));
        addNewSearchDocument(out);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> delete(Long id) {
        LOGGER.debug("Deleting section with id (ADMIN): " + id);
        Section section = sectionRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        executor.execute(() -> {
            removeBookSection(section);
            sectionRepository.delete(section);
            SearchRepository.deleteDocument(String.valueOf(id), TypeConstants.SECTION);
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional(readOnly = true)
    public Section getById(Long id) {
        LOGGER.debug("Getting section id: " + id);
        return sectionRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Section> getByIds(Long[] ids) {
        LOGGER.debug("Getting sections by ids: " + Arrays.toString(ids));
        List<Section> results = new ArrayList<>();
        for (Long l : ids)
            results.add(sectionRepository.findById(l).orElseThrow(ItemNotFoundException::new));
        return results;
    }

    @Override
    @Transactional
    public ResponseEntity<Void> userDelete(Long id, Principal principal) {
        LOGGER.debug("Deleting section (user): " + id);
        Section section = sectionRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        if (section.getCreatedBy().equals(principal.getName())) {
            removeBookSection(section);
            executor.execute(() -> {
                sectionRepository.delete(section);
                SearchRepository.deleteDocument(String.valueOf(id), TypeConstants.SECTION);
            });
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Section> getAll() {
        LOGGER.debug("Returning all sections. NOAUTH.");
        return sectionRepository.findAllByIsPublicDomain(true).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Section> authedUserGetAll() {
        LOGGER.debug("Returning all sections. AUTH.");
        return sectionRepository.findAll();
    }

    @Override
    @Transactional
    public String getAllSimple() {
        LOGGER.debug("Returning all sections simple. NOAUTH.");
        return sectionRepository.getAllSectionsSimplePDO().orElseThrow(StoredProcedureQueryException::new);
    }

    @Override
    @Transactional
    public String authedUserGetAllSimple() {
        LOGGER.debug("Returning all sections simple. AUTH.");
        return sectionRepository.getAllSectionsSimple().orElseThrow(StoredProcedureQueryException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Section> getAllByUser(Principal principal) {
        LOGGER.debug("Returning all sections added by user: " + principal.getName());
        return sectionRepository.findAllByCreatedBy(principal.getName()).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Section> getAllPaged(Pageable pageable) {
        LOGGER.debug("Returning all sections paged.");
        return sectionRepository.findAll(pageable);
    }

    /**
     * @param dto the new information.
     * @return OK if the section is modified.
     */
    @Override
    @Transactional
    public ResponseEntity<Void> modify(SectionDto dto) {
        LOGGER.debug("Modifying section (ADMIN): " + dto.toString());
        Section section = sectionRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        Book book = bookRepository.findById(dto.getBookId()).orElseThrow(ItemAlreadyConfirmedException::new);

        bookRepository.save(book);
        SearchRepository.updateSection(sectionRepository.save(createOrCopySection(section, author, book, dto)));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param dto       the new information.
     * @param principal the user making the request.
     * @return OK if the section is modified.
     */
    @Override
    @Transactional
    public ResponseEntity<Void> modifyUser(SectionDto dto, Principal principal) {
        LOGGER.debug("Modifying section (USER): " + dto.toString());
        Section section = sectionRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);
        if (section.getConfirmation().isConfirmed()) {
            throw new ItemAlreadyConfirmedException("This item has already been confirmed.");
        }
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        Book book = bookRepository.findById(dto.getBookId()).orElseThrow(ItemAlreadyConfirmedException::new);

        // Ensure the user making the request is also the owner of the section.
        if (section.getCreatedBy().equals(principal.getName())) {
            SearchRepository.updateSection(sectionRepository.save(
                    createOrCopySection(section, author, book, dto)));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * Returns all sections from a given book.
     *
     * @param bookId the id of the book to get the sections from.
     * @return a list of sections.
     */
    @Transactional(readOnly = true)
    public List<Section> getAllFromBook(Long bookId) {
        LOGGER.debug("Getting all sections of: " + bookId);
        Book book = bookRepository.findById(bookId).orElseThrow(ItemNotFoundException::new);
        return book.getSections();
    }

    /**
     * @param lastName an author's last name.
     * @return a list of results or null.
     */
    @Transactional(readOnly = true)
    public List<Section> getAllByAuthorLastName(String lastName) {
        LOGGER.debug("Returning all sections by author: " + lastName);
        return sectionRepository.findAllByAuthor_LastName(lastName).orElseThrow(ItemNotFoundException::new);
    }

    public Section setNarrator(AnnotationDto dto) {
        LOGGER.debug("Adding narrator to section: " + dto.getSectionId());
        if (!dto.getType().equals("NARA")) {
            throw new AnnotationTypeMismatchException(dto.getType() + " does not match narrator type annotation.");
        }
        Section section = sectionRepository.findById(dto.getSectionId()).orElseThrow(ItemNotFoundException::new);
        if (section.getConfirmation().isConfirmed()) {
            throw new ItemAlreadyConfirmedException("This item has already been confirmed.");
        }
        BookCharacter character = bookCharacterRepository.findById(dto.getItemId())
                .orElseThrow(ItemNotFoundException::new);
        section.setNarrator(character);
        return sectionRepository.save(section);
    }

    /**
     * @param bookId the book to get the sections from.
     * @return a JSON string of all the sections.
     */
    public String getAllFromBookSimple(Long bookId) {
        LOGGER.debug(String.format("Returning all sections from book id '%s' as JSON.", bookId));
        return sectionRepository.getBookSectionsSimple(bookId).orElseThrow(ItemNotFoundException::new);
    }

    public ResponseEntity<Void> deleteNarrator(Long sectionId) {
        Section section = sectionRepository.findById(sectionId).orElseThrow(ItemNotFoundException::new);
        section.setNarrator(null);
        executor.execute(() -> sectionRepository.save(section));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Removes a section from a book AND saves the book.
     *
     * @param section the section to remove.
     */
    private void removeBookSection(Section section) {
        Book book = bookRepository.findById(section.getParentId()).orElseThrow(ItemNotFoundException::new);
        List<Section> sections = book.getSections();
        sections.remove(section);
        book.setSections(sections);
        executor.execute(() -> bookRepository.save(book));
    }
}
