package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.base.ItemOutDto;
import com.sonnets.sonnet.persistence.dtos.base.TextDto;
import com.sonnets.sonnet.persistence.models.web.CustomStopWords;
import com.sonnets.sonnet.services.exceptions.ItemNotFoundException;
import com.sonnets.sonnet.services.helpers.GetObjectOrThrowNullPointer;
import com.sonnets.sonnet.wordtools.FrequencyDistribution;
import com.sonnets.sonnet.wordtools.MalletTools;
import com.sonnets.sonnet.wordtools.NLPTools;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
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

    private static final int REQUEST_TIMEOUT = 60;

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
                case "SECT":
                    sb.append(item.getText());
                    break;
                default:
                    throw new ItemNotFoundException(String.format("Item type %s does not exist.", item.getCategory()));
            }
        };
        items.forEach(itemConsumer);
        return sb.toString();
    }

    private String parseCorporaItems(final String items) {
        StringBuilder result = new StringBuilder();
        JSONArray array = null;
        try {
            array = new JSONArray(items);
            for (int i = 0; i < array.length(); i++) {
                switch (array.getJSONObject(i).getString("item_type")) {
                    case "POEM":
                        result.append(array.getJSONObject(i).getString("poem_text"));
                        result.append(" ");
                        break;
                    case "SECT":
                        result.append(array.getJSONObject(i).getString("text"));
                        result.append(" ");
                        break;
                    default:
                        break;
                }
            }
        } catch (JSONException e) {
            LOGGER.error(e);
        }
        return result.toString();
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
        dto.setText(parseCorporaItems(corporaService.getCorporaItems(corporaId)));
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
        return strings.thenApply(freqDist::getFrequency);
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
        dto.setText(parseCorporaItems(corporaService.getCorporaItems(corporaId)));
        CompletableFuture<List<String>> strings = pipeline.getListOfLemmatizedWords(dto);
        return strings.thenApply(freqDist::getFrequency);
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
                .orTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
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
        return malletTools.topicModel(parseCorporaItems(corporaService.getCorporaItems(corporaId)), numberOfTopics)
                .orTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
    }
}
