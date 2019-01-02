package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.base.RejectDto;
import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import com.sonnets.sonnet.persistence.dtos.poetry.PoemDto;
import com.sonnets.sonnet.persistence.dtos.web.MessageDto;
import com.sonnets.sonnet.persistence.exceptions.ItemAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Confirmation;
import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.repositories.AuthorRepository;
import com.sonnets.sonnet.persistence.repositories.UserRepository;
import com.sonnets.sonnet.persistence.repositories.poem.PoemRepository;
import com.sonnets.sonnet.services.exceptions.ItemNotFoundException;
import com.sonnets.sonnet.services.search.SearchQueryHandlerService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Handles all CRUD for the poem repository.
 *
 * @author Josh Harkema
 */
@Service
public class PoemService {
    private static final Logger LOGGER = Logger.getLogger(PoemService.class);
    private final PoemRepository poemRepository;
    private final MessageService messageService;
    private final AuthorRepository authorRepository;
    private final UserRepository userRepository;
    private final SearchQueryHandlerService searchQueryHandlerService;

    @Autowired
    public PoemService(PoemRepository poemRepository, MessageService messageService, AuthorRepository authorRepository,
                       UserRepository userRepository, SearchQueryHandlerService searchQueryHandlerService) {
        this.poemRepository = poemRepository;
        this.messageService = messageService;
        this.authorRepository = authorRepository;
        this.userRepository = userRepository;
        this.searchQueryHandlerService = searchQueryHandlerService;
    }

    /**
     * Parses text input into an Array for database storage.
     *
     * @param text a string[] of the text.
     * @return an ArrayList of the string[].
     */
    private static List<String> parseText(String text) {
        List<String> strings = new ArrayList<>();
        String[] textArr = text.split("\\r?\\n");

        for (String s : textArr) {
            if (!s.isEmpty()) {
                strings.add(s.trim());
            }
        }
        return strings;
    }

    /**
     * Copy data from a dto to a poem object.
     *
     * @param poem   the poem to copy the data onto.
     * @param dto    the dto to copy the data from.
     * @param author the author of the poem.
     * @return the poem with the new data copied.
     */
    private static Poem createOrUpdateFromDto(Poem poem, PoemDto dto, Author author) {
        poem.setAuthor(author);
        poem.setCategory(Item.Type.POEM.getStringValue());
        poem.setText(parseText(dto.getText()));
        if (dto.getTitle().isEmpty() || dto.getTitle() == null) {
            poem.setTitle(poem.getText().get(0));
        } else {
            poem.setTitle(dto.getTitle());
        }
        poem.setConfirmation(new Confirmation());
        poem.setPublicationYear(dto.getPublicationYear());
        poem.setPublicationStmt(dto.getPublicationStmt());
        poem.setSourceDesc(dto.getSourceDesc());
        poem.setPeriod(dto.getPeriod());
        poem.setForm(dto.getForm());
        poem.setPageNumber(dto.getPageNumber());
        if (poem.getConfirmation().isPendingRevision()) { // Check to see if the poem is pending revision.
            poem.getConfirmation().setPendingRevision(false);
        }
        return poem;
    }

