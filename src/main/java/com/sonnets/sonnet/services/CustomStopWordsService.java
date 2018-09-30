package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.web.CustomStopWordsDto;
import com.sonnets.sonnet.persistence.models.web.CustomStopWords;
import com.sonnets.sonnet.persistence.models.web.User;
import com.sonnets.sonnet.persistence.repositories.CustomStopWordsRepository;
import com.sonnets.sonnet.security.UserDetailsServiceImpl;
import com.sonnets.sonnet.services.exceptions.ItemNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

/**
 * Service handles CRUD for user's custom stop words.
 *
 * @author Josh Harkema
 */
@Service
public class CustomStopWordsService {
    private static final Logger LOGGER = Logger.getLogger(CustomStopWordsService.class);
    private final CustomStopWordsRepository customStopWordsRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public CustomStopWordsService(CustomStopWordsRepository customStopWordsRepository,
                                  UserDetailsServiceImpl userDetailsService) {
        this.customStopWordsRepository = customStopWordsRepository;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Create a new list of stop words.
     *
     * @param customStopWordsDto the data for the new list.
     * @param principal          the user making the list.
     * @return OK if the list is added.
     */
    public ResponseEntity<Void> create(CustomStopWordsDto customStopWordsDto, Principal principal) {
        LOGGER.debug("Creating stop words: " + customStopWordsDto.toString());
        CustomStopWords customStopWords = new CustomStopWords();
        customStopWords.setName(customStopWordsDto.getName());
        customStopWords.setWords(Arrays.asList(customStopWordsDto.getWords()));

        User user = userDetailsService.loadUserObjectByUsername(principal.getName());
        user.getCustomStopWords().add(customStopWords);
        userDetailsService.save(user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Modify a list of stop words. Checks the user making the mod is the user who created the list.
     *
     * @param dto       the new data.
     * @param principal the user making the list.
     * @return OK if the list is modified.
     */
    public ResponseEntity<Void> modify(CustomStopWordsDto dto, Principal principal) {
        LOGGER.debug("Modifying stop words: " + dto.toString());
        CustomStopWords stopWords = getWordsListOrThrowNotFound(dto.getId().toString());
        assert stopWords.getCreatedBy().equals(principal.getName());
        stopWords.setName(dto.getName());
        stopWords.setWords(Arrays.asList(dto.getWords()));
        customStopWordsRepository.saveAndFlush(stopWords);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Delete a list of stop words. Ensure the user made the list being deleted.
     *
     * @param id        the id of the list to delete.
     * @param principal the user deleting the list.
     * @return OK if the list is deleted.
     */
    public ResponseEntity<Void> delete(String id, Principal principal) {
        LOGGER.debug("Deleting custom stop words with id: " + id);
        CustomStopWords stopWords = getWordsListOrThrowNotFound(id);
        assert stopWords.getCreatedBy().equals(principal.getName());
        customStopWordsRepository.delete(stopWords);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get a list of words.
     *
     * @param id the id of the list.
     * @return the list of words.
     */
    public List<String> getWords(String id) {
        LOGGER.debug("Getting stop words list with id: " + id);
        CustomStopWords stopWords = getWordsListOrThrowNotFound(id);
        return stopWords.getWords();
    }

    /**
     * Get all of a user's lists.
     *
     * @param principal the user to look for.
     * @return a list of all the user's lists, null if there aren't any.
     */
    public List<CustomStopWords> getAllByUser(Principal principal) {
        LOGGER.debug("Getting all lists of stop words by: " + principal.getName());
        return customStopWordsRepository.findAllByCreatedBy(principal.getName());
    }

    /**
     * Helper for getting lists, returns null if a list isn't found.
     *
     * @param id the id of the list to find.
     * @return the list or null.
     */
    CustomStopWords getWordsListOrThrowNotFound(String id) {
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(id + " is not a number.");
            return null;
        }
        return customStopWordsRepository.findById(parsedId).orElseThrow(ItemNotFoundException::new);
    }
}
