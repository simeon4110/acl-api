package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.CustomStopWordsDto;
import com.sonnets.sonnet.persistence.models.web.CustomStopWords;
import com.sonnets.sonnet.persistence.repositories.CustomStopWordsRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public ResponseEntity<Void> create(CustomStopWordsDto customStopWordsDto) {
        LOGGER.debug("Creating stop words: " + customStopWordsDto.toString());
        CustomStopWords customStopWords = new CustomStopWords();
        customStopWords.setName(customStopWordsDto.getName());
        customStopWords.setWords(Arrays.asList(customStopWordsDto.getWords()));

        customStopWordsRepository.saveAndFlush(customStopWords);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<Void> modify(CustomStopWordsDto dto) {
        LOGGER.debug("Modifying stop words: " + dto.toString());
        Optional<CustomStopWords> customStopWords =
                customStopWordsRepository.findById(dto.getId());

        if (customStopWords.isPresent()) {
            CustomStopWords stopWords = customStopWords.get();
            stopWords.setName(dto.getName());
            stopWords.setWords(Arrays.asList(dto.getWords()));

            customStopWordsRepository.saveAndFlush(stopWords);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            LOGGER.error("Stop words with id '" + dto.getId() + "' not found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public List<String> getWords(String id) {
        LOGGER.debug("Getting stop words list with id: " + id);
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(id + " is not a number.");
            return Collections.emptyList();
        }

        Optional<CustomStopWords> stopWords = customStopWordsRepository.findById(parsedId);
        if (stopWords.isPresent()) {
            return stopWords.get().getWords();
        } else {
            LOGGER.error("Stop words with id '" + parsedId + "' does not exist.");
            return Collections.emptyList();
        }
    }

    public List<CustomStopWords> getAllByUser(Principal principal) {
        LOGGER.debug("Getting all lists of stop words by: " + principal.getName());
        return customStopWordsRepository.findAllByCreatedBy(principal.getName());
    }
}
