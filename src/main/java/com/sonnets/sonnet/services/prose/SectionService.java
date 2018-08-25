package com.sonnets.sonnet.services.prose;

import com.sonnets.sonnet.persistence.dtos.MessageDto;
import com.sonnets.sonnet.persistence.dtos.base.RejectDto;
import com.sonnets.sonnet.persistence.dtos.prose.SectionDto;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Confirmation;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.persistence.repositories.SectionRepository;
import com.sonnets.sonnet.services.MessageService;
import com.sonnets.sonnet.services.helpers.GetObjectOrNull;
import com.sonnets.sonnet.services.helpers.SaveObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

/**
 * Deals with everything related to sections.
 *
 * @author Josh Harkema
 */
@Service
public class SectionService {
    private static final Logger LOGGER = Logger.getLogger(SectionService.class);
    private final GetObjectOrNull getObjectOrNull;
    private final SaveObject saveObject;
    private final SectionRepository sectionRepository;
    private final MessageService messageService;

    @Autowired
    public SectionService(GetObjectOrNull getObjectOrNull, SaveObject saveObject, SectionRepository sectionRepository,
                          MessageService messageService) {
        this.getObjectOrNull = getObjectOrNull;
        this.saveObject = saveObject;
        this.sectionRepository = sectionRepository;
        this.messageService = messageService;
    }

    private static Section createOrCopySection(Section section, Author author, Book book, SectionDto dto) {
        section.setCategory(book.getCategory());
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

    public Section get(String id) {
        LOGGER.debug("Getting section id: " + id);
        return getObjectOrNull.section(id);
    }

    public List<Section> getAllFromBook(String bookId) {
        LOGGER.debug("Getting all sections of: " + bookId);
        Book book = getObjectOrNull.book(bookId);
        if (book != null) {
            return book.getSections();
        }
        return Collections.emptyList();
    }

    public ResponseEntity<Void> add(SectionDto dto) {
        LOGGER.debug("Adding new section: " + dto.toString());
        Book book = getObjectOrNull.book(dto.getBookId().toString());
        Author author = getObjectOrNull.author(dto.getAuthorId().toString());
        if (book != null && author != null) {
            LOGGER.debug("BOOK: " + book.toString());
            LOGGER.debug("AUTHOR: " + author.toString());
            Section section = createOrCopySection(new Section(), author, book, dto);
            sectionRepository.saveAndFlush(section);
            book.getSections().add(section);
            saveObject.book(book);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> modify(SectionDto dto) {
        LOGGER.debug("Modifying section (ADMIN): " + dto.toString());
        Section section = getObjectOrNull.section(dto.getId().toString());
        Author author = getObjectOrNull.author(dto.getAuthorId().toString());
        Book book = getObjectOrNull.book(dto.getBookId().toString());
        if (section != null && author != null) {
            sectionRepository.saveAndFlush(createOrCopySection(section, author, book, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> modify(SectionDto dto, Principal principal) {
        LOGGER.debug("Modifying section (USER): " + dto.toString());
        Section section = getObjectOrNull.section(dto.getId().toString());
        Author author = getObjectOrNull.author(dto.getAuthorId().toString());
        Book book = getObjectOrNull.book(dto.getBookId().toString());
        if (section != null && author != null && section.getCreatedBy().equals(principal.getName())) {
            sectionRepository.saveAndFlush(createOrCopySection(section, author, book, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> deleteById(String id) {
        LOGGER.debug("Deleting other with id (ADMIN): " + id);
        Section section = getObjectOrNull.section(id);
        if (section != null) {
            Book book = getObjectOrNull.book(section.getParentId().toString());
            if (book != null) {
                book.getSections().remove(section);
                saveObject.book(book);
                sectionRepository.delete(section);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> confirm(String id, Principal principal) {
        LOGGER.debug(principal.getName() + " is confirming section: " + id);
        Section section = getObjectOrNull.section(id);
        if (section != null) {
            Confirmation confirmation = section.getConfirmation();
            confirmation.setConfirmedBy(principal.getName());
            confirmation.setConfirmedAt(new Timestamp(System.currentTimeMillis()));
            confirmation.setConfirmed(true);
            confirmation.setPendingRevision(false);
            section.setConfirmation(confirmation);
            sectionRepository.saveAndFlush(section);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> reject(RejectDto rejectDto) {
        LOGGER.debug("Rejecting section: " + rejectDto.toString());
        Section section = getObjectOrNull.section(rejectDto.getId());
        if (section != null) {
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
            messageDto.setSubject("One of your Sectopms has been rejected.");
            messageDto.setContent(rejectDto.getRejectMessage());
            messageService.sendAdminMessage(messageDto);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public Section getUnprocessed() {
        LOGGER.debug("Returning an unprocessed section.");
        return sectionRepository.findByProcessed(false).orElse(null);
    }

    public void save(Section section) {
        LOGGER.debug("Saving section: " + section.getId());
        sectionRepository.saveAndFlush(section);
    }
}
