package org.acl.database.services.annotation;

import org.acl.database.persistence.dtos.base.AnnotationDto;
import org.acl.database.persistence.models.annotation.Dialog;
import org.acl.database.persistence.models.prose.BookCharacter;
import org.acl.database.persistence.repositories.annotation.DialogRepository;
import org.acl.database.services.exceptions.AnnotationTypeMismatchException;
import org.acl.database.services.exceptions.ItemNotFoundException;
import org.acl.database.services.prose.CharacterService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CRUD service for Dialog annotations.
 *
 * @author Josh Harkema
 */
@Service
public class DialogService {
    private static final Logger LOGGER = Logger.getLogger(DialogService.class);
    private final DialogRepository dialogRepository;
    private final CharacterService characterService;

    @Autowired
    public DialogService(DialogRepository dialogRepository, CharacterService characterService) {
        this.dialogRepository = dialogRepository;
        this.characterService = characterService;
    }

    /**
     * Get all of a characters dialog objects.
     *
     * @param id the id of the character.
     * @return a list of dialog objects or an ItemNotFoundException.
     */
    @Transactional(readOnly = true)
    public List<Dialog> getAllFromCharacter(final Long id) {
        LOGGER.debug("Getting all dialog from book: " + id);
        return dialogRepository.findAllByItemIdOrderByCharacterOffsetBeginAsc(id)
                .orElseThrow(ItemNotFoundException::new);
    }

    /**
     * Get all dialog objects.
     *
     * @return a list of all dialog objects.
     */
    @Transactional(readOnly = true)
    public List<Dialog> getAll() {
        LOGGER.debug("Returning all dialogs.");
        return dialogRepository.findAll();
    }

    /**
     * Delete a dialog object from the database.
     *
     * @param id the id of the dialog to delete.
     * @return 200 if successful.
     */
    @Transactional
    public ResponseEntity<Void> delete(final Long id) {
        LOGGER.debug("Deleting dialog: " + id);
        Dialog dialog = dialogRepository.findById(id).orElseThrow(ItemNotFoundException::new);
        BookCharacter character = characterService.getCharacterOrThrowNotFound(dialog.getItemId());
        character.getDialog().remove(dialog);
        characterService.save(character);
        dialogRepository.delete(dialog);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private static Dialog createOrCopy(Dialog dialog, AnnotationDto dto) {
        dialog.setBody(dto.getBody());
        dialog.setItemId(dto.getItemId());
        dialog.setItemFriendly(dto.getItemFriendly());
        dialog.setSectionId(dto.getSectionId());
        dialog.setCharacterOffsetBegin(dto.getCharacterOffsetBegin());
        dialog.setCharacterOffsetEnd(dto.getCharacterOffsetEnd());
        return dialog;
    }

    @Transactional
    public Dialog add(final AnnotationDto dto) {
        LOGGER.debug("Adding dialog: " + dto.toString());
        if (!dto.getType().equals("CHAR")) {
            throw new AnnotationTypeMismatchException(dto.getType() + " is not a valid dialog annotation type.");
        }
        Dialog dialog = createOrCopy(new Dialog(), dto);
        BookCharacter character = characterService.getCharacterOrThrowNotFound(dto.getItemId());
        character.getDialog().add(dialog);
        characterService.save(character);
        return dialog;
    }

    @Transactional
    public Dialog modify(final AnnotationDto dto) {
        LOGGER.debug("Modifying dialog: " + dto.toString());
        Dialog dialog = dialogRepository.findById(dto.getId()).orElseThrow(ItemNotFoundException::new);
        return dialogRepository.save(
                createOrCopy(dialog, dto)
        );
    }
}
