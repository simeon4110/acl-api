package org.acl.database.services.base;

import org.acl.database.persistence.dtos.base.AuthorDto;
import org.acl.database.persistence.models.base.Author;
import org.acl.database.persistence.repositories.AuthorRepository;
import org.acl.database.services.exceptions.ItemNotFoundException;
import org.acl.database.services.exceptions.NoResultsException;
import org.acl.database.tools.FormatTools;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
     * Add an author to the database.
     *
     * @param authorDto the dto with the new data.
     * @return OK if the author is added.
     */
    public ResponseEntity<Void> add(AuthorDto authorDto) {
        LOGGER.debug("Creating new author: " + authorDto.toString());

        // Ensure author doesn't already exist.
        if (authorRepository.findByLastNameAndFirstName(authorDto.getLastName(), authorDto.getFirstName()) != null) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        authorRepository.saveAndFlush(createOrCopyAuthor(new Author(), authorDto));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Modify an author.
     *
     * @param authorDto the dto with the new data.
     * @return OK if the author is modified.
     */
    public ResponseEntity<Void> modify(AuthorDto authorDto) {
        LOGGER.debug("Modifying author: " + authorDto.toString());
        Author author = authorRepository.findById(authorDto.getId()).orElseThrow(ItemNotFoundException::new);
        authorRepository.saveAndFlush(createOrCopyAuthor(author, authorDto));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Delete an author from the database.
     *
     * @param id the id of the author to delete.
     * @return OK if the author is removed.
     */
    public ResponseEntity<Void> delete(Long id) {
        LOGGER.debug("Deleting author with id: " + id);
        Author author = authorRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        authorRepository.delete(author);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Get an specific author.
     *
     * @param id the db id of the author to get.
     * @return the author.
     */
    public Author get(Long id) {
        LOGGER.debug("Looking for author with id: " + id);
        return authorRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * Get an author by last name. Return 404 if nothing is found.
     *
     * @param lastName the last name to look for.
     * @return the author, if found, or null.
     */
    public Author getByLastName(String lastName) {
        LOGGER.debug("Looking for author with last name: " + lastName);
        lastName = FormatTools.parseParam(lastName);
        return authorRepository.findByLastName(lastName).orElseThrow(NoResultsException::new);
    }
}
