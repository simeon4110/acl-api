package com.sonnets.sonnet.services.base;

import com.sonnets.sonnet.config.LuceneConfig;
import com.sonnets.sonnet.persistence.dtos.base.PoemDto;
import com.sonnets.sonnet.persistence.dtos.base.SearchDto;
import com.sonnets.sonnet.persistence.exceptions.ItemAlreadyExistsException;
import com.sonnets.sonnet.persistence.models.TypeConstants;
import com.sonnets.sonnet.persistence.models.base.Author;
import com.sonnets.sonnet.persistence.models.base.Confirmation;
import com.sonnets.sonnet.persistence.models.base.Poem;
import com.sonnets.sonnet.persistence.models.web.User;
import com.sonnets.sonnet.persistence.repositories.AuthorRepository;
import com.sonnets.sonnet.persistence.repositories.UserRepository;
import com.sonnets.sonnet.persistence.repositories.poem.PoemRepository;
import com.sonnets.sonnet.services.AbstractItemService;
import com.sonnets.sonnet.services.exceptions.ItemNotFoundException;
import com.sonnets.sonnet.services.exceptions.StoredProcedureQueryException;
import com.sonnets.sonnet.services.search.SearchCRUDService;
import com.sonnets.sonnet.services.search.SearchConstants;
import com.sonnets.sonnet.services.search.SearchQueryHandlerService;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.ParseSourceDetails;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.sonnets.sonnet.services.search.SearchCRUDService.parseCommonFields;

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
        LOGGER.debug("PROCESSING POEM: " + dto.toString());
        if (poem.getId() != null) {
            poem.setText(parsePoemText(dto.getText()));
            poem.setTitle(dto.getTitle());
            poem = parseSourceDetails.parse(poem, dto);
            poem.setPeriod(dto.getPeriod());
            poem.setForm(dto.getForm());
            if (poem.getConfirmation().isPendingRevision()) { // Check to see if the poem is pending revision.
                poem.getConfirmation().setPendingRevision(false);
            }
        } else {
            poem.setAuthor(author);
            poem.setCategory(TypeConstants.POEM);
            poem.setText(parsePoemText(dto.getText()));
            poem = parseSourceDetails.parse(poem, dto);
            poem.setPeriod(dto.getPeriod());
            poem.setForm(dto.getForm());
            poem.setConfirmation(new Confirmation());
            if (dto.getTitle().isEmpty() || dto.getTitle() == null) {
                poem.setTitle(poem.getText().get(0));
            } else {
                poem.setTitle(dto.getTitle());
            }
        }
        return poem;
    }

    /**
     * Converts a string split up with \n or \\n into a clean array of strings.
     *
     * @param input the input string to parse.
     * @return a parsed ArrayList.
     */
    private static ArrayList<String> parsePoemText(final String input) {
        ArrayList<String> arrayOut = new ArrayList<>();
        for (String s : input.split("\n")) {
            if (!s.equals("")) {
                arrayOut.add(s.trim());
            }
        }
        return arrayOut;
    }

    // :todo: add this method to AbstractItemService
    private static void addNewSearchDocument(final Poem poem) {
        LOGGER.debug("Updating poem's search document...");
        Document document = parseCommonFields(new Document(), poem);
        document.add(new TextField(SearchConstants.POEM_FORM, poem.getForm(), Field.Store.YES));
        // :todo: this requires its own custom field.
        document.add(new TextField(SearchConstants.TOPIC_MODEL, String.valueOf(poem.getTopicModel()),
                Field.Store.YES));
        document.add(LuceneConfig.getTextField(String.join(" ", poem.getText())));
        SearchCRUDService.addDocument(document, TypeConstants.POEM);
        LOGGER.debug("Poem's search document updated successfully.");
    }

    @Override
    @Transactional
    public ResponseEntity<Void> add(PoemDto dto) {
        LOGGER.debug("Adding poem: " + dto.toString());
        try { // Check if poem already exists.
            similarPoemExists(dto);
        } catch (ItemAlreadyExistsException | ParseException e) {
            LOGGER.error(e);
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        Poem poem = createOrUpdateFromDto(new Poem(), dto, author);
        poem = poemRepository.saveAndFlush(poem);
        addNewSearchDocument(poem);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> delete(Long id) {
        LOGGER.debug("Deleting poem with id (ADMIN): " + id);
        Poem poem = poemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        if (poem.getConfirmation().isConfirmed()) {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }

        poemRepository.delete(poem);
        SearchCRUDService.deleteDocument(id.toString(), TypeConstants.POEM);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> userDelete(Long id, Principal principal) {
        LOGGER.debug("Deleting poem with id (USER): " + id);
        Poem poem = poemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        if (poem.getConfirmation().isConfirmed()) {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }

        if (poem.getCreatedBy().equals(principal.getName())) {
            poemRepository.delete(poem);
            SearchCRUDService.deleteDocument(id.toString(), TypeConstants.POEM);
            return new ResponseEntity<>(HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Override
    @Transactional(readOnly = true)
    public Poem getById(Long id) {
        LOGGER.debug("Getting poem with id: " + id);
        return poemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    @Override
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

    @Override
    @Transactional(readOnly = true)
    public List<Poem> getAll() {
        LOGGER.debug("Returning all poems. NOAUTH");
        return poemRepository.findAllByIsPublicDomain(true).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Poem> authedUserGetAll() {
        LOGGER.debug("Returning all poems. AUTH");
        return poemRepository.findAll();
    }

    @Override
    @Transactional
    public String getAllSimple() {
        LOGGER.debug("Returning all poems as JSON. NOAUTH");
        return poemRepository.getAllPoemsSimplePDO().orElseThrow(StoredProcedureQueryException::new);
    }

    @Override
    @Transactional
    public String authedUserGetAllSimple() {
        LOGGER.debug("Returning all poems as JSON. AUTH");
        return poemRepository.getAllPoemsSimple().orElseThrow(StoredProcedureQueryException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Poem> getAllPaged(Pageable pageable) {
        LOGGER.debug("Returning all poems paged.");
        return poemRepository.findAllByIsPublicDomain(true, pageable)
                .orElseThrow(ItemNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Poem> getAllByUser(Principal principal) {
        LOGGER.debug("Returning all sonnets added by user: " + principal.getName());
        return poemRepository.findAllByCreatedBy(principal.getName()).orElseThrow(ItemNotFoundException::new);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> modify(PoemDto dto) {
        LOGGER.debug("Modifying poem (ADMIN): " + dto.toString());
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        Poem poem = poemRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);
        poem = createOrUpdateFromDto(poem, dto, author);
        poem = poemRepository.saveAndFlush(poem);
        this.updateCanConfirm(poem);
        SearchCRUDService.updatePoem(poem);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<Void> modifyUser(PoemDto dto, Principal principal) {
        LOGGER.debug("Modifying poem (USER): " + dto.toString());
        Poem poem = poemRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);

        if (poem.getConfirmation().isConfirmed()) {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }

        if (!principal.getName().equals(poem.getCreatedBy())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        poem = poemRepository.saveAndFlush(createOrUpdateFromDto(poem, dto, author));
        this.updateCanConfirm(poem);
        SearchCRUDService.updatePoem(poem);
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
     * @return two poems selected at random.
     */
    @Transactional
    public String getTwoRandomPoems() {
        LOGGER.debug("Returning two random poems.");
        return poemRepository.getTwoRandomPoems().orElseThrow(StoredProcedureQueryException::new);
    }

    /**
     * :todo: fix this.
     *
     * @param dto the dto of the poem with all its details.
     */
    private void similarPoemExists(PoemDto dto) throws ParseException {
        LOGGER.debug("Checking if similar poem exists: " + dto.toString());
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
        if (poemRepository.countAllByCreatedByAndConfirmation_PendingRevision(
                user.getUsername(), true) == 0) {
            user.setCanConfirm(true);
            userRepository.saveAndFlush(user);
        }
    }
}
