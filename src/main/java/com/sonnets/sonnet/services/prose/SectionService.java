package com.sonnets.sonnet.services.prose;

import com.sonnets.sonnet.persistence.dtos.base.RejectDto;
import com.sonnets.sonnet.persistence.dtos.prose.SectionDto;
import com.sonnets.sonnet.persistence.dtos.web.MessageDto;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Confirmation;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.persistence.repositories.SectionRepositoryBase;
import com.sonnets.sonnet.services.MessageService;
import com.sonnets.sonnet.services.helpers.GetObjectOrThrowNullPointer;
import com.sonnets.sonnet.services.helpers.SaveObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Deals with everything related to sections.
 *
 * @author Josh Harkema
 */
@Service
public class SectionService {
    private static final Logger LOGGER = Logger.getLogger(SectionService.class);
    private final GetObjectOrThrowNullPointer getObjectOrNull;
    private final SaveObject saveObject;
    private final SectionRepositoryBase sectionRepository;
    private final MessageService messageService;
    private final EntityManager em;

    @Autowired
    public SectionService(GetObjectOrThrowNullPointer getObjectOrNull, SaveObject saveObject,
                          SectionRepositoryBase sectionRepository, MessageService messageService, EntityManager em) {
        this.getObjectOrNull = getObjectOrNull;
        this.saveObject = saveObject;
        this.sectionRepository = sectionRepository;
        this.messageService = messageService;
        this.em = em;
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
        section.setCategory("SECTION");
        section.setAuthor(author);
        section.setTitle(dto.getTitle());
        section.setDescription(dto.getDescription());
        section.setPublicationYear(book.getPublicationYear());
        section.setPublicationStmt(book.getPublicationStmt());
        section.setSourceDesc(book.getSourceDesc());
        section.setPeriod(book.getPeriod());
        section.setText(dto.getText());
        section.setParentId(dto.getBookId());
        return section;
    }

    /**
     * Get a section.
     *
     * @param id the db id of the section to get.
     * @return the section.
     */
    public Section get(String id) {
        LOGGER.debug("Getting section id: " + id);
        return getObjectOrNull.section(id);
    }

    /**
     * @return all the sections.
     */
    public List<Section> getAll() {
        Query query = em.createNativeQuery("SELECT section.id,\n" +
                "\t\tsection.title,\n" +
                "\t\tsection.publication_year,\n" +
                "\t\tsection.publication_stmt,\n" +
                "\t\tsection.source_desc,\n" +
                "\t\tsection.period,\n" +
                "\t\t[author].[first_name],\n" +
                "\t\t[author].[last_name],\n" +
                "\t\t[book].[title] AS book_title,\n" +
                "\t\t[book].[type]\n" +
                "\t\tFROM [dbo].[section] section\n" +
                "\t\tINNER JOIN [author] ON section.author_id = [author].[id]\n" +
                "\t\tINNER JOIN [book] ON section.parent_id = [book].[id] ");
        List<Section> results = query.getResultList();
        return results;
    }

    /**
     * Get all sections from a book.
     *
     * @param bookId the id of the book to get the sections from.
     * @return a list of sections.
     */
    public List<Section> getAllFromBook(String bookId) {
        LOGGER.debug("Getting all sections of: " + bookId);
        Book book = getObjectOrNull.book(bookId);
        return book.getSections();
    }

    public List<Section> getAllByAuthorLastName(String lastName) {
        LOGGER.debug("Returning all sections by author: " + lastName);
        return sectionRepository.findAllByAuthor_LastName(lastName).orElseThrow(NullPointerException::new);
    }

    /**
     * Add a section to the db.
     *
     * @param dto the data for the new section.
     * @return OK if the section is added.
     */
    public ResponseEntity<Void> add(SectionDto dto) {
        LOGGER.debug("Adding new section: " + dto.toString());
        Book book = getObjectOrNull.book(dto.getBookId().toString());
        Author author = getObjectOrNull.author(dto.getAuthorId().toString());
        Section section = createOrCopySection(new Section(), author, book, dto);
        sectionRepository.saveAndFlush(section);
        book.getSections().add(section);
        saveObject.book(book);
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
        Section section = getObjectOrNull.section(dto.getId().toString());
        Author author = getObjectOrNull.author(dto.getAuthorId().toString());
        Book book = getObjectOrNull.book(dto.getBookId().toString());
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
        Section section = getObjectOrNull.section(dto.getId().toString());
        Author author = getObjectOrNull.author(dto.getAuthorId().toString());
        Book book = getObjectOrNull.book(dto.getBookId().toString());

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
        Section section = getObjectOrNull.section(id);
        Book book = getObjectOrNull.book(section.getParentId().toString());
        book.getSections().remove(section);
        saveObject.book(book);
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
    public ResponseEntity<Void> confirm(String id, Principal principal) {
        LOGGER.debug(principal.getName() + " is confirming section: " + id);
        Section section = getObjectOrNull.section(id);
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
        Section section = getObjectOrNull.section(rejectDto.getId());
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

    /**
     * @return and unprocessed section.
     */
    public Section getUnprocessed() {
        LOGGER.debug("Returning an unprocessed section.");
        return sectionRepository.findByProcessed(false).orElse(null);
    }

    /**
     * @param section the section object to save.
     */
    public void save(Section section) {
        LOGGER.debug("Saving section: " + section.getId());
        sectionRepository.saveAndFlush(section);
    }
}
