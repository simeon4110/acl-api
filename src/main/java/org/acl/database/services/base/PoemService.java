package org.acl.database.services.base;

import org.acl.database.config.LuceneConfig;
import org.acl.database.persistence.dtos.base.PoemDto;
import org.acl.database.persistence.dtos.base.PoemOutDto;
import org.acl.database.persistence.models.TypeConstants;
import org.acl.database.persistence.models.base.Author;
import org.acl.database.persistence.models.base.Confirmation;
import org.acl.database.persistence.models.base.Poem;
import org.acl.database.persistence.models.web.User;
import org.acl.database.persistence.repositories.AuthorRepository;
import org.acl.database.persistence.repositories.UserRepository;
import org.acl.database.persistence.repositories.poem.PoemRepository;
import org.acl.database.search.SearchRepository;
import org.acl.database.services.AbstractItemService;
import org.acl.database.services.exceptions.ItemNotFoundException;
import org.acl.database.services.exceptions.StoredProcedureQueryException;
import org.acl.database.services.search.SearchConstants;
import org.acl.database.services.search.SearchQueryHandlerService;
import org.acl.database.tools.ParseSourceDetails;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Handles all CRUD for the poem repository.
 *
 * @author Josh Harkema
 */
@Service
public class PoemService implements AbstractItemService<Poem, PoemDto, PoemOutDto> {
    private static final Logger LOGGER = Logger.getLogger(PoemService.class);
    private static final ParseSourceDetails<Poem, PoemDto> parseSourceDetails = new ParseSourceDetails<>();
    private final PoemRepository poemRepository;
    private final AuthorRepository authorRepository;
    private final UserRepository userRepository;

    @Autowired
    public PoemService(PoemRepository poemRepository, AuthorRepository authorRepository,
                       UserRepository userRepository) {
        this.poemRepository = poemRepository;
        this.authorRepository = authorRepository;
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
     * Converts a string split up with \n into a clean List of strings.
     *
     * @param input the input string to parse.
     * @return a parsed List.
     */
    private static List<String> parsePoemText(final String input) {
        return Arrays.stream(input.split("\n"))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toUnmodifiableList());
    }

    // :todo: add this method to AbstractItemService
    private static void addNewSearchDocument(final Poem poem) {
        LOGGER.debug("Updating poem's search document...");
        Document document = SearchRepository.parseCommonFields(new Document(), poem);
        document.add(new TextField(SearchConstants.POEM_FORM, poem.getForm(), Field.Store.YES));
        // :todo: this requires its own custom field.
        document.add(new TextField(SearchConstants.TOPIC_MODEL, String.valueOf(poem.getTopicModel()),
                Field.Store.YES));
        document.add(LuceneConfig.getTextField(String.join(" ", poem.getText())));
        SearchRepository.addDocument(document, TypeConstants.POEM);
        LOGGER.debug("Poem's search document updated successfully.");
    }

    @Override
    @Transactional
    public ResponseEntity<Void> add(PoemDto dto) {
        LOGGER.debug("Adding poem: " + dto.toString());
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
        if (SearchQueryHandlerService.similarPoemExists(dto.getTitle(), author.getLastName())) {
            Poem poem = createOrUpdateFromDto(new Poem(), dto, author);
            addNewSearchDocument(poemRepository.saveAndFlush(poem));
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            LOGGER.error("A poem with the same title is already in the database!");
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Void> delete(Long id, Principal principal) {
        LOGGER.debug("Deleting poem with id (ADMIN): " + id);
        Poem poem = poemRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        if (poem.getConfirmation().isConfirmed()) {
            return new ResponseEntity<>(HttpStatus.LOCKED);
        }

        if (principal.getName().equals(poem.getCreatedBy()) ||
                userRepository.findByUsername(principal.getName()).getAdmin()) {
            poemRepository.delete(poem);
            SearchRepository.deleteDocument(id.toString(), TypeConstants.POEM);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
    public List<PoemOutDto> getAll() {
        LOGGER.debug("Returning all poems. NOAUTH");
        return poemRepository.getAllPublicDomain();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PoemOutDto> authedUserGetAll() {
        LOGGER.debug("Returning all poems. AUTH");
        return poemRepository.getAll();
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
    public ResponseEntity<Void> modify(PoemDto dto, Principal principal) {
        LOGGER.debug("Modifying poem (ADMIN): " + dto.toString());
        Poem poem = poemRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);

        if (principal.getName().equals(poem.getCreatedBy()) ||
                userRepository.findByUsername(principal.getName()).getAdmin()) {
            Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
            poem = createOrUpdateFromDto(poem, dto, author);
            this.updateCanConfirm(poem);
            SearchRepository.updatePoem(poemRepository.saveAndFlush(poem));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
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
