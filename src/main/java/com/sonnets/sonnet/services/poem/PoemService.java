package com.sonnets.sonnet.services.poem;

import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import com.sonnets.sonnet.persistence.dtos.poetry.PoemDto;
import com.sonnets.sonnet.persistence.exceptions.ItemAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Confirmation;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.web.User;
import com.sonnets.sonnet.persistence.repositories.AuthorRepository;
import com.sonnets.sonnet.persistence.repositories.UserRepository;
import com.sonnets.sonnet.persistence.repositories.poem.PoemRepository;
import com.sonnets.sonnet.services.AbstractItemService;
import com.sonnets.sonnet.services.exceptions.ItemNotFoundException;
import com.sonnets.sonnet.services.exceptions.StoredProcedureQueryException;
import com.sonnets.sonnet.services.search.SearchQueryHandlerService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.FormatTools;
import tools.ParseSourceDetails;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Handles all CRUD for the poem repository.
 *
 * @author Josh Harkema
 */
@Service
public class PoemService implements AbstractItemService<Poem, PoemDto> {
    private static final Logger LOGGER = Logger.getLogger(PoemService.class);
    private static final ParseSourceDetails<Poem, PoemDto> parseSourceDetails = new ParseSourceDetails<>();
    private final PoemRepository poemRepository;
    private final AuthorRepository authorRepository;
    private final SearchQueryHandlerService searchQueryHandlerService;
    private final UserRepository userRepository;

    @Autowired
    public PoemService(PoemRepository poemRepository, AuthorRepository authorRepository,
                       SearchQueryHandlerService searchQueryHandlerService, UserRepository userRepository) {
        this.poemRepository = poemRepository;
        this.authorRepository = authorRepository;
        this.searchQueryHandlerService = searchQueryHandlerService;
        this.userRepository = userRepository;
    }

    /**
     * Helper copies data from a dto to a poem object.
     *
     * @param poem   the poem to copy the data onto.
     * @param dto    the dto to copy the data from.
     * @param author the author of the poem.
     * @return the poem with the new data copied.
     */
    private static Poem createOrUpdateFromDto(Poem poem, PoemDto dto, Author author) {
        poem.setAuthor(author);
        poem.setCategory(TypeConstants.POEM);
        poem.setText(Arrays.asList(FormatTools.parsePoemText(dto.getText())));
        if (dto.getTitle().isEmpty() || dto.getTitle() == null) {
            poem.setTitle(poem.getText().get(0));
        } else {
            poem.setTitle(dto.getTitle());
        }
        poem.setConfirmation(new Confirmation());
        poem = parseSourceDetails.parse(poem, dto);
        poem.setPeriod(dto.getPeriod());
        poem.setForm(dto.getForm());
        if (poem.getConfirmation().isPendingRevision()) { // Check to see if the poem is pending revision.
            poem.getConfirmation().setPendingRevision(false);
        }
        return poem;
    }

