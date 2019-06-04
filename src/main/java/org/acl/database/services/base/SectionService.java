package org.acl.database.services.base;

import org.acl.database.config.LuceneConfig;
import org.acl.database.persistence.dtos.base.AnnotationDto;
import org.acl.database.persistence.dtos.base.SectionOutDto;
import org.acl.database.persistence.dtos.prose.SectionDto;
import org.acl.database.persistence.models.TypeConstants;
import org.acl.database.persistence.models.base.Author;
import org.acl.database.persistence.models.base.Book;
import org.acl.database.persistence.models.base.Section;
import org.acl.database.persistence.models.prose.BookCharacter;
import org.acl.database.persistence.repositories.AuthorRepository;
import org.acl.database.persistence.repositories.BookCharacterRepository;
import org.acl.database.persistence.repositories.BookRepository;
import org.acl.database.persistence.repositories.SectionRepositoryBase;
import org.acl.database.search.SearchRepository;
import org.acl.database.services.AbstractItemService;
import org.acl.database.services.exceptions.AnnotationTypeMismatchException;
import org.acl.database.services.exceptions.ItemAlreadyConfirmedException;
import org.acl.database.services.exceptions.ItemNotFoundException;
import org.acl.database.services.search.SearchConstants;
import org.acl.database.tools.ParseSourceDetails;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
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
 * Deals with everything related to sections.
 *
 * @author Josh Harkema
 */
@Service
public class SectionService implements AbstractItemService<Section, SectionDto, SectionOutDto> {
    private static final Logger LOGGER = Logger.getLogger(SectionService.class);
    private static final ParseSourceDetails<Section, SectionDto> parseSourceDetails = new ParseSourceDetails<>();
    private final SectionRepositoryBase sectionRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookCharacterRepository bookCharacterRepository;

    @Autowired
    public SectionService(SectionRepositoryBase sectionRepository, BookRepository bookRepository,
                          AuthorRepository authorRepository, BookCharacterRepository bookCharacterRepository) {
        this.sectionRepository = sectionRepository;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookCharacterRepository = bookCharacterRepository;
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
        LOGGER.debug("Updating section's com.sonnets.sonnet.search document...");
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
        removeBookSection(section);
        sectionRepository.delete(section);
        SearchRepository.deleteDocument(String.valueOf(id), TypeConstants.SECTION);
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
            sectionRepository.delete(section);
            SearchRepository.deleteDocument(String.valueOf(id), TypeConstants.SECTION);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SectionOutDto> getAll() {
        LOGGER.debug("Returning all sections. NOAUTH.");
        return sectionRepository.getAllPublicDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SectionOutDto> authedUserGetAll() {
        LOGGER.debug("Returning all sections. AUTH.");
        return sectionRepository.getAll();
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

    public ResponseEntity<Void> deleteNarrator(Long sectionId) {
        Section section = sectionRepository.findById(sectionId).orElseThrow(ItemNotFoundException::new);
        section.setNarrator(null);
        sectionRepository.saveAndFlush(section);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Removes a section from a book AND saves the book.
     *
     * @param section the section to remove.
     */
    private void removeBookSection(Section section) {
        Book book = bookRepository.findById(section.getParentId()).orElseThrow(ItemNotFoundException::new);
        book.getSections().remove(section);
        bookRepository.saveAndFlush(book);
    }
}
