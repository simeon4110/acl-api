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

    /**
     * Converts testing poems into JSON so they appear the same as regular poems.
     *
     * @param poem the poem to parse into a json object.
     * @return the poem as a JsonArray.
     */
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
     * Gives a user a poem to confirm. If the user cannot confirm, or has confirmed the required
     * number of poems, null is returned.
     *
     * @param principal of the user making the request.
     * @return an unconfirmed poem if one not added by the user making the request exists. Returns null
     * when a user has confirmed all their assigned poems.
     */
    public String getPoemToConfirm(Principal principal) {
        LOGGER.debug("Returning a poem to confirm to user: " + principal.getName());
        User user = userRepository.findByUsername(principal.getName());

        // Catch user's who are finished confirming and return null.
        if (user.getRequiredSonnets() <= user.getConfirmedSonnets() || !user.getCanConfirm()) {
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
            default:
                return poemRepository.getPoemToConfirm(principal.getName()).orElse(null);
        }
    }

    /**
     * Confirms a poem, notifies admin of correctly identified testing poems.
     *
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
            messageDto.setSubject("INCORRECT - testing sonnet");
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

        // Set poem to confirmed.
        Confirmation confirmation = poem.getConfirmation();
        confirmation.setConfirmed(true);
        confirmation.setConfirmedBy(principal.getName());
        confirmation.setConfirmedAt(new Timestamp(System.currentTimeMillis()));
        confirmation.setPendingRevision(false);
        poem.setConfirmation(confirmation);
        poemRepository.saveAndFlush(poem);

        // Update confirming users number of confirmed poems +1.
        user.setConfirmedSonnets(user.getConfirmedSonnets() + 1);
        userRepository.saveAndFlush(user);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This is a bit of a mess, but it gets the job done. Handles the logic for poem rejections.
     * All rejections count, and each rejection adds 1 to the user who created the poems required
     * confirmations.
     *
     * @param dto a valid ConfirmationDto.
     * @return OK if successful.
     */
    public ResponseEntity<Void> rejectPoem(ConfirmationDto dto, Principal principal) {
        LOGGER.debug("Rejecting poem: " + dto.toString());
        Poem poem = poemRepository.findById(dto.getItemId()).orElseThrow(ItemNotFoundException::new);

        if (poem.isTesting()) { // Catch correct testing rejections.
            User user = userRepository.findByUsername(principal.getName());

            // Send a message to admin with the correct identification of a testing sonnet.
            MessageDto messageDto = new MessageDto();
            messageDto.setUserTo("jharkema");
            messageDto.setSubject("CORRECT - testing sonnet");
            messageDto.setUserFrom(principal.getName());
            messageDto.setContent(principal.getName() + " has correctly identified a testing sonnet: "
                    + poem.getTitle() + "\nWith message: " + dto.getMessage());
            messageService.sendMessage(messageDto, principal);

            // Update the user's confirmed sonnets + 1
            user.setConfirmedSonnets(user.getConfirmedSonnets() + 1);
            userRepository.saveAndFlush(user);

            return new ResponseEntity<>(HttpStatus.OK);
        }

        // Set confirmation to false and pending revision to true.
        Confirmation confirmation = poem.getConfirmation();
        confirmation.setConfirmed(false);
        confirmation.setPendingRevision(true);
        poem.setConfirmation(confirmation);
        poemRepository.save(poem);

        // Notify the poems owner of the rejection.
        MessageDto messageDto = new MessageDto();
        messageDto.setUserTo(poem.getCreatedBy());
        messageDto.setSubject("One of your sonnets has been rejected");
        messageDto.setContent("Title: " + poem.getTitle() + "\n" + dto.getMessage());
        messageService.sendAdminMessage(messageDto);

        // Update the user's who's poem is rejected.
        User user = userRepository.findByUsername(poem.getCreatedBy());
        user.setRequiredSonnets(user.getRequiredSonnets() + 1);
        user.setCanConfirm(false);
        userRepository.saveAndFlush(user);

        // Update the user who did the rejecting.
        User rejectingUser = userRepository.findByUsername(principal.getName());
        rejectingUser.setConfirmedSonnets(rejectingUser.getConfirmedSonnets() + 1);
        userRepository.saveAndFlush(rejectingUser);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
