package com.sonnets.sonnet.services.prose;

import com.sonnets.sonnet.persistence.dtos.base.AnnotationDto;
import com.sonnets.sonnet.persistence.dtos.base.RejectDto;
import com.sonnets.sonnet.persistence.dtos.prose.SectionDto;
import com.sonnets.sonnet.persistence.dtos.web.MessageDto;
import com.sonnets.sonnet.persistence.models.base.Annotation;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Confirmation;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.persistence.repositories.AuthorRepository;
import com.sonnets.sonnet.persistence.repositories.BookCharacterRepository;
import com.sonnets.sonnet.persistence.repositories.book.BookRepository;
import com.sonnets.sonnet.persistence.repositories.section.SectionRepositoryBase;
import com.sonnets.sonnet.services.MessageService;
import com.sonnets.sonnet.services.ToolsService;
import com.sonnets.sonnet.services.annotations.AnnotationsParseService;
import com.sonnets.sonnet.services.exceptions.AnnotationTypeMismatchException;
import com.sonnets.sonnet.services.exceptions.ItemAlreadyConfirmedException;
import com.sonnets.sonnet.services.exceptions.ItemNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Deals with everything related to sections.
 *
 * @author Josh Harkema
 */
@Service
public class SectionService {
    private static final Logger LOGGER = Logger.getLogger(SectionService.class);
    private final SectionRepositoryBase sectionRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final BookCharacterRepository bookCharacterRepository;

    private final MessageService messageService;
    private final ToolsService toolsService;
    private final AnnotationsParseService annotationsParseService;

