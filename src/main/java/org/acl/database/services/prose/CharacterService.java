package org.acl.database.services.prose;

import org.acl.database.persistence.dtos.prose.CharacterDto;
import org.acl.database.persistence.exceptions.AuthorAlreadyExistsException;
import org.acl.database.persistence.models.base.Book;
import org.acl.database.persistence.models.prose.BookCharacter;
import org.acl.database.persistence.repositories.BookCharacterRepository;
import org.acl.database.persistence.repositories.BookRepository;
import org.acl.database.services.exceptions.ItemNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * All BookCharacter related methods are here.
 *
 * @author Josh Harkema.
 */
@Service
public class CharacterService {
    private static final Logger LOGGER = Logger.getLogger(CharacterService.class);
    private final BookCharacterRepository bookCharacterRepository;
    private final BookRepository bookRepository;

    @Autowired
    public CharacterService(BookCharacterRepository bookCharacterRepository, BookRepository bookRepository) {
        this.bookCharacterRepository = bookCharacterRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Helper method for copying data from a dto onto a BookCharacter object.
     *
     * @param bookCharacter the BookCharacter object to copy the data onto.
     * @param dto           the dto with to data.
     * @return the BookCharacter with the new data copied onto it.
     */
    private static BookCharacter createOrCopyCharacter(BookCharacter bookCharacter, CharacterDto dto) {
        bookCharacter.setFirstName((dto.getFirstName() == null) ? "" : dto.getFirstName());
        bookCharacter.setLastName((dto.getLastName() == null) ? "" : dto.getLastName());
        bookCharacter.setGender((dto.getGender() == null) ? "" : dto.getGender());
        bookCharacter.setDescription((dto.getDescription() == null) ? "" : dto.getDescription());
        bookCharacter.setDialog((bookCharacter.getDialog() == null) ? new HashSet<>() : bookCharacter.getDialog());
        return bookCharacter;
    }

    /**
     * Checks to see if a Book already includes an character.
     *
     * @param book      the book to check.
     * @param firstName the character's first name.
     * @param lastName  the character's last name.
     * @return false if the character does not exist.
     */
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

    /**
     * Add a BookCharacter to the db.
     *
     * @param dto the new data.
     * @return OK if the BookCharacter is added.
     */
    public ResponseEntity<Void> add(CharacterDto dto) {
        LOGGER.debug("Adding character: " + dto.toString());
        Book book = bookRepository.findById(dto.getBookId()).orElseThrow(ItemNotFoundException::new);
        if (characterAlreadyExists(book, dto.getFirstName(), dto.getLastName())) {
            throw new AuthorAlreadyExistsException(
                    String.format("Author: %s %s already exists", dto.getFirstName(), dto.getLastName())
            );
        }
        BookCharacter bookCharacter = new BookCharacter();
        book.getBookCharacters().add(createOrCopyCharacter(bookCharacter, dto));
        bookCharacterRepository.saveAndFlush(bookCharacter);
        bookRepository.save(book);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Modify a BookCharacter.
     *
     * @param dto the new data.
     * @return OK if the BookCharacter is modified.
     */
    public ResponseEntity<Void> modify(CharacterDto dto) {
        LOGGER.debug("Modifying bookCharacter: " + dto.toString());
        BookCharacter bookCharacter = getCharacterOrThrowNotFound(dto.getId());
        CompletableFuture.runAsync(() -> bookCharacterRepository.save(createOrCopyCharacter(bookCharacter, dto)));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Delete a BookCharacter.
     *
     * @param id the id of the BookCharacter to delete.
     * @return OK if the BookCharacter is deleted.
     */
    public ResponseEntity<Void> delete(String id) {
        LOGGER.debug("Deleting bookCharacter: " + id);
        BookCharacter character = getCharacterOrThrowNotFound(id);
        bookCharacterRepository.delete(character);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public BookCharacter save(BookCharacter bookCharacter) {
        LOGGER.debug("Saving book character: " + bookCharacter.toString());
        return bookCharacterRepository.save(bookCharacter);
    }

    public BookCharacter getCharacterOrThrowNotFound(String id) {
        long parsedId = Long.parseLong(id);
        return bookCharacterRepository.findById(parsedId).orElseThrow(ItemNotFoundException::new);
    }

    public BookCharacter getCharacterOrThrowNotFound(Long id) {
        return bookCharacterRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }
}
