package org.acl.database.services.web;

import org.acl.database.persistence.dtos.web.CustomStopWordsDto;
import org.acl.database.persistence.models.tools.CustomStopWords;
import org.acl.database.persistence.repositories.CustomStopWordsRepository;
import org.acl.database.services.exceptions.ItemNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
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

    @Autowired
    public CustomStopWordsService(CustomStopWordsRepository customStopWordsRepository) {
        this.customStopWordsRepository = customStopWordsRepository;
    }

    /**
     * Create a new list of stop words.
     *
     * @param customStopWordsDto the data for the new list.
     * @return OK if the list is added.
     */
    @Transactional
    public ResponseEntity<Void> add(CustomStopWordsDto customStopWordsDto) {
        LOGGER.debug("Creating stop words: " + customStopWordsDto.toString());
        CustomStopWords customStopWords = new CustomStopWords();
        customStopWords.setName(customStopWordsDto.getName());
        customStopWords.setWords(customStopWordsDto.getWords());
        customStopWordsRepository.save(customStopWords);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * Get a list of stop words.
     *
     * @param id the db id of the stop words list.
     * @return a list of stop words or 404 if not found.
     */
    @Transactional(readOnly = true)
    public CustomStopWords getById(Long id) {
        LOGGER.debug("Returning stop words with id: " + id);
        return customStopWordsRepository.findById(id).orElseThrow(ItemNotFoundException::new);
    }

    /**
     * Modify a list of stop words. Checks the user making the mod is the user who created the list.
     *
     * @param dto       the new data.
     * @param principal the user making the list.
     * @return OK if the list is modified.
     */
    @Transactional
    public ResponseEntity<Void> modify(CustomStopWordsDto dto, Principal principal) {
        LOGGER.debug("Modifying stop words: " + dto.toString());
        CustomStopWords stopWords = customStopWordsRepository.findById(dto.getId())
                .orElseThrow(ItemNotFoundException::new);
        if (!stopWords.getCreatedBy().equals(principal.getName())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        stopWords.setName(dto.getName());
        stopWords.setWords(dto.getWords());
        customStopWordsRepository.saveAndFlush(stopWords);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Delete a list of stop words. Ensure the user made the list being deleted.
     *
     * @param id        the id of the list to delete.
     * @param principal the user deleting the list.
     * @return OK if the list is deleted.
     */
    @Transactional
    public ResponseEntity<Void> delete(Long id, Principal principal) {
        LOGGER.debug("Deleting custom stop words with id: " + id);
        CustomStopWords stopWords = customStopWordsRepository.findById(id)
                .orElseThrow(ItemNotFoundException::new);

        if (!stopWords.getCreatedBy().equals(principal.getName())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        customStopWordsRepository.delete(stopWords);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Get all of a user's lists.
     *
     * @param principal the user to look for.
     * @return a list of all the user's lists, null if there aren't any.
     */
    @Transactional(readOnly = true)
    public List<CustomStopWords> getAllByUser(Principal principal) {
        LOGGER.debug("Getting all lists of stop words by: " + principal.getName());
        return customStopWordsRepository.findAllByCreatedBy(principal.getName());
    }
}