    /**
     * Add a poem to the db, checks to see if poem already exists.
     *
     * @param dto the new poem's data.
     * @return OK if the poem is added, CONFLICT if the poem exists.
     */
    public ResponseEntity<Void> add(PoemDto dto, Principal principal) {
        LOGGER.debug("Adding poem: " + dto.toString());
        // Check if poem already exists.
        try {
            similarPoemExists(dto);
        } catch (ItemAlreadyExistsException e) {
            LOGGER.error(e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Author author = authorRepository.findById(Long.parseLong(dto.getAuthorId()))
                .orElseThrow(ItemNotFoundException::new);
        Poem poem = createOrUpdateFromDto(new Poem(), dto, author);
        poemRepository.saveAndFlush(poem);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * Modify a poem. (ADMIN ONLY).
     *
     * @param dto the new data for the poem.
     * @return OK if the poem is modified; BAD_REQUEST if the poem / author doesn't exist.
     */
    public ResponseEntity<Void> modify(PoemDto dto) {
        LOGGER.debug("Modifying poem (ADMIN): " + dto.toString());
        Author author = authorRepository.findById(Long.parseLong(dto.getAuthorId()))
                .orElseThrow(ItemNotFoundException::new);
        Poem poem = createOrUpdateFromDto(
                poemRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new), dto, author);
        poemRepository.saveAndFlush(poem);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Modify a poem. (OWNER ONLY).
     *
     * @param dto       the new data for the poem.
     * @param principal the user making the mods.
     * @return OK if the poem is modified; BAD_REQUEST if the poem / author doesn't exist or the user making the
     * request doesn't own the poem.
     */
    public ResponseEntity<Void> modify(PoemDto dto, Principal principal) {
        LOGGER.debug("Modifying poem (USER): " + dto.toString());
        Poem poem = poemRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);
        if (poem.getConfirmation().isConfirmed()) {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }
        Author author = authorRepository.findById(Long.parseLong(dto.getAuthorId()))
                .orElseThrow(ItemNotFoundException::new);
        assert principal.getName().equals(poem.getCreatedBy());
        poemRepository.saveAndFlush(createOrUpdateFromDto(poem, dto, author));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Delete a poem. (ADMIN ONLY).
     *
     * @param id the id of the poem to delete.
     * @return OK if the poem is deleted.
     */
    public ResponseEntity<Void> deleteById(String id) {
        LOGGER.debug("Deleting poem with id (ADMIN): " + id);
        Poem poem = poemRepository.findById(Long.parseLong(id)).orElseThrow(ItemNotFoundException::new);
        if (poem.getConfirmation().isConfirmed()) {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }
        poemRepository.delete(poem);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Confirm a poem.
     *
     * @param id        the id of the poem to confirm.
     * @param principal the user making the request.
     * @return OK if the poem is confirmed.
     */
    public ResponseEntity<Void> confirm(String id, Principal principal) {
        LOGGER.debug("Confirming sonnet: " + id);
        Poem poem = poemRepository.findById(Long.parseLong(id)).orElseThrow(ItemNotFoundException::new);
        poem.getConfirmation().setConfirmed(true);
        poem.getConfirmation().setConfirmedBy(principal.getName());
        poem.getConfirmation().setConfirmedAt(new Timestamp(System.currentTimeMillis()));
        poem.getConfirmation().setPendingRevision(false);
        poemRepository.saveAndFlush(poem);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Reject a poem.
     *
     * @param rejectDto the dto with the rejection data.
     * @return OK if the poem is rejected.
     */
    public ResponseEntity<Void> reject(RejectDto rejectDto, Principal principal) {
        LOGGER.debug("Rejecting poem: " + rejectDto.getId());
        Poem poem = poemRepository.findById(rejectDto.getId()).orElseThrow(ItemNotFoundException::new);
        poem.getConfirmation().setConfirmed(false);
        poem.getConfirmation().setPendingRevision(true);
        poemRepository.saveAndFlush(poem);

        MessageDto messageDto = new MessageDto();
        messageDto.setUserFrom("Administrator");
        messageDto.setUserTo(poem.getCreatedBy());
        messageDto.setSubject("One of your sonnets has been rejected.");
        messageDto.setContent(rejectDto.getRejectMessage());
        messageService.sendAdminMessage(messageDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * THIS IS A STOPPER, IT ALWAYS RETURNS NULL.
     *
     * @param principal the user doing the confirmation.
     * @return an unconfirmed poem or nothing if there aren't any.
     */
    public Poem getPoemToConfirm(Principal principal) {
        LOGGER.debug("Returning first unconfirmed poem not submitted by: " + principal.getName());

        return null;
    }

    /**
     * Get a poem by id.
     *
     * @param id the id of the poem to get.
     * @return the poem or null if it isn't found.
     */
    public Poem getById(Long id) {
        LOGGER.debug("Getting poem with id: " + id);
        return poemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * Get a list of poems by a list of ids.
     *
     * @param ids the ids of the poems to get.
     * @return a list of poems or null if the poems aren't found.
     */
    public List<Poem> getByIds(Long[] ids) {
        LOGGER.debug("Getting poems with ids: " + Arrays.toString(ids));
        List<Poem> poems = new ArrayList<>();
        for (Long l : ids) {
            Poem poem = this.getById(l);
            if (poem != null) {
                poems.add(poem);
            }
        }
        return poems;
    }

    /**
     * @return two random sonnets.
     */
    public String getTwoRandomSonnets() {
        LOGGER.debug("Returning two random sonnets.");
        return poemRepository.getRandomPoem("SONNET");
    }

    /**
     * This query is for getting all poems via an SQL query rather than a slow ass db crawl. It places a \n
     * after each line in a poem. Runs async.
     *
     * @return all poems as a list.
     */
    public String getAll() {
        return poemRepository.getAllPoemsManual();
    }

    /**
     * This gets all poems in a paged format. Is not currently used.
     *
     * @param pageable the pageable object from the request.
     * @return a page of poems.
     */
    public Page<Poem> getAllPaged(Pageable pageable) {
        LOGGER.debug("Returning all poems paged.");
        return poemRepository.findAll(pageable);
    }

    /**
     * Gets all poems by their form.
     *
     * @param form the form to return.
     * @return a list of poems by form.
     */
    public List<Poem> getAllByForm(final String form) {
        LOGGER.debug("Returning all sonnets with form: " + form);
        return poemRepository.findAllByForm(form);
    }

    /**
     * Gets all poems by form in a paged format.
     *
     * @param form     the form to return.
     * @param pageable the pageable object from the request.
     * @return a page of poems by form.
     */
    public Page<Poem> getAllByFormPaged(final String form, Pageable pageable) {
        LOGGER.debug("Returning all sonnets in category paged: " + form);
        return poemRepository.findAllByForm(form, pageable);
    }

    /**
     * Get all poems created_by a given user.
     *
     * @param principal the principal object from the request.
     * @return a list of all poems by a user.
     */
    @Async
    public CompletableFuture<List> getAllByUser(Principal principal) {
        LOGGER.debug("Returning all sonnets added by user: " + principal.getName());
        return poemRepository.getPoemsByUser(principal.getName()).thenApply(poems ->
                poems.orElseThrow(ItemNotFoundException::new));
    }

    /**
     * Get all poems by author's last name.
     *
     * @param lastName the last name of the author to get.
     * @return a list of poems by author's last name.
     */
    public List<Poem> getAllByAuthorLastName(String lastName) {
        LOGGER.debug("Returning all poems by author: " + lastName);
        return poemRepository.findAllByAuthor_LastName(lastName).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * Deletes a poem from the database.
     *
     * @param id        the db id of the poem to delete.
     * @param principal the principal of the user making the request.
     * @return OK if successful.
     */
    public ResponseEntity<Void> deleteById(String id, Principal principal) {
        LOGGER.debug("Deleting poem with id (USER): " + id);
        Poem poem = poemRepository.findById(Long.parseLong(id)).orElseThrow(ItemNotFoundException::new);
        if (poem.getCreatedBy().equals(principal.getName())) {
            poemRepository.delete(poem);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Checks if a similar poem already exists in the database.
     *
     * @param dto the dto of the poem with all its details.
     */
    private void similarPoemExists(PoemDto dto) {
        SearchDto searchDto = new SearchDto();
        Author author = authorRepository.findById(Long.parseLong(dto.getAuthorId()))
                .orElseThrow(ItemNotFoundException::new);
        searchDto.setAuthor(author);
        searchDto.setTitle(dto.getTitle());
        searchDto.setSearchPoems(true);
        searchQueryHandlerService.similarExistsPoem(searchDto);
    }
}
