package com.sonnets.sonnet.services.embedded;

import com.sonnets.sonnet.persistence.dtos.base.ConfirmationDto;
import com.sonnets.sonnet.persistence.models.base.Confirmation;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.web.User;
import com.sonnets.sonnet.persistence.repositories.UserRepository;
import com.sonnets.sonnet.persistence.repositories.poem.PoemRepository;
import com.sonnets.sonnet.services.exceptions.ItemNotFoundException;
import com.sonnets.sonnet.services.exceptions.NothingToConfirmException;
import com.sonnets.sonnet.services.web.MessageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;

/**
 * Handles CRUD and logic for item confirmations.
 *
 * @author Josh Harkema
 */
@Service
public class ConfirmationService {
    private static final Logger LOGGER = Logger.getLogger(ConfirmationService.class);
    private final PoemRepository poemRepository;
    private final MessageService messageService;
    private final UserRepository userRepository;

    @Autowired
    public ConfirmationService(PoemRepository poemRepository, MessageService messageService,
                               UserRepository userRepository) {
        this.poemRepository = poemRepository;
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    /**
     * @param principal of the user making the request.
     * @return an unconfirmed poem if one not added by the user making the request exists.
     */
    public Poem getPoemToConfirm(Principal principal) {
        LOGGER.debug("Returning a poem to confirm to user: " + principal.getName());
        return poemRepository.getDistinctFirstByConfirmation_ConfirmedAndCreatedByNot(false,
                principal.getName()).orElseThrow(NothingToConfirmException::new);
    }

    /**
     * @param dto       a valid ConfirmationDto.
     * @param principal of the user making the request.
     * @return OK if the poem is confirmed, UNAUTHORIZED if the user is attempting to confirm their own poem.
     */
    public ResponseEntity<Void> confirmPoem(ConfirmationDto dto, Principal principal) {
        LOGGER.debug("Confirming poem: " + dto.toString());
        Poem poem = poemRepository.findById(dto.getItemId()).orElseThrow(ItemNotFoundException::new);
        if (poem.getCreatedBy().equals(principal.getName())) { // Check user is not self-confirming.
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Confirmation confirmation = poem.getConfirmation();
        confirmation.setConfirmed(true);
        confirmation.setConfirmedBy(principal.getName());
        confirmation.setConfirmedAt(new Timestamp(System.currentTimeMillis()));
        confirmation.setPendingRevision(false);
        poem.setConfirmation(confirmation);
        poemRepository.saveAndFlush(poem);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param dto a valid ConfirmationDto.
     * @return OK if successful.
     */
    public ResponseEntity<Void> rejectPoem(ConfirmationDto dto) {
        LOGGER.debug("Rejecting poem>: " + dto.toString());
        Poem poem = poemRepository.findById(dto.getItemId()).orElseThrow(ItemNotFoundException::new);
        Confirmation confirmation = poem.getConfirmation();
        confirmation.setConfirmed(false);
        confirmation.setPendingRevision(true);
        poem.setConfirmation(confirmation);
        poemRepository.save(poem);
        messageService.sendRejectMessage("admin", poem.getCreatedBy(), dto.getMessage());
        User user = userRepository.findByUsername(poem.getCreatedBy());
        user.setCanConfirm(false);
        userRepository.saveAndFlush(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
