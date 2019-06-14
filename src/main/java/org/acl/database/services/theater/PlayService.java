package org.acl.database.services.theater;

import org.acl.database.persistence.dtos.theater.PlayDto;
import org.acl.database.persistence.models.TypeConstants;
import org.acl.database.persistence.models.base.Author;
import org.acl.database.persistence.models.theater.Play;
import org.acl.database.persistence.repositories.AuthorRepository;
import org.acl.database.persistence.repositories.theater.PlayRepository;
import org.acl.database.search.SearchRepository;
import org.acl.database.security.UserDetailsServiceImpl;
import org.acl.database.services.exceptions.ItemNotFoundException;
import org.acl.database.services.exceptions.PlayAlreadyExistsException;
import org.acl.database.tools.ParseSourceDetails;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Collections;

/**
 * Service handles CRUD for top-level Play objects.
 *
 * @author Josh Harkema
 */
@Service
public class PlayService {
    private static final Logger LOGGER = Logger.getLogger(PlayService.class);
    private static final ParseSourceDetails<Play, PlayDto> parseSourceDetails = new ParseSourceDetails<>();
    private final PlayRepository playRepository;
    private final AuthorRepository authorRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public PlayService(PlayRepository playRepository, AuthorRepository authorRepository,
                       UserDetailsServiceImpl userDetailsService) {
        this.playRepository = playRepository;
        this.authorRepository = authorRepository;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Copy an PlayDto onto a Play Object.
     *
     * @param play   the play to copy the dto onto.
     * @param author the author defined in the dto.
     * @param dto    the dto with the data to copy.
     * @return a Play object with the data from the dto added.
     */
    private static Play createOrUpdateFromDto(Play play, Author author, PlayDto dto) {
        play.setAuthor(author);
        play.setTitle(dto.getTitle());
        play = parseSourceDetails.parse(play, dto);
        play.setPeriod(dto.getPeriod());
        if (play.getActs() == null) {
            play.setActs(Collections.emptySet());
        }
        return play;
    }

    /**
     * Adds / updates a lucene search doc for a play. Creates a separate doc for every DialogLines child object.
     *
     * @param play the Play to create a search doc for.
     */
    public static void addSearchDocument(final Play play) {
        LOGGER.debug("Updating play's search document...");
        Document document = SearchRepository.parseCommonFields(new Document(), play);
        SearchRepository.addDocument(document, TypeConstants.PLAY);
        play.getActs()
                .forEach(act -> act.getScenes()
                        .forEach(scene -> {
                            scene.getLines().forEach(dialogLines -> SearchRepository.addDocument(
                                    DialogLinesAndStageDirectionService.getSearchDocument(dialogLines, act.getId(),
                                            scene.getId(), play), TypeConstants.DILI
                            ));
                        }));
    }

    /**
     * Create and persist a new Play object.
     *
     * @param dto the dto with the Play's details.
     * @return 201 if good, 409 if the play already exists.
     */
    @Transactional
    public ResponseEntity<Void> add(final PlayDto dto) {
        LOGGER.debug("Adding new play: " + dto.toString());
        Play play = new Play();
        Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);

        // Check to see if Play is already in the database.
        if (playRepository.existsByAuthor_LastNameAndTitle(author.getLastName(), dto.getTitle())) {
            throw new PlayAlreadyExistsException(
                    String.format("'%s' by %s %s is already in the database.", dto.getTitle(), author.getFirstName(),
                            author.getLastName()));
        }

        play = playRepository.saveAndFlush(createOrUpdateFromDto(play, author, dto));
        addSearchDocument(play);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Modify an existing Play object.
     *
     * @param dto       the dto with the Play's updated details.
     * @param principal of the user making the request.
     * @return 204 if good, 401 if user not authorized.
     */
    @Transactional
    public ResponseEntity<Void> modify(final PlayDto dto, final Principal principal) {
        LOGGER.debug("Modifying play: " + dto.toString());
        Play play = playRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);

        if (principal.getName().equals(play.getCreatedBy()) || userDetailsService.userIsAdmin(principal)) {
            Author author = authorRepository.findById(dto.getAuthorId()).orElseThrow(ItemNotFoundException::new);
            play = playRepository.saveAndFlush(createOrUpdateFromDto(
                    play, author, dto
            ));

            // Update search document.
            SearchRepository.updateDocument(play.getId().toString(),
                    SearchRepository.parseCommonFields(new Document(), play), TypeConstants.PLAY);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Delete an existing Play object.
     *
     * @param id        the db ID of the Play to delete.
     * @param principal of the user making the request.
     * @return 204 if good, 401 if user not authorized.
     */
    @Transactional
    public ResponseEntity<Void> delete(final Long id, final Principal principal) {
        LOGGER.debug("Deleting play: " + id);
        Play play = playRepository.findById(id).orElseThrow(ItemNotFoundException::new);

        if (principal.getName().equals(play.getCreatedBy()) || userDetailsService.userIsAdmin(principal)) {
            playRepository.delete(play);
            SearchRepository.deleteDocument(id.toString(), TypeConstants.PLAY);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Get a Play from its db ID.
     *
     * @param id the ID of the Play to get.
     * @return they play. Returns 404 if the Play does not exist.
     */
    @Transactional(readOnly = true)
    public Play getById(Long id) {
        LOGGER.debug("Getting play: " + id);
        return playRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }
}