    /**
     * @param dto the new poem's data.
     * @return OK if the poem is added, CONFLICT if the poem exists.
     */
    public ResponseEntity<Void> add(PoemDto dto) {
        LOGGER.debug("Adding poem: " + dto.toString());
        try { // Check if poem already exists.
            similarPoemExists(dto);
        } catch (ItemAlreadyExistsException e) {
            LOGGER.error(e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        Poem poem = createOrUpdateFromDto(new Poem(), dto, author);
        poem = poemRepository.saveAndFlush(poem);

        updateCanConfirm(poem);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param id the id of the poem to delete.
     * @return OK if the poem is deleted.
     */
    public ResponseEntity<Void> delete(Long id) {
        LOGGER.debug("Deleting poem with id (ADMIN): " + id);
        Poem poem = poemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        if (poem.getConfirmation().isConfirmed()) {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }
        poemRepository.delete(poem);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param id        the db id of the poem to delete.
     * @param principal the principal of the user making the request.
     * @return OK if successful.
     */
    public ResponseEntity<Void> userDelete(Long id, Principal principal) {
        LOGGER.debug("Deleting poem with id (USER): " + id);
        Poem poem = poemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        if (poem.getCreatedBy().equals(principal.getName())) {
            poemRepository.delete(poem);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * @param id the id of the poem to get.
     * @return the poem or null if it isn't found.
     */
    @Transactional(readOnly = true)
    public Poem getById(Long id) {
        LOGGER.debug("Getting poem with id: " + id);
        return poemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * @param ids the ids of the poems to get.
     * @return a list of poems or null if the poems aren't found.
     */
    @Transactional(readOnly = true)
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
     * @return all poems as a list.
     */
    @Transactional(readOnly = true)
    public List<Poem> getAll() {
        LOGGER.debug("Returning all poems.");
        return poemRepository.findAll();
    }

    /**
     * @return a JSON array of only the most basic details of all poems in the db.
     */
    public String getAllSimple() {
        LOGGER.debug("Returning all poems as JSON.");
        return poemRepository.getAllPoemsSimple().orElseThrow(StoredProcedureQueryException::new);
    }

    /**
     * @param pageable the pageable object from the request.
     * @return a page of poems.
     */
    @Transactional(readOnly = true)
    public Page<Poem> getAllPaged(Pageable pageable) {
        LOGGER.debug("Returning all poems paged.");
        return poemRepository.findAll(pageable);
    }

    /**
     * @param principal the principal object from the request.
     * @return a list of all poems by a user.
     */
    @Transactional(readOnly = true)
    public List<Poem> getAllByUser(Principal principal) {
        LOGGER.debug("Returning all sonnets added by user: " + principal.getName());
        return poemRepository.findAllByCreatedBy(principal.getName()).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * @param dto the new data for the poem.
     * @return OK if the poem is modified; BAD_REQUEST if the poem / author doesn't exist.
     */
    public ResponseEntity<Void> modify(PoemDto dto) {
        LOGGER.debug("Modifying poem (ADMIN): " + dto.toString());
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        Poem poem = createOrUpdateFromDto(
                poemRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new), dto, author);
        poemRepository.saveAndFlush(poem);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param dto       the new data for the poem.
     * @param principal the user making the mods.
     * @return OK if the poem is modified; BAD_REQUEST if the poem / author doesn't exist or the user making the
     * request doesn't own the poem.
     */
    public ResponseEntity<Void> modifyUser(PoemDto dto, Principal principal) {
        LOGGER.debug("Modifying poem (USER): " + dto.toString());
        Poem poem = poemRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);
        if (poem.getConfirmation().isConfirmed()) {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        assert principal.getName().equals(poem.getCreatedBy());
        poemRepository.saveAndFlush(createOrUpdateFromDto(poem, dto, author));

        updateCanConfirm(poem);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Gets all poems by their form (i.e. 'sonnet').
     *
     * @param form the form to return.
     * @return a list of poems by form.
     */
    @Transactional(readOnly = true)
    public List<Poem> getAllByForm(final String form) {
        LOGGER.debug("Returning all sonnets with form: " + form);
        return poemRepository.findAllByForm(form).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * Get all poems by author's last name.
     *
     * @param lastName the last name of the author to get.
     * @return a list of poems by author's last name.
     */
    @Transactional(readOnly = true)
    public List<Poem> getAllByAuthorLastName(String lastName) {
        LOGGER.debug("Returning all poems by author: " + lastName);
        return poemRepository.findAllByAuthor_LastName(lastName).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * Gets all poems by form in a paged format.
     *
     * @param form     the form to return.
     * @param pageable the pageable object from the request.
     * @return a page of poems by form.
     */
    @Transactional(readOnly = true)
    public Page<Poem> getAllByFormPaged(final String form, Pageable pageable) {
        LOGGER.debug("Returning all sonnets in category paged: " + form);
        return poemRepository.findAllByForm(form, pageable).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * :todo: fix this.
     *
     * @param dto the dto of the poem with all its details.
     */
    private void similarPoemExists(PoemDto dto) {
        SearchDto searchDto = new SearchDto();
        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(ItemNotFoundException::new);
        searchDto.setAuthor(author);
        searchDto.setTitle(dto.getTitle());
        searchDto.setSearchPoems(true);
        searchQueryHandlerService.similarExistsPoem(searchDto);
    }

    /**
     * Sets canConfirm to true when a user has added their required amount of poems.
     *
     * @param poem the poem to add to the user's count.
     */
    private void updateCanConfirm(Poem poem) {
        User user = userRepository.findByUsername(poem.getCreatedBy());
        if (poemRepository.countAllByCreatedByAndConfirmation_PendingRevision(user.getUsername(), false)
                >= user.getRequiredSonnets()) {
            user.setCanConfirm(true);
            userRepository.saveAndFlush(user);
        }
    }
}
