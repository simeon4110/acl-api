package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.base.RejectDto;
import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import com.sonnets.sonnet.persistence.dtos.poetry.PoemDto;
import com.sonnets.sonnet.persistence.dtos.web.MessageDto;
import com.sonnets.sonnet.persistence.exceptions.ItemAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Confirmation;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.repositories.poem.PoemRepository;
import com.sonnets.sonnet.security.UserDetailsServiceImpl;
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
    private final AuthorService authorService;
    private final UserDetailsServiceImpl userDetailsService;
    private final SearchQueryHandlerService searchQueryHandlerService;

    @Autowired
    public PoemService(PoemRepository poemRepository, MessageService messageService, AuthorService authorService,
                       UserDetailsServiceImpl userDetailsService, SearchQueryHandlerService searchQueryHandlerService) {
        this.poemRepository = poemRepository;
        this.messageService = messageService;
        this.authorService = authorService;
        this.userDetailsService = userDetailsService;
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
            strings.add(s.trim());
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
        poem.setCategory("POEM");
        poem.setTitle(dto.getTitle());
        poem.setConfirmation(new Confirmation());
        poem.setPublicationYear(dto.getPublicationYear());
        poem.setPublicationStmt(dto.getPublicationStmt());
        poem.setSourceDesc(dto.getSourceDesc());
        poem.setPeriod(dto.getPeriod());
        poem.setForm(dto.getForm());
        poem.setText(parseText(dto.getText()));
        poem.setPageNumber(dto.getPageNumber());

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
            similarExistsPoem(dto);
        } catch (ItemAlreadyExistsException e) {
            LOGGER.error(e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Author author = authorService.getAuthorOrThrowNotFound(dto.getAuthorId());
        Poem poem = createOrUpdateFromDto(new Poem(), dto, author);
        poemRepository.saveAndFlush(poem);
        this.getCountAndUpdate(principal.getName());

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
        Author author = authorService.getAuthorOrThrowNotFound(dto.getAuthorId());
        Poem poem = createOrUpdateFromDto(getPoemOrThrowNotFound(dto.getId()), dto, author);
        poemRepository.saveAndFlush(poem);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Modify a poem. (OWNER ALL).
     *
     * @param dto       the new data for the poem.
     * @param principal the user making the mods.
     * @return OK if the poem is modified; BAD_REQUEST if the poem / author doesn't exist or the user making the
     * request doesn't own the poem.
     */
    public ResponseEntity<Void> modify(PoemDto dto, Principal principal) {
        LOGGER.debug("Modifying poem (USER): " + dto.toString());
        Poem poem = getPoemOrThrowNotFound(dto.getId().toString());
        Author author = authorService.getAuthorOrThrowNotFound(dto.getAuthorId());
        assert poem != null;
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
        Poem poem = getPoemOrThrowNotFound(id);
        assert poem != null;
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
        Poem poem = getPoemOrThrowNotFound(id);
        assert poem != null;
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
    public ResponseEntity<Void> reject(RejectDto rejectDto) {
        LOGGER.debug("Rejecting poem: " + rejectDto.getId());
        Poem poem = getPoemOrThrowNotFound(rejectDto.getId());
        assert poem != null;
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
     * Get an unconfirmed poem.
     *
     * @param principal the user doing the confirmation.
     * @return an unconfirmed poem or nothing if there aren't any.
     */
    public Poem getPoemToConfirm(Principal principal) {
        LOGGER.debug("Returning first unconfirmed poem not submitted by: " + principal.getName());
        return poemRepository
                .findFirstByConfirmation_ConfirmedAndConfirmation_PendingRevisionAndCreatedByNot(
                        false, false, principal.getName()
                );
    }

    /**
     * Get a poem by id.
     *
     * @param id the id of the poem to get.
     * @return the poem or null if it isn't found.
     */
    public Poem getById(String id) {
        LOGGER.debug("Getting poem with id: " + id);
        return getPoemOrThrowNotFound(id);
    }

    /**
     * Get a list of poems by a list of ids.
     *
     * @param ids the ids of the poems to get.
     * @return a list of poems or null if the poems aren't found.
     */
    public List<Poem> getByIds(String[] ids) {
        LOGGER.debug("Getting poems with ids: " + Arrays.toString(ids));
        List<Poem> poems = new ArrayList<>();
        for (String s : ids) {
            Poem poem = this.getById(s);
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

    public Page<Poem> getAllPaged(Pageable pageable) {
        LOGGER.debug("Returning all poems paged.");
        return poemRepository.findAll(pageable);
    }

    public List<Poem> getAllByForm(final String form) {
        LOGGER.debug("Returning all sonnets with form: " + form);
        return poemRepository.findAllByForm(form);
    }

    public Page<Poem> getAllByFormPaged(final String form, Pageable pageable) {
        LOGGER.debug("Returning all sonnets in category paged: " + form);
        return poemRepository.findAllByForm(form, pageable);
    }

    @Async
    public CompletableFuture<List> getAllByUser(Principal principal) {
        LOGGER.debug("Returning all sonnets added by user: " + principal.getName());
        return poemRepository.getPoemsByUser(principal.getName()).thenApply(poems ->
                poems.orElseThrow(ItemNotFoundException::new));
    }

    public List<Poem> getAllByAuthorLastName(String lastName) {
        LOGGER.debug("Returning all poems by author: " + lastName);
        return poemRepository.findAllByAuthor_LastName(lastName).orElseThrow(ItemNotFoundException::new);
    }

    // User - delete poem.
    public ResponseEntity<Void> deleteById(String id, Principal principal) {
        LOGGER.debug("Deleting poem with id (USER): " + id);
        Poem poem = getPoemOrThrowNotFound(id);
        if (poem != null && poem.getCreatedBy().equals(principal.getName())) {
            poemRepository.delete(poem);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public Poem getUnprocessed() {
        LOGGER.debug("Returning an unprocessed poem");
        return poemRepository.findFirstByProcessed(false);
    }

    public void save(Poem poem) {
        LOGGER.debug("Saving poem: " + poem.toString());
        poemRepository.saveAndFlush(poem);
    }

    private Poem getPoemOrThrowNotFound(String id) {
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(e);
            return null;
        }
        return poemRepository.findById(parsedId).orElseThrow(ItemNotFoundException::new);
    }

    private Poem getPoemOrThrowNotFound(Long id) {
        return poemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    private void similarExistsPoem(PoemDto dto) {
        SearchDto searchDto = new SearchDto();
        Author author = authorService.getAuthorOrThrowNotFound(dto.getAuthorId());
        searchDto.setAuthor(author);
        searchDto.setTitle(dto.getTitle());
        searchDto.setSearchPoems(true);
        searchQueryHandlerService.similarExistsPoem(searchDto);
    }

    private void getCountAndUpdate(String username) {
        int count = poemRepository.countAllByCreatedBy(username);
        userDetailsService.updateConfirmed(username, count);
    }
}
