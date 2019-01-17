package com.sonnets.sonnet.controllers.tools;

import com.sonnets.sonnet.persistence.dtos.web.CustomStopWordsDto;
import com.sonnets.sonnet.persistence.models.tools.CustomStopWords;
import com.sonnets.sonnet.services.web.CustomStopWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * Handles all REST endpoints related to user's custom stop words lists.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class CustomStopWordsController {
    private final CustomStopWordsService customStopWordsService;

    @Autowired
    public CustomStopWordsController(CustomStopWordsService customStopWordsService) {
        this.customStopWordsService = customStopWordsService;
    }

    /**
     * Create a list of custom stop words.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'GUEST')")
    @PostMapping(value = "/secure/stop_words/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createList(@RequestBody @Valid CustomStopWordsDto customStopWordsDto,
                                           Principal principal) {
        return customStopWordsService.create(customStopWordsDto, principal);
    }

    /**
     * Modify a list of custom stop words.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'GUEST')")
    @PutMapping(value = "/secure/stop_words/modify", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modifyList(@RequestBody @Valid CustomStopWordsDto dto, Principal principal) {
        return customStopWordsService.modify(dto, principal);
    }

    /**
     * Delete a list of custom stop words.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'GUEST')")
    @DeleteMapping(value = "/secure/stop_words/delete/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable("id") String id, Principal principal) {
        return customStopWordsService.delete(id, principal);
    }

    /**
     * @param id the id of the list to get.
     * @return a list of words.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'GUEST')")
    @GetMapping(value = "/secure/stop_words/get_words/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getWords(@PathVariable("id") String id) {
        return customStopWordsService.getWords(id);
    }

    /**
     * @return all of a user's stop words lists.
     */
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'GUEST')")
    @GetMapping(value = "/secure/stop_words/get_user_lists", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CustomStopWords> getUserLists(Principal principal) {
        return customStopWordsService.getAllByUser(principal);
    }
}
