package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.base.AuthorDto;
import com.sonnets.sonnet.persistence.exceptions.AuthorAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.repositories.AuthorRepository;
import com.sonnets.sonnet.services.helpers.GetObjectOrThrowNullPointer;
import com.sonnets.sonnet.tools.ParseParam;
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
    private final GetObjectOrThrowNullPointer getObjectOrThrowNull;

    @Autowired
    public AuthorService(AuthorRepository authorRepository, GetObjectOrThrowNullPointer getObjectOrThrowNull) {
        this.authorRepository = authorRepository;
        this.getObjectOrThrowNull = getObjectOrThrowNull;
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
     * Add an author to the database.
     *
     * @param authorDto the dto with the new data.
     * @return OK if the author is added.
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

    /**
     * Modify an author.
     *
     * @param authorDto the dto with the new data.
     * @return OK if the author is modified.
     */
    public ResponseEntity<Void> modify(AuthorDto authorDto) {
        LOGGER.debug("Modifying author: " + authorDto.toString());
        Author author = getObjectOrThrowNull.author(authorDto.getId().toString());
        authorRepository.saveAndFlush(createOrCopyAuthor(author, authorDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Delete an author from the database.
     *
     * @param id the id of the author to delete.
     * @return OK if the author is removed.
     */
    public ResponseEntity<Void> delete(String id) {
        LOGGER.debug("Deleting author with id: " + id);
        Author author = getObjectOrThrowNull.author(id);
        authorRepository.delete(author);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get an specific author.
     *
     * @param id the db id of the author to get.
     * @return the author.
     */
    public Author get(String id) {
        LOGGER.debug("Looking for author with id: " + id);
        return getObjectOrThrowNull.author(id);
    }

    /**
     * Get an author by last name. Return null if nothing is found.
     *
     * @param lastName the last name to look for.
     * @return the author, if found, or null.
     */
    public Author getByLastName(String lastName) {
        LOGGER.debug("Looking for author with last name: " + lastName);
        lastName = ParseParam.parse(lastName);
        Optional<Author> author = authorRepository.findByLastName(lastName);
        return author.orElse(null);
    }
}
