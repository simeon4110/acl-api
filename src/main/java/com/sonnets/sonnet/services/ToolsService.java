package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.base.TextDto;
import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.Section;
import com.sonnets.sonnet.persistence.models.web.Corpora;
import com.sonnets.sonnet.persistence.models.web.CustomStopWords;
import com.sonnets.sonnet.services.helpers.GetObjectOrThrowNullPointer;
import com.sonnets.sonnet.wordtools.FrequencyDistribution;
import com.sonnets.sonnet.wordtools.MalletTools;
import com.sonnets.sonnet.wordtools.NLPTools;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Autowired
    public ToolsService(GetObjectOrThrowNullPointer getObjectOrThrowNullPointer) {
        this.getObjectOrThrowNullPointer = getObjectOrThrowNullPointer;
    }

    /**
     * Helper method for stripping a corpora into raw text.
     *
     * @param items the items to strip.
     * @return a string of the combined item's text.
     */
    private static String parseCorporaItems(final Set<Item> items) {
        StringBuilder sb = new StringBuilder();
        Consumer<Item> itemConsumer = item -> { // Function strips items into text only.
            if (item instanceof Poem) {
                ((Poem) item).getText().forEach(s -> sb.append(s).append(" "));
            } else if (item instanceof Section) {
                sb.append(((Section) item).getText()).append(" ");
            } else if (item instanceof Book) {
                ((Book) item).getSections().forEach(s -> sb.append(s.getText()).append(" "));
            } else {
                LOGGER.error("Unhandled type in corpora. Text not copied.");
            }
        };
        items.forEach(itemConsumer);
        return sb.toString();
    }

    /**
     * @param text a textDto with the text to lemmatize and optional custom stop words.
     * @return a list of lemmatized strings.
     */
    public List<String> lemmatizeText(TextDto text) {
        LOGGER.debug("Running text lemmatizer (raw text.)");
        return pipeline.getListOfLemmatizedWords(text);
    }

    /**
     * @param corporaId   a corpora db id.
     * @param stopWordsId a stop words id.
     * @return a list of lemmatized strings.
     */
    public List<String> lemmatizeText(String corporaId, String stopWordsId) {
        LOGGER.debug("Running text lemmatizer (corpora): " + corporaId);
        TextDto dto = new TextDto();
        Corpora corpora = getObjectOrThrowNullPointer.corpora(corporaId);
        CustomStopWords customStopWords;
        if (Integer.parseInt(stopWordsId) != 0) {
            customStopWords = getObjectOrThrowNullPointer.stopWords(stopWordsId);
            dto.setCustomStopWords(customStopWords.getWords().toArray(new String[0]));
        }
        dto.setText(parseCorporaItems(corpora.getItems()));
        return pipeline.getListOfLemmatizedWords(dto);
    }

    /**
     * @param text a textDto with some text to tag.
     * @return a JSON index of all the tags.
     */
    public String tagTextSimple(TextDto text) {
        LOGGER.debug("Running simple tagger.");
        return pipeline.tagTextSimple(text);
    }

    /**
     * @param textDto a textDto with the text to lemmatize and optional custom stop words.
     * @return a sorted freqdist of the top 20 results.
     */
    public Map<String, Integer> frequencyDistribution(TextDto textDto) {
        LOGGER.debug("Running frequency distribution (raw text.)");
        List<String> strings = pipeline.getListOfLemmatizedWords(textDto);
        return freqDist.getFrequency(strings);
    }

    /**
     * @param corporaId   a corpora db id.
     * @param stopWordsId a stop words id.
     * @return a list of lemmatized strings.
     */
    public Map<String, Integer> frequencyDistribution(String corporaId, String stopWordsId) {
        LOGGER.debug("Running frequency distribution (corpora): " + corporaId);
        TextDto dto = new TextDto();
        Corpora corpora = getObjectOrThrowNullPointer.corpora(corporaId);
        CustomStopWords customStopWords;
        if (Integer.parseInt(stopWordsId) != 0) {
            customStopWords = getObjectOrThrowNullPointer.stopWords(stopWordsId);
            dto.setCustomStopWords(customStopWords.getWords().toArray(new String[0]));
        }
        dto.setText(parseCorporaItems(corpora.getItems()));
        List<String> strings = pipeline.getListOfLemmatizedWords(dto);
        return freqDist.getFrequency(strings);
    }

    /**
     * Runs a mallet topic model on arbitrary text.
     *
     * @param textDto the textDto with the text to analyze.
     * @return a Map where the key is an integer (0 = most likely, -1 = trimmed) and the key is a Map where the key
     * is the exact probability of the model and the value is the model.
     */
    public Map<Integer, Map<Double, String>> runMalletTopicModel(TextDto textDto) {
        LOGGER.debug("Running topic model (raw text).");
        try {
            return malletTools.topicModel(textDto.getText(), textDto.getNumberOfTopics());
        } catch (IOException e) {
            LOGGER.error(e);
            return null;
        }
    }

    /**
     * Runs a mallet topic model on corpora text.
     *
     * @param corporaId      a corpora db id.
     * @param numberOfTopics a number of topics.
     * @return a Map where the key is an integer (0 = most likely, -1 = trimmed) and the key is a Map where the key
     * is the exact probability of the model and the value is the model.
     */
    public Map<Integer, Map<Double, String>> runMalletTopicModel(String corporaId, int numberOfTopics) {
        LOGGER.debug("Running topic model (corpora): " + corporaId);
        Corpora corpora = getObjectOrThrowNullPointer.corpora(corporaId);
        try {
            return malletTools.topicModel(parseCorporaItems(corpora.getItems()), numberOfTopics);
        } catch (IOException e) {
            LOGGER.error(e);
            return null;
        }
    }
}
