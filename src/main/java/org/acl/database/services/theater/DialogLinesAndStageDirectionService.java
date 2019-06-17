package org.acl.database.services.theater;

import org.acl.database.config.LuceneConfig;
import org.acl.database.persistence.dtos.theater.DialogLinesDto;
import org.acl.database.persistence.dtos.theater.StageDirectionDto;
import org.acl.database.persistence.models.TypeConstants;
import org.acl.database.persistence.models.theater.*;
import org.acl.database.persistence.repositories.theater.*;
import org.acl.database.search.SearchRepository;
import org.acl.database.security.UserDetailsServiceImpl;
import org.acl.database.services.exceptions.ItemNotFoundException;
import org.acl.database.services.search.SearchConstants;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Combined service for handling both DialogLines and StageDirections (it is combined because both objects are
 * basically the same.)
 *
 * @author Josh Harkema
 */
@Service
public class DialogLinesAndStageDirectionService {
    private static final Logger LOGGER = Logger.getLogger(DialogLinesAndStageDirectionService.class);
    private final DialogLinesRepository dialogLinesRepository;
    private final StageDirectionRepository stageDirectionRepository;
    private final PlayRepository playRepository;
    private final SceneRepository sceneRepository;
    private final ActorRepository actorRepository;
    private final UserDetailsServiceImpl userDetailsService;

