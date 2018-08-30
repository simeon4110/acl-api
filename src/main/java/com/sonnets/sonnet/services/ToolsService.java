package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.base.TextDto;
import com.sonnets.sonnet.wordtools.FrequencyDistribution;
import com.sonnets.sonnet.wordtools.KWIC;
import com.sonnets.sonnet.wordtools.MalletTools;
import com.sonnets.sonnet.wordtools.NLPTools;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This service coordinates all the NLP and other tools methods.
 *
 * @author Josh Harkema
 */
@Service
public class ToolsService implements KWIC {
    private static final Logger LOGGER = Logger.getLogger(ToolsService.class);
    private static final NLPTools pipeline = NLPTools.getInstance();
    private static final MalletTools malletTools = MalletTools.getInstance();

    /**
     * @param text a textDto with the text to lemmatize and optional custom stop words.
     * @return a list of lemmatized strings.
     */
    public List<String> lemmatizeText(TextDto text) {
        LOGGER.debug("Running text lemmatizer.");
        return pipeline.getListOfLemmatizedWords(text);
    }

    /**
     * @param textDto a textDto with the text to lemmatize and optional custom stop words.
     * @return a sorted freqdist of the top 20 results.
     */
    public Map<String, Integer> frequencyDistribution(TextDto textDto) {
        List<String> strings = pipeline.getListOfLemmatizedWords(textDto);
        return FrequencyDistribution.getFrequency(strings);
    }

    /**
     * Runs a mallet topic model on arbitrary text.
     *
     * @param textDto the textDto with the text to analyze.
     * @return a Map where the key is an integer (0 = most likely, -1 = trimmed) and the key is a Map where the key
     * is the exact probability of the model and the value is the model.
     */
    public Map<Integer, Map<Double, String>> runMalletTopicModel(TextDto textDto) {
        LOGGER.debug("Running topic model on: " + textDto.getText());
        try {
            return malletTools.topicModel(textDto.getText(), textDto.getNumberOfTopics());
        } catch (IOException e) {
            LOGGER.error(e);
            return null;
        }
    }
}
