package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.TextDto;
import com.sonnets.sonnet.services.ToolsService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * All the tools endpoints are defined here. PUT is used when a large amount of input data is needed.
 *
 * @author Josh Harkema
 */
@RestController
public class ToolsController {
    private static final String ALLOWED_ORIGIN = "*";
    private final ToolsService toolsService;

    public ToolsController(ToolsService toolsService) {
        this.toolsService = toolsService;
    }

    /**
     * Runs a kwic search on sonnets in the db.
     *
     * @param ids    the ids of the sonnets.
     * @param word   the word to look for.
     * @param length the size of the context on each side.
     * @return a List of Map<String, String> entries.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/tools/sonnet/kwic/{ids}/{word}/{length}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map.Entry<String, String>> getKwicSonnet(@PathVariable("ids") String[] ids,
                                                         @PathVariable("word") String word,
                                                         @PathVariable("length") int length) {
        return toolsService.kwicSonnets(ids, word, length);
    }

    /**
     * Lemmatize sonnets in the db.
     *
     * @param ids the sonnets to lemmatize.
     * @return a list of lemmatized words.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/tools/sonnet/lemmatize/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> lemmatizeSonnets(@PathVariable("ids") String[] ids) {
        return toolsService.lemmatizeSonnets(ids);
    }

    /**
     * Lemmatize arbitrary text.
     *
     * @param text the text to lemmatize.
     * @return a list of lemmatized words.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PostMapping(value = "/tools/text/lemmatize", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> lemmatizeText(@RequestBody @Valid TextDto text) {
        return toolsService.lemmatizeText(text);
    }

    /**
     * A freqdist of sonnets in teh db.
     *
     * @param ids the sonnets to get a freqdist of.
     * @return a Map of where key = word and value = frequency of key.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @GetMapping(value = "/tools/sonnet/freqdist/{ids}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Integer> getFrequencyDistributionSonnets(@PathVariable("ids") String[] ids) {
        return toolsService.frequencyDistribution(ids);
    }

    /**
     * Run a freqdist on arbitrary text.
     *
     * @param textDto a textDto with the text and optional custom stop words.
     * @return a Map of where key = word and value = frequency of key.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PostMapping(value = "/tools/text/freqdist", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Integer> getFrequencyDistribution(@RequestBody @Valid TextDto textDto) {
        return toolsService.frequencyDistribution(textDto);
    }
}
