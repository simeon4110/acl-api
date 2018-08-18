package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.MessageDto;
import com.sonnets.sonnet.persistence.dtos.base.RejectDto;
import com.sonnets.sonnet.persistence.dtos.prose.BookDto;
import com.sonnets.sonnet.persistence.dtos.prose.CharacterDto;
import com.sonnets.sonnet.persistence.dtos.prose.SectionDto;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Confirmation;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.persistence.repositories.BookRepository;
import com.sonnets.sonnet.persistence.repositories.CharacterRepository;
import com.sonnets.sonnet.persistence.repositories.SectionRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.List;

/**
 * This class deals with the components of a prose work. Characters and sections are managed here.
 *
 * @author Josh Harkema
 */
@Service
public class ProseService {
    private static final Logger LOGGER = Logger.getLogger(ProseService.class);
    private final BookRepository bookRepository;
    private final SectionRepository sectionRepository;
    private final CharacterRepository characterRepository;
    private final AuthorService authorService;
    private final MessageService messageService;

    @Autowired
    public ProseService(BookRepository bookRepository, SectionRepository sectionRepository,
                        CharacterRepository characterRepository, AuthorService authorService,
                        MessageService messageService) {
        this.bookRepository = bookRepository;
        this.sectionRepository = sectionRepository;
        this.characterRepository = characterRepository;
        this.authorService = authorService;
        this.messageService = messageService;
    }

    private static Book createOrCopyBook(Book book, Author author, BookDto dto) {
        book.setAuthor(author);
        book.setTitle(dto.getTitle());
        book.setPublicationYear(dto.getPublicationYear());
        book.setPublicationStmt(dto.getPublicationStmt());
        book.setSourceDesc(dto.getSourceDesc());
        book.setPeriod(dto.getPeriod());
        book.setCategory(dto.getCategory());
        book.setType(dto.getType());
        return book;
    }

    private static Section createOrCopySection(Section section, Author author, SectionDto dto) {
        section.setTitle(dto.getTitle());
        section.setText(dto.getText());
        section.setAuthor(author);
        return section;
    }

    private static BookCharacter createOrCopyCharacter(BookCharacter bookCharacter, CharacterDto dto) {
        bookCharacter.setFirstName(dto.getFirstName());
        bookCharacter.setLastName(dto.getLastName());
        bookCharacter.setGender(dto.getGender());
        bookCharacter.setDescription(dto.getDescription());
        return bookCharacter;
    }

    private static Boolean characterAlreadyExists(Book book, String firstName, String lastName) {
        boolean exists = false;
        List<BookCharacter> bookCharacters = book.getBookCharacters();
        for (BookCharacter c : bookCharacters) {
            if (c.getFirstName().equals(firstName) && c.getLastName().equals(lastName)) {
                exists = true;
            }
        }
        return exists;
    }

    //########## BOOK STUFF ##########//

    public ResponseEntity<Void> createBook(BookDto dto) {
        LOGGER.debug("Adding new book: " + dto.toString());
        Book book = new Book();
        Author author = authorService.get(dto.getAuthorId().toString());
        if (bookRepository.findByAuthor_IdAndTitle(dto.getAuthorId(), dto.getTitle()) == null
                && author != null) {
            bookRepository.saveAndFlush(createOrCopyBook(book, author, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> modifyBook(BookDto dto) {
        LOGGER.debug("Modifying book: " + dto.toString());
        Book book = getBookOrNull(dto.getId().toString());
        Author author = authorService.get(dto.getAuthorId().toString());
        if (book != null && author != null) {
            bookRepository.saveAndFlush(createOrCopyBook(book, author, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ########## SECTION STUFF ##########//

    public ResponseEntity<Void> addSection(SectionDto dto) {
        LOGGER.debug("Adding new section: " + dto.toString());
        Book book = getBookOrNull(dto.getBookId().toString());
        Author author = authorService.get(dto.getAuthorId().toString());
        if (book != null && author != null) {
            Section section = new Section();
            book.getSections().add(createOrCopySection(section, author, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> modifySection(SectionDto dto) {
        LOGGER.debug("Modifying section: " + dto.toString());
        Section section = getSectionOrNull(dto.getId().toString());
        Author author = authorService.get(dto.getAuthorId().toString());
        if (section != null && author != null) {
            sectionRepository.saveAndFlush(createOrCopySection(section, author, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> confirmSection(String id, Principal principal) {
        LOGGER.debug(principal.getName() + " is confirming section: " + id);
        Section section = getSectionOrNull(id);
        if (section != null) {
            Confirmation confirmation = new Confirmation();
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

    public ResponseEntity<Void> rejectSection(RejectDto rejectDto) {
        LOGGER.debug("Rejecting section: " + rejectDto.toString());
        Section section = getSectionOrNull(rejectDto.getId());
        if (section != null) {
            Confirmation confirmation = new Confirmation();
            confirmation.setConfirmedBy(null);
            confirmation.setConfirmedAt(null);
            confirmation.setConfirmed(false);
            confirmation.setPendingRevision(true);
            section.setConfirmation(confirmation);
            sectionRepository.saveAndFlush(section);

            MessageDto messageDto = new MessageDto();

            messageDto.setUserFrom("Administrator");
            messageDto.setUserTo(section.getCreatedBy());
            messageDto.setSubject("One of your sonnets has been rejected.");
            messageDto.setContent(rejectDto.getRejectMessage());
            messageService.sendAdminMessage(messageDto);

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    //########## CHARACTER STUFF ##########//

    public ResponseEntity<Void> addCharacter(CharacterDto dto) {
        LOGGER.debug("Adding character: " + dto.toString());
        Book book = getBookOrNull(dto.getBookId());
        if (book != null && !characterAlreadyExists(book, dto.getFirstName(), dto.getLastName())) {
            BookCharacter bookCharacter = new BookCharacter();
            book.getBookCharacters().add(createOrCopyCharacter(bookCharacter, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Void> modifyCharacter(CharacterDto dto) {
        LOGGER.debug("Modifying bookCharacter: " + dto.toString());
        BookCharacter bookCharacter = getCharacterOrNull(dto.getId());
        if (bookCharacter != null) {
            characterRepository.saveAndFlush(createOrCopyCharacter(bookCharacter, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //########## HELPER STUFF ##########//

    private Book getBookOrNull(String id) {
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        return bookRepository.findById(parsedId).orElse(null);
    }

    private Section getSectionOrNull(String id) {
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        return sectionRepository.findById(parsedId).orElse(null);
    }

    private BookCharacter getCharacterOrNull(String id) {
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        return characterRepository.findById(parsedId).orElse(null);
    }
}
