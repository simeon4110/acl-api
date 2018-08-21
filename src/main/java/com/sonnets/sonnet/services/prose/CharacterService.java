package com.sonnets.sonnet.services.prose;

import com.sonnets.sonnet.persistence.dtos.prose.CharacterDto;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.BookCharacter;
import com.sonnets.sonnet.persistence.repositories.CharacterRepository;
import com.sonnets.sonnet.services.helpers.GetObjectOrNull;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CharacterService {
    private static final Logger LOGGER = Logger.getLogger(CharacterService.class);
    private final GetObjectOrNull getObjectOrNull;
    private final CharacterRepository characterRepository;

    @Autowired
    public CharacterService(GetObjectOrNull getObjectOrNull, CharacterRepository characterRepository) {
        this.getObjectOrNull = getObjectOrNull;
        this.characterRepository = characterRepository;
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

    public ResponseEntity<Void> add(CharacterDto dto) {
        LOGGER.debug("Adding character: " + dto.toString());
        Book book = getObjectOrNull.book(dto.getBookId());
        if (book != null && !characterAlreadyExists(book, dto.getFirstName(), dto.getLastName())) {
            BookCharacter bookCharacter = new BookCharacter();
            book.getBookCharacters().add(createOrCopyCharacter(bookCharacter, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<Void> modify(CharacterDto dto) {
        LOGGER.debug("Modifying bookCharacter: " + dto.toString());
        BookCharacter bookCharacter = getObjectOrNull.character(dto.getId());
        if (bookCharacter != null) {
            characterRepository.saveAndFlush(createOrCopyCharacter(bookCharacter, dto));
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> delete(String id) {
        LOGGER.debug("Deleting bookCharacter: " + id);
        BookCharacter character = getObjectOrNull.character(id);
        if (character != null) {
            characterRepository.delete(character);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
