package com.sonnets.sonnet.controllers;

import com.sonnets.sonnet.persistence.dtos.base.TextDto;
import com.sonnets.sonnet.services.ToolsService;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * All the tools endpoints are defined here. PUT is used when a large amount of input data is needed.
 *
 * @author Josh Harkema
 */
@RestController
@PropertySource("classpath:global.properties")
public class ToolsController {
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
    @Async
    @CrossOrigin(origins = "${allowed-origin}")
    @PostMapping(value = "/tools/text/lemmatize", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<List<String>> lemmatizeText(@RequestBody @Valid TextDto text) {
        return toolsService.lemmatizeText(text);
    }

    /**
     * Runs a POS tag and Lemmatizer on arbitrary text.
     *
     * @param text the text to tag.
     * @return a JSON indexed list of all the words and their POS tags and lemmas.
     */
    @Async
    @CrossOrigin(origins = "${allowed-origin}")
    @PostMapping(value = "/tools/text/simple_tagger", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<String> tagRawTextSimple(@RequestBody @Valid TextDto text) {
        return toolsService.tagTextSimple(text);
    }

    /**
     * Lemmatize corpora content.
     *
     * @param corporaId   the corpora db id.
     * @param stopWordsId the stop words db id.
     * @return a list of lemmatized words.
     */
    @Async
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'GUEST')")
    @GetMapping(value = "/tools/corpora/lemmatize", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<List<String>> lemmatizeCorpora(@RequestParam("corpora_id") String corporaId,
                                                            @RequestParam(value = "stop_words_id", required = false) String stopWordsId) {
        return toolsService.lemmatizeText(corporaId, stopWordsId);
    }

    /**
     * Run a freqdist on arbitrary text.
     *
     * @param textDto a textDto with the text and optional custom stop words.
     * @return a Map of where key = word and value = frequency of key.
     */
    @Async
    @CrossOrigin(origins = "${allowed-origin}")
    @PostMapping(value = "/tools/text/freqdist", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<Map<String, Integer>> getFrequencyDistribution(@RequestBody @Valid TextDto textDto) {
        return toolsService.frequencyDistribution(textDto);
    }

    /**
     * Run a freqdist on corpora content.
     *
     * @param corporaId   the corpora db id.
     * @param stopWordsId the stop words db id.
     * @return a Map of where key = word and value = frequency of key.
     */
    @Async
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'GUEST')")
    @GetMapping(value = "/tools/corpora/freqdist", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<Map<String, Integer>> getFrequencyDistributionCorpora(@RequestParam("corpora_id") String corporaId,
                                                                                   @RequestParam(value = "stop_words_id",
                                                                        required = false) String stopWordsId) {
        return toolsService.frequencyDistribution(corporaId, stopWordsId);
    }

    /**
     * Run a topic model on arbitrary text.
     *
     * @param textDto a textDto with the text to model.
     * @return a Map where the key is an integer (0 = most likely, -1 = trimmed) and the key is a Map where the key
     * is the exact probability of the model and the value is the model.
     */
    @Async
    @CrossOrigin(origins = "${allowed-origin}")
    @PostMapping(value = "/tools/text/topic_model", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<Map<Integer, Map<Double, String>>> runTopicModel(@RequestBody @Valid TextDto textDto) {
        return toolsService.runMalletTopicModel(textDto);
    }

    /**
     * Run a topic model on corpora content.
     *
     * @param corporaId    the corpora db id.
     * @param numberTopics the number of topics desired.
     * @return a Map where the key is an integer (0 = most likely, -1 = trimmed) and the key is a Map where the key
     * is the exact probability of the model and the value is the model.
     */
    @Async
    @CrossOrigin(origins = "${allowed-origin}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'GUEST')")
    @GetMapping(value = "/tools/corpora/topic_model", produces = MediaType.APPLICATION_JSON_VALUE)
    public CompletableFuture<Map<Integer, Map<Double, String>>> runTopicModelCorpora(@RequestParam("corpora_id") String corporaId,
                                                                                     @RequestParam("number_topics") int numberTopics) {
        return toolsService.runMalletTopicModel(corporaId, numberTopics);
    }
}
