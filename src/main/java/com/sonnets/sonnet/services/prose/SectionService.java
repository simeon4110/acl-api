package com.sonnets.sonnet.services.prose;

import com.sonnets.sonnet.persistence.dtos.base.AnnotationDto;
import com.sonnets.sonnet.persistence.dtos.prose.SectionDto;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.persistence.repositories.AuthorRepository;
import com.sonnets.sonnet.persistence.repositories.BookCharacterRepository;
import com.sonnets.sonnet.persistence.repositories.book.BookRepository;
import com.sonnets.sonnet.persistence.repositories.section.SectionRepositoryBase;
import com.sonnets.sonnet.services.AbstractItemService;
import com.sonnets.sonnet.services.exceptions.AnnotationTypeMismatchException;
import com.sonnets.sonnet.services.exceptions.ItemAlreadyConfirmedException;
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
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        return section;
    }

    /**
     * @param dto the data for the new section.
     * @return OK if the section is added.
     */
    public ResponseEntity<Void> add(SectionDto dto) {
        LOGGER.debug("Adding new section: " + dto.toString());
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        Book book = bookRepository.findById(dto.getBookId()).orElseThrow(ItemAlreadyConfirmedException::new);
        Section section = createOrCopySection(new Section(), author, book, dto);
        sectionRepository.saveAndFlush(section);
        book.getSections().add(section);
        CompletableFuture.runAsync(() -> bookRepository.save(book));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param id the id of the section to delete.
     * @return OK if the book is deleted.
     */
    public ResponseEntity<Void> delete(Long id) {
        LOGGER.debug("Deleting other with id (ADMIN): " + id);
        Section section = sectionRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        Book book = bookRepository.findById(section.getParentId()).orElseThrow(ItemAlreadyConfirmedException::new);
        book.getSections().remove(section);
        CompletableFuture.runAsync(() -> bookRepository.save(book));
        sectionRepository.delete(section);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param id        of the section to delete.
     * @param principal of the user making the request.
     * @return OK if accepted UNAUTHORIZED if user does not own section.
     */
    public ResponseEntity<Void> userDelete(Long id, Principal principal) {
        Section section = sectionRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        if (section.getCreatedBy().equals(principal.getName())) {
            sectionRepository.delete(section);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Get a section.
     *
     * @param id the db id of the section to get.
     * @return the section.
     */
    @Transactional(readOnly = true)
    public Section getById(Long id) {
        LOGGER.debug("Getting section id: " + id);
        return sectionRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * @param ids a list of the db ids for the sections.
     * @return a list of results.
     */
    @Transactional(readOnly = true)
    public List<Section> getByIds(Long[] ids) {
        return null;
    }

    /**
     * @return all the sections. A custom query is used because hibernate stock generates a query for each record.
     * It takes 20 seconds to return all the data. This way takes 200ms.
     */
    @Transactional(readOnly = true)
    public List<Section> getAll() {
        return sectionRepository.findAll();
    }

    /**
     * @return a JSON array of only the most basic details for all sections in the db.
     */
    public String getAllSimple() {
        return sectionRepository.getAllSectionsSimple().orElseThrow(StoredProcedureQueryException::new);
    }

    @Transactional(readOnly = true)
    public Page<Section> getAllPaged(Pageable pageable) {
        return sectionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Section> getAllByUser(Principal principal) {
        return sectionRepository.findAllByCreatedBy(principal.getName()).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * @param dto the new information.
     * @return OK if the section is modified.
     */
    public ResponseEntity<Void> modify(SectionDto dto) {
        LOGGER.debug("Modifying section (ADMIN): " + dto.toString());
        Section section = sectionRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        Book book = bookRepository.findById(dto.getBookId()).orElseThrow(ItemAlreadyConfirmedException::new);
        sectionRepository.saveAndFlush(createOrCopySection(section, author, book, dto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param dto       the new information.
     * @param principal the user making the request.
     * @return OK if the section is modified.
     */
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
            sectionRepository.saveAndFlush(createOrCopySection(section, author, book, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /**
     * :todo: this is wrong.
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
     * @param bookId the book to get the sections from.
     * @return a JSON string of all the sections.
     */
    public String getAllFromBookSimple(Long bookId) {
        LOGGER.debug(String.format("Returning all sections from book id '%s' as JSON.", bookId));
        return sectionRepository.getBookSectionsSimple(bookId).orElseThrow(ItemNotFoundException::new);
    }
}
