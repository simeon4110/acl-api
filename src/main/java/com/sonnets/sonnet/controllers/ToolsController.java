package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.base.TextDto;
import com.sonnets.sonnet.services.ToolsService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * Run a topic model on arbitrary text.
     *
     * @param textDto a textDto with the text to model.
     * @return a Map where the key is an integer (0 = most likely, -1 = trimmed) and the key is a Map where the key
     * is the exact probability of the model and the value is the model.
     */
    @CrossOrigin(origins = ALLOWED_ORIGIN)
    @PostMapping(value = "/tools/text/topic_model", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer, Map<Double, String>> runTopicModel(@RequestBody @Valid TextDto textDto) {
        return toolsService.runMalletTopicModel(textDto);
    }
}
