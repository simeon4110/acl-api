package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.AuthorDto;
import com.sonnets.sonnet.persistence.exceptions.AuthorAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.repositories.AuthorRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Deals with all author related functions.
 *
 * @author Josh Harkema.
 */
@Service
public class AuthorService {
    private static final Logger LOGGER = Logger.getLogger(AuthorService.class);
    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    /**
     * Copy's details from dto to Author object.
     *
     * @param author    the author object to copy the dto to.
     * @param authorDto the dto with the new information.
     * @return an Author object with the info from the DTO.
     */
    private static Author createOrCopyAuthor(Author author, AuthorDto authorDto) {
        author.setFirstName(authorDto.getFirstName());
        author.setMiddleName(authorDto.getMiddleName());
        author.setLastName(authorDto.getLastName());

        return author;
    }

    /**
     * Add a new Author to the db.
     *
     * @param authorDto an valid AuthorDto object.
     * @return throws exception if exists, returns HttpStatus.OK if good.
     */
    public ResponseEntity<Void> add(AuthorDto authorDto) {
        LOGGER.debug("Creating new author: " + authorDto.toString());

        // Ensure author doesn't already exist.
        if (authorRepository.findByLastNameAndFirstName(authorDto.getLastName(), authorDto.getFirstName()) != null) {
            throw new AuthorAlreadyExistsException("Author: " + authorDto.getFirstName() + " " +
                    authorDto.getLastName() + " already exists.");
        }

        authorRepository.saveAndFlush(createOrCopyAuthor(new Author(), authorDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> modify(AuthorDto authorDto) {
        LOGGER.debug("Modifying author: " + authorDto.toString());

        Optional<Author> optionalAuthor = authorRepository.findById(authorDto.getId());
        if (optionalAuthor.isPresent()) {
            Author author = optionalAuthor.get();
            authorRepository.saveAndFlush(createOrCopyAuthor(author, authorDto));
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Get an author by id.
    public Author get(String id) {
        LOGGER.debug("Looking for author with id: " + id);

        Long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            throw new NumberFormatException(e.getMessage());
        }

        Optional<Author> author = authorRepository.findById(parsedId);
        return author.orElse(null);
    }

    // Get an author by last name.
    public Author getByLastName(String lastName) {
        LOGGER.debug("Looking for author with last name: " + lastName);
        Optional<Author> author = authorRepository.findByLastName(lastName);
        return author.orElse(null);
    }
}
