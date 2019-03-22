package com.sonnets.sonnet.services.base;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sonnets.sonnet.persistence.dtos.base.ConfirmationDto;
import com.sonnets.sonnet.persistence.dtos.web.MessageDto;
import com.sonnets.sonnet.persistence.models.base.Confirmation;
import com.sonnets.sonnet.persistence.models.base.Poem;
import com.sonnets.sonnet.persistence.models.web.User;
import com.sonnets.sonnet.persistence.repositories.UserRepository;
import com.sonnets.sonnet.persistence.repositories.poem.PoemRepository;
import com.sonnets.sonnet.services.exceptions.ItemNotFoundException;
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
    private static final Gson gson = new Gson();
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

    private static JsonArray parseToJson(Poem poem) {
        JsonObject out = new JsonObject();
        JsonObject author = new JsonObject();
        String text = String.join("\n", poem.getText());

        author.addProperty("id", poem.getAuthor().getId());
        author.addProperty("lastName", poem.getAuthor().getLastName());
        author.addProperty("firstName", poem.getAuthor().getFirstName());

        out.addProperty("id", poem.getId());
        out.addProperty("title", poem.getTitle());
        out.addProperty("sourceTitle", poem.getSourceTitle());
        out.add("author", author);
        out.addProperty("pageRange", poem.getPageRange());
        out.addProperty("text", text);

        JsonArray outArr = new JsonArray();
        outArr.add(out);
        return outArr;
    }

    /**
     * @param principal of the user making the request.
     * @return an unconfirmed poem if one not added by the user making the request exists. Returns null
     * when a user has confirmed all their assigned poems.
     */
    public String getPoemToConfirm(Principal principal) {
        LOGGER.debug("Returning a poem to confirm to user: " + principal.getName());
        User user = userRepository.findByUsername(principal.getName());

        // Catch user's who are finished confirming and return null.
        if (user.getRequiredSonnets() == user.getConfirmedSonnets() || !user.getCanConfirm()) {
            return null;
        }

        // Return testing poems when applicable.
        Poem poem;
        switch (user.getConfirmedSonnets()) {
            case 3:
                poem = poemRepository.findById(2107L).orElseThrow(ItemNotFoundException::new);
                return gson.toJson(parseToJson(poem));
            case 9:
                poem = poemRepository.findById(2108L).orElseThrow(ItemNotFoundException::new);
                return gson.toJson(parseToJson(poem));
            case 17:
                poem = poemRepository.findById(2109L).orElseThrow(ItemNotFoundException::new);
                return gson.toJson(parseToJson(poem));
            case 24:
                poem = poemRepository.findById(2110L).orElseThrow(ItemNotFoundException::new);
                return gson.toJson(parseToJson(poem));
        }
        return poemRepository.getPoemToConfirm(principal.getName()).orElse(null);
    }

    /**
     * @param dto       a valid ConfirmationDto.
     * @param principal of the user making the request.
     * @return OK if the poem is confirmed, UNAUTHORIZED if the user is attempting to confirm their own poem.
     */
    public ResponseEntity<Void> confirmPoem(ConfirmationDto dto, Principal principal) {
        LOGGER.debug("Confirming poem: " + dto.toString());
        Poem poem = poemRepository.findById(dto.getItemId()).orElseThrow(ItemNotFoundException::new);
        User user = userRepository.findByUsername(principal.getName());

        if (poem.isTesting()) { // Catch incorrect testing confirmations.
            MessageDto messageDto = new MessageDto();
            messageDto.setUserTo("jharkema");
            messageDto.setUserFrom(principal.getName());
            messageDto.setContent(principal.getName() + " has NOT identified a testing sonnet correctly: "
                    + poem.getTitle());
            messageService.sendMessage(messageDto, principal);

            user.setConfirmedSonnets(user.getConfirmedSonnets() + 1);
            userRepository.saveAndFlush(user);
            return new ResponseEntity<>(HttpStatus.OK);
        }

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

        user.setConfirmedSonnets(user.getConfirmedSonnets() + 1);
        userRepository.saveAndFlush(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * @param dto a valid ConfirmationDto.
     * @return OK if successful.
     */
    public ResponseEntity<Void> rejectPoem(ConfirmationDto dto, Principal principal) {
        LOGGER.debug("Rejecting poem: " + dto.toString());
        Poem poem = poemRepository.findById(dto.getItemId()).orElseThrow(ItemNotFoundException::new);

        if (poem.isTesting()) { // Catch correct testing rejections.
            User user = userRepository.findByUsername(principal.getName());

            MessageDto messageDto = new MessageDto();
            messageDto.setUserTo("jharkema");
            messageDto.setUserFrom(principal.getName());
            messageDto.setContent(principal.getName() + " has correctly identified a testing sonnet: "
                    + poem.getTitle() + "\nWith message: " + dto.getMessage());
            messageService.sendMessage(messageDto, principal);

            user.setConfirmedSonnets(user.getConfirmedSonnets() + 1);
            userRepository.saveAndFlush(user);

            return new ResponseEntity<>(HttpStatus.OK);
        }

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
