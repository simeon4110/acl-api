package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.base.ItemOutDto;
import com.sonnets.sonnet.persistence.dtos.base.TextDto;
import com.sonnets.sonnet.persistence.models.web.CustomStopWords;
import com.sonnets.sonnet.services.helpers.GetObjectOrThrowNullPointer;
import com.sonnets.sonnet.wordtools.FrequencyDistribution;
import com.sonnets.sonnet.wordtools.MalletTools;
import com.sonnets.sonnet.wordtools.NLPTools;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * This service coordinates all the NLP and other tools methods.
 *
 * @author Josh Harkema
 */
@Service
public class ToolsService {
    private static final Logger LOGGER = Logger.getLogger(ToolsService.class);
    private static final NLPTools pipeline = NLPTools.getInstance();
    private static final MalletTools malletTools = MalletTools.getInstance();
    private static final FrequencyDistribution freqDist = FrequencyDistribution.getInstance();
    private final GetObjectOrThrowNullPointer getObjectOrThrowNullPointer;
    private final CorporaService corporaService;

    @Autowired
    public ToolsService(GetObjectOrThrowNullPointer getObjectOrThrowNullPointer, CorporaService corporaService) {
        this.getObjectOrThrowNullPointer = getObjectOrThrowNullPointer;
        this.corporaService = corporaService;
    }

    /**
     * Helper method for stripping a corpora into raw text.
     *
     * @param items the items to strip.
     * @return a string of the combined item's text.
     */
    private String parseCorporaItems(final Set<ItemOutDto> items) {
        StringBuilder sb = new StringBuilder();
        Consumer<ItemOutDto> itemConsumer = item -> { // Function strips items into text only.
            switch (item.getCategory()) {
                case "POEM":
                    sb.append(item.getPoemText());
                    break;
                case "SECTION":
                    sb.append(item.getText());
                    break;
            }
        };
        items.forEach(itemConsumer);
        return sb.toString();
    }

    /**
     * @param text a textDto with the text to lemmatize and optional custom stop words.
     * @return a list of lemmatized strings.
     */
    @Async
    public CompletableFuture<List<String>> lemmatizeText(TextDto text) {
        LOGGER.debug("Running text lemmatizer (raw text.)");
        return pipeline.getListOfLemmatizedWords(text);
    }

    /**
     * @param corporaId   a corpora db id.
     * @param stopWordsId a stop words id.
     * @return a list of lemmatized strings.
     */
    @Async
    public CompletableFuture<List<String>> lemmatizeText(String corporaId, String stopWordsId) {
        LOGGER.debug("Running text lemmatizer (corpora): " + corporaId);
        TextDto dto = new TextDto();
        CustomStopWords customStopWords;
        if (Integer.parseInt(stopWordsId) != 0) {
            customStopWords = getObjectOrThrowNullPointer.stopWords(stopWordsId);
            dto.setCustomStopWords(customStopWords.getWords().toArray(new String[0]));
        }
        corporaService.getCorporaItems(corporaId).thenAccept(s -> dto.setText(parseCorporaItems(s)));
        return pipeline.getListOfLemmatizedWords(dto);
    }

    /**
     * @param text a textDto with some text to tag.
     * @return a JSON index of all the tags.
     */
    @Async
    public CompletableFuture<String> tagTextSimple(TextDto text) {
        LOGGER.debug("Running simple tagger.");
        String result = NLPTools.tagTextSimple(text);
        return CompletableFuture.completedFuture(result);
    }

    /**
     * @param text the text to tag.
     * @return a JSON index of all the tags.
     */
    @Async
    public CompletableFuture<String> tagTextSimple(String text) {
        LOGGER.debug("Running simple tagger on raw text.");
        String result = NLPTools.tagTextSimple(text);
        return CompletableFuture.completedFuture(result);
    }

    /**
     * @param textDto a textDto with the text to lemmatize and optional custom stop words.
     * @return a sorted freqdist of the top 20 results.
     */
    @Async
    public CompletableFuture<Map<String, Integer>> frequencyDistribution(TextDto textDto) {
        LOGGER.debug("Running frequency distribution (raw text.)");
        CompletableFuture<List<String>> strings = pipeline.getListOfLemmatizedWords(textDto);
        return strings.thenApplyAsync(freqDist::getFrequency);
    }

    /**
     * @param corporaId   a corpora db id.
     * @param stopWordsId a stop words id.
     * @return a list of lemmatized strings.
     */
    @Async
    public CompletableFuture<Map<String, Integer>> frequencyDistribution(String corporaId, String stopWordsId) {
        LOGGER.debug("Running frequency distribution (corpora): " + corporaId);
        TextDto dto = new TextDto();
        CustomStopWords customStopWords;
        if (Integer.parseInt(stopWordsId) != 0) {
            customStopWords = getObjectOrThrowNullPointer.stopWords(stopWordsId);
            dto.setCustomStopWords(customStopWords.getWords().toArray(new String[0]));
        }
        corporaService.getCorporaItems(corporaId).thenAccept(s -> dto.setText(parseCorporaItems(s)));
        CompletableFuture<List<String>> strings = pipeline.getListOfLemmatizedWords(dto);
        return strings.thenApplyAsync(freqDist::getFrequency);
    }

    /**
     * Runs a mallet topic model on arbitrary text.
     *
     * @param textDto the textDto with the text to analyze.
     * @return a Map where the key is an integer (0 = most likely, -1 = trimmed) and the key is a Map where the key
     * is the exact probability of the model and the value is the model.
     */
    @Async
    public CompletableFuture<Map<Integer, Map<Double, String>>> runMalletTopicModel(TextDto textDto) {
        LOGGER.debug("Running topic model (raw text).");
        return malletTools.topicModel(textDto.getText(), textDto.getNumberOfTopics())
                .orTimeout(60, TimeUnit.SECONDS);
    }

    /**
     * Runs a mallet topic model on corpora text.
     *
     * @param corporaId      a corpora db id.
     * @param numberOfTopics a number of topics.
     * @return a Map where the key is an integer (0 = most likely, -1 = trimmed) and the key is a Map where the key
     * is the exact probability of the model and the value is the model.
     */
    @Async
    public CompletableFuture runMalletTopicModel(String corporaId, int numberOfTopics) {
        LOGGER.debug("Running topic model (corpora): " + corporaId);
        CompletableFuture<Set<ItemOutDto>> items = corporaService.getCorporaItems(corporaId);
        return items.thenApply(this::parseCorporaItems)
                .thenApplyAsync(s -> malletTools.topicModel(s, numberOfTopics)).orTimeout(60, TimeUnit.SECONDS);
    }
}
