package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.base.TextDto;
import com.sonnets.sonnet.persistence.models.web.CustomStopWords;
import com.sonnets.sonnet.persistence.repositories.corpora.CorporaRepository;
import com.sonnets.sonnet.wordtools.FrequencyDistribution;
import com.sonnets.sonnet.wordtools.MalletTools;
import com.sonnets.sonnet.wordtools.NLPTools;
import com.sonnets.sonnet.wordtools.Normalization;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
    private final Normalization normalization;
    private final CorporaRepository corporaRepository;
    private final CustomStopWordsService stopWords;

    private static final int REQUEST_TIMEOUT = 60;

    @Autowired
    public ToolsService(CorporaRepository corporaRepository, CustomStopWordsService stopWords) {
        this.corporaRepository = corporaRepository;
        this.stopWords = stopWords;
        this.normalization = new Normalization();
    }

    private String parseCorporaItems(final String items) {
        StringBuilder result = new StringBuilder();
        JSONArray array;
        try {
            array = new JSONArray(items);
            for (int i = 0; i < array.length(); i++) {
                result.append(array.getJSONObject(i).getString("text"));
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
    public CompletableFuture<List<String>> lemmatizeText(Long corporaId, String stopWordsId) {
        LOGGER.debug("Running text lemmatizer (corpora): " + corporaId);
        TextDto dto = new TextDto();
        CustomStopWords customStopWords;
        if (Integer.parseInt(stopWordsId) != 0) {
            customStopWords = stopWords.getWordsListOrThrowNotFound(stopWordsId);
            dto.setCustomStopWords(customStopWords.getWords().toArray(new String[0]));
        }
        String items = corporaRepository.getCorporaItems(corporaId);
        dto.setText(parseCorporaItems(items));
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
    public CompletableFuture<Map<String, Integer>> frequencyDistribution(Long corporaId, String stopWordsId) {
        LOGGER.debug("Running frequency distribution (corpora): " + corporaId);
        TextDto dto = new TextDto();
        CustomStopWords customStopWords;
        if (Integer.parseInt(stopWordsId) != 0) {
            customStopWords = stopWords.getWordsListOrThrowNotFound(stopWordsId);
            dto.setCustomStopWords(customStopWords.getWords().toArray(new String[0]));
        }
        dto.setText(parseCorporaItems(corporaRepository.getCorporaItems(corporaId)));
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
    public CompletableFuture runMalletTopicModel(Long corporaId, int numberOfTopics) {
        LOGGER.debug("Running topic model (corpora): " + corporaId);
        return malletTools.topicModel(parseCorporaItems(corporaRepository.getCorporaItems(corporaId)), numberOfTopics)
                .orTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * @param text the text to normalize.
     * @return normalized text.
     */
    @Async
    public CompletableFuture<String> normalizeText(String text) {
        LOGGER.debug("Running normalization (text) on: " + text);
        return CompletableFuture.completedFuture(normalization.normalizeText(text));
    }
}