    @Autowired
    public SectionService(SectionRepositoryBase sectionRepository, BookRepository bookRepository,
                          AuthorRepository authorRepository, BookCharacterRepository bookCharacterRepository,
                          MessageService messageService, ToolsService toolsService,
                          AnnotationsParseService annotationsParseService) {
        this.sectionRepository = sectionRepository;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.bookCharacterRepository = bookCharacterRepository;
        this.messageService = messageService;
        this.toolsService = toolsService;
        this.annotationsParseService = annotationsParseService;
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
    private Section createOrCopySection(Section section, Author author, Book book, SectionDto dto) {
        section.setCategory("SECT");
        section.setAuthor(author);
        section.setTitle(dto.getTitle());
        section.setDescription(dto.getDescription());
        section.setPublicationYear(book.getPublicationYear());
        section.setPublicationStmt(book.getPublicationStmt());
        section.setSourceDesc(book.getSourceDesc());
        section.setPeriod(book.getPeriod());
        section.setText(parseText(dto.getText()));
        section.setParentId(dto.getBookId());
        if (section.getAnnotation() == null) { // null check to prevent overwriting a section's annotation.
            Annotation annotation = new Annotation();
            CompletableFuture<String> taggedText = toolsService.tagTextSimple(dto.getText());
            taggedText.thenAccept(annotation::setAnnotationBody);
            section.setAnnotation(annotation);
        }
        return section;
    }

    private static String parseText(String text) {
        StringBuilder sb = new StringBuilder();
        for (String line : text.split("\n")) {
            if (!line.equals("")) {
                sb.append(line.trim());
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    /**
     * Get a section.
     *
     * @param id the db id of the section to get.
     * @return the section.
     */
    public Section get(String id) {
        LOGGER.debug("Getting section id: " + id);
        return getSectionOrThrowNotFound(id);
    }

    public Section get(Long id) {
        LOGGER.debug("Getting section id: " + id);
        return getSectionOrThrowNotFound(id);
    }

    /**
     * @return all the sections. A custom query is used because hibernate stock generates a query for each record.
     * It takes 20 seconds to return all the data. This way takes 200ms.
     */
    public String getAll() {
        return sectionRepository.getAllSections();
    }

    /**
     * Get a section's title as well as it's parent title on the quick.
     *
     * @param id the db id of the book to get the title of.
     * @return the book's title. (title = section title, bookTitle = parent title}
     */
    public String getTitle(String id) {
        String title = bookRepository.getBookTitle(Long.parseLong(id)).orElseThrow(ItemNotFoundException::new);
        try {
            return new JSONObject().put("title", title).toString();
        } catch (JSONException e) {
            LOGGER.error(e);
            return null;
        }
    }

    /**
     * Get all sections from a book.
     *
     * @param bookId the id of the book to get the sections from.
     * @return a list of sections.
     */
    public List<Section> getAllFromBook(String bookId) {
        LOGGER.debug("Getting all sections of: " + bookId);
        Book book = bookRepository.findById(Long.parseLong(bookId)).orElseThrow(ItemNotFoundException::new);
        return book.getSections();
    }

    public List<Section> getAllByAuthorLastName(String lastName) {
        LOGGER.debug("Returning all sections by author: " + lastName);
        return sectionRepository.findAllByAuthor_LastName(lastName).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * Returns the section title and id as a JSON string.
     *
     * @param bookId the book to get the sections of.
     * @return a JSON string of all the sections.
     */
    public String getAllFromBookSimple(String bookId) {
        LOGGER.debug(String.format("Returning all sections from book id '%s' as JSON.", bookId));
        return sectionRepository.getBookSectionsSimple(Long.parseLong(bookId)).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * Add a section to the db.
     *
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
     * Modify a section. No user-checking.
     *
     * @param dto the new information.
     * @return OK if the section is modified.
     */
    public ResponseEntity<Void> modify(SectionDto dto) {
        LOGGER.debug("Modifying section (ADMIN): " + dto.toString());
        Section section = getSectionOrThrowNotFound(dto.getId());
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        Book book = bookRepository.findById(dto.getBookId()).orElseThrow(ItemAlreadyConfirmedException::new);
        sectionRepository.saveAndFlush(createOrCopySection(section, author, book, dto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Modify a section with user checking.
     *
     * @param dto       the new information.
     * @param principal the user making the request.
     * @return OK if the section is modified.
     */
    public ResponseEntity<Void> modify(SectionDto dto, Principal principal) {
        LOGGER.debug("Modifying section (USER): " + dto.toString());
        Section section = getSectionOrThrowNotFound(dto.getId());
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
     * Delete a section from the database.
     *
     * @param id the id of the book to delete.
     * @return OK if the book is deleted.
     */
    public ResponseEntity<Void> deleteById(String id) {
        LOGGER.debug("Deleting other with id (ADMIN): " + id);
        Section section = getSectionOrThrowNotFound(id);
        Book book = bookRepository.findById(section.getParentId()).orElseThrow(ItemAlreadyConfirmedException::new);
        book.getSections().remove(section);
        CompletableFuture.runAsync(() -> bookRepository.save(book));
        sectionRepository.delete(section);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Confirm a section.
     *
     * @param id        the id of the section to confirm.
     * @param principal the user doing the confirming.
     * @return 200 if the book is confirmed.
     */
    public ResponseEntity<Void> confirm(Long id, Principal principal) {
        LOGGER.debug(principal.getName() + " is confirming section: " + id);
        Section section = getSectionOrThrowNotFound(id);
        Confirmation confirmation = section.getConfirmation();
        confirmation.setConfirmedBy(principal.getName());
        confirmation.setConfirmedAt(new Timestamp(System.currentTimeMillis()));
        confirmation.setConfirmed(true);
        confirmation.setPendingRevision(false);
        section.setConfirmation(confirmation);
        sectionRepository.saveAndFlush(section);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Reject a section.
     *
     * @param rejectDto a valid rejection dto.
     * @return OK if the section is rejected.
     */
    public ResponseEntity<Void> reject(RejectDto rejectDto) {
        LOGGER.debug("Rejecting section: " + rejectDto.toString());
        Section section = getSectionOrThrowNotFound(rejectDto.getId());
        Confirmation confirmation = section.getConfirmation();
        confirmation.setConfirmedBy(null);
        confirmation.setConfirmedAt(null);
        confirmation.setConfirmed(false);
        confirmation.setPendingRevision(true);
        section.setConfirmation(confirmation);
        sectionRepository.saveAndFlush(section);

        MessageDto messageDto = new MessageDto();
        messageDto.setUserFrom("Administrator");
        messageDto.setUserTo(section.getCreatedBy());
        messageDto.setSubject("One of your Sections has been rejected.");
        messageDto.setContent(rejectDto.getRejectMessage());
        messageService.sendAdminMessage(messageDto);

        return new ResponseEntity<>(HttpStatus.OK);
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

    public String getAnnotations(final Long id) {
        LOGGER.debug("Getting annotations for section: " + id);
        return annotationsParseService.parseSectionAnnotationOut(
                getSectionOrThrowNotFound(id)
        ).toString();
    }

    private Section getSectionOrThrowNotFound(String id) {
        long parsedId = Long.parseLong(id);
        return sectionRepository.findById(parsedId).orElseThrow(ItemNotFoundException::new);
    }

    private Section getSectionOrThrowNotFound(Long id) {
        return sectionRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }
}
