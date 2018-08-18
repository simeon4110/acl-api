package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.CustomStopWordsDto;
import com.sonnets.sonnet.persistence.models.web.CustomStopWords;
import com.sonnets.sonnet.persistence.models.web.User;
import com.sonnets.sonnet.persistence.repositories.CustomStopWordsRepository;
import com.sonnets.sonnet.security.UserDetailsServiceImpl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
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

    public ResponseEntity<Void> modify(CustomStopWordsDto dto, Principal principal) {
        LOGGER.debug("Modifying stop words: " + dto.toString());
        CustomStopWords stopWords = getWordsListOrNull(dto.getId().toString());
        if (stopWords != null && stopWords.getCreatedBy().equals(principal.getName())) {
            stopWords.setName(dto.getName());
            stopWords.setWords(Arrays.asList(dto.getWords()));
            customStopWordsRepository.saveAndFlush(stopWords);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Void> delete(String id, Principal principal) {
        LOGGER.debug("Deleting custom stop words with id: " + id);
        CustomStopWords stopWords = getWordsListOrNull(id);
        if (stopWords != null && stopWords.getCreatedBy().equals(principal.getName())) {
            customStopWordsRepository.delete(stopWords);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public List<String> getWords(String id) {
        LOGGER.debug("Getting stop words list with id: " + id);
        CustomStopWords stopWords = getWordsListOrNull(id);
        if (stopWords != null) {
            return stopWords.getWords();
        }
        return Collections.emptyList();
    }

    public List<CustomStopWords> getAllByUser(Principal principal) {
        LOGGER.debug("Getting all lists of stop words by: " + principal.getName());
        return customStopWordsRepository.findAllByCreatedBy(principal.getName());
    }

    private CustomStopWords getWordsListOrNull(String id) {
        long parsedId;
        try {
            parsedId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            LOGGER.error(id + " is not a number.");
            return null;
        }
        return customStopWordsRepository.findById(parsedId).orElse(null);
    }
}