    public DialogLinesAndStageDirectionService(DialogLinesRepository dialogLinesRepository,
                                               StageDirectionRepository stageDirectionRepository,
                                               PlayRepository playRepository, SceneRepository sceneRepository,
                                               ActorRepository actorRepository,
                                               UserDetailsServiceImpl userDetailsService) {
        this.dialogLinesRepository = dialogLinesRepository;
        this.stageDirectionRepository = stageDirectionRepository;
        this.playRepository = playRepository;
        this.sceneRepository = sceneRepository;
        this.actorRepository = actorRepository;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Splits and trims a string on newline (\n) chars.
     *
     * @param in the string to split.
     * @return a list of trimmed strings.
     */
    private static List<String> parseBody(final String in) {
        return Arrays.stream(in.split("\\n")).map(String::trim).collect(Collectors.toUnmodifiableList());
    }

    /**
     * Parses a DialogLinesDto onto a DialogLines object.
     *
     * @param actor       the actor defined in the dto.
     * @param dialogLines the DialogLines object to parse the dto onto.
     * @param dto         the dto with the data to parse.
     * @return a DialogLines Object with the data from the dto added.
     */
    private static DialogLines createOrUpdateDialogLinesFromDto(final Actor actor, final DialogLines dialogLines,
                                                                final DialogLinesDto dto) {
        dialogLines.setActor(actor);
        dialogLines.setBody(parseBody(dto.getBody()));
        dialogLines.setSequence(dto.getSequence());
        return dialogLines;
    }

    /**
     * Parses a StageDirectionDto onto a StageDirection object.
     *
     * @param actors         the set of actors defined in the dto.
     * @param stageDirection the StageDirection object to parse the dto onto.
     * @param dto            the dto with the data to parse.
     * @return a StageDirection object with the data from the dto added.
     */
    private static StageDirection createOrUpdateStageDirectionFromDto(final Set<Actor> actors,
                                                                      final StageDirection stageDirection,
                                                                      final StageDirectionDto dto) {
        stageDirection.setActors(actors);
        stageDirection.setBody(dto.getBody());
        stageDirection.setSequence(dto.getSequence());
        return stageDirection;
    }

    /**
     * Parses a DialogLines object onto a Lucene search document.
     *
     * @param dialogLines the object to parse.
     * @param actId       the parent act's db ID.
     * @param sceneId     the parent scene's db ID.
     * @param play        the parent play.
     * @return a Lucene doc with the details added.
     */
    static Document parseSearchDocument(final DialogLines dialogLines, final Long actId, final Long sceneId,
                                        final Play play) {
        Document document = new Document();
        document.add(new StringField(SearchConstants.ID, dialogLines.getId().toString(), Field.Store.YES));
        document.add(new StringField(SearchConstants.ACT_NUMBER, actId.toString(), Field.Store.YES));
        document.add(new StringField(SearchConstants.SCENE_NUMBER, sceneId.toString(), Field.Store.YES));
        document.add(new TextField(SearchConstants.TITLE, play.getTitle(), Field.Store.YES));
        document.add(new TextField(SearchConstants.AUTHOR_FIRST_NAME, play.getAuthor().getFirstName(),
                Field.Store.YES));
        document.add(new TextField(SearchConstants.AUTHOR_LAST_NAME, play.getAuthor().getLastName(), Field.Store.YES));
        if (dialogLines.getActor().getFirstName() != null) {
            document.add(new TextField(SearchConstants.ACTOR_FIRST_NAME, dialogLines.getActor().getFirstName(),
                    Field.Store.YES));
        }
        if (dialogLines.getActor().getMiddleName() != null) {
            document.add(new TextField(SearchConstants.ACTOR_MIDDLE_NAME, dialogLines.getActor().getMiddleName(),
                    Field.Store.YES));
        }
        if (dialogLines.getActor().getLastName() != null) {
            document.add(new TextField(SearchConstants.ACTOR_LAST_NAME, dialogLines.getActor().getLastName(),
                    Field.Store.YES));
        }
        document.add(LuceneConfig.getTextField(String.join(" ", dialogLines.getBody())));
        return document;
    }

    /**
     * Creates and persists a new DialogLines object.
     *
     * @param dto the dto with the new object's details.
     * @return 201 if good.
     */
    @Transactional
    public ResponseEntity<Void> addDialogLines(final DialogLinesDto dto) {
        LOGGER.debug("Adding dialogLines: " + dto.toString());
        Actor actor = actorRepository.findById(dto.getActorId()).orElseThrow(ItemNotFoundException::new);
        Scene scene = sceneRepository.findById(dto.getSceneId()).orElseThrow(ItemNotFoundException::new);
        DialogLines dialogLines = createOrUpdateDialogLinesFromDto(actor, new DialogLines(), dto);
        dialogLines = dialogLinesRepository.saveAndFlush(dialogLines);

        // Add new search doc.
        SearchRepository.addDocument(
                parseSearchDocument(dialogLines, dto.getActId(), dto.getSceneId(),
                        playRepository.findById(dto.getPlayId()).orElseThrow(ItemNotFoundException::new)),
                TypeConstants.DILI
        );

        scene.getLines().add(dialogLines);
        sceneRepository.saveAndFlush(scene);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Modifies an existing DialogLines object.
     *
     * @param dto       the dto with the new data.
     * @param principal the principal of the user making the request.
     * @return 204 if good, 401 if user is not authorized.
     */
    @Transactional
    public ResponseEntity<Void> modifyDialogLines(final DialogLinesDto dto, final Principal principal) {
        LOGGER.debug("Modifying dialog lines: " + dto.toString());
        DialogLines lines = dialogLinesRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);

        if (principal.getName().equals(lines.getCreatedBy()) || userDetailsService.userIsAdmin(principal)) {
            Actor actor = actorRepository.findById(dto.getActorId()).orElseThrow(ItemNotFoundException::new);
            lines = dialogLinesRepository.saveAndFlush(createOrUpdateDialogLinesFromDto(
                    actor, lines, dto
            ));

            // Update the search doc.
            SearchRepository.updateDocument(lines.getId().toString(),
                    parseSearchDocument(lines, dto.getActId(), dto.getSceneId(),
                            playRepository.findById(dto.getPlayId()).orElseThrow(ItemNotFoundException::new)),
                    TypeConstants.DILI);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    /**
     * Creates and persists a new StageDirection object.
     *
     * @param dto the dto with the new object's details.
     * @return 201 if good.
     */
    @Transactional
    public ResponseEntity<Void> addStageDirection(final StageDirectionDto dto) {
        LOGGER.debug("Adding stage direction: " + dto.toString());
        Set<Actor> actors = dto.getActorIds().stream()
                .map(x -> actorRepository.findById(x).orElseThrow(ItemNotFoundException::new))
                .collect(Collectors.toUnmodifiableSet());
        Scene scene = sceneRepository.findById(dto.getSceneId()).orElseThrow(ItemNotFoundException::new);
        StageDirection stageDirection = createOrUpdateStageDirectionFromDto(actors, new StageDirection(), dto);
        stageDirection = stageDirectionRepository.saveAndFlush(stageDirection);
        scene.getDirections().add(stageDirection);
        sceneRepository.saveAndFlush(scene);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Modifies an existing StageDirection object.
     *
     * @param dto       the dto with the updated data.
     * @param principal the principal of the user making the request.
     * @return 204 if good, 401 if user is not authorized.
     */
    @Transactional
    public ResponseEntity<Void> modifyStageDirection(final StageDirectionDto dto, final Principal principal) {
        LOGGER.debug("Modifying stage direction: " + dto.toString());
        StageDirection direction = stageDirectionRepository.findById(
                dto.getId()).orElseThrow(ItemNotFoundException::new);

        if (principal.getName().equals(direction.getCreatedBy()) || userDetailsService.userIsAdmin(principal)) {
            Set<Actor> actors = dto.getActorIds().stream()
                    .map(x -> actorRepository.findById(x).orElseThrow(ItemNotFoundException::new))
                    .collect(Collectors.toUnmodifiableSet());
            stageDirectionRepository.saveAndFlush(createOrUpdateStageDirectionFromDto(
                    actors, direction, dto
            ));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}
