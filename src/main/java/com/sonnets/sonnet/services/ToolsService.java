package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.TextDto;
import com.sonnets.sonnet.wordtools.FrequencyDistribution;
import com.sonnets.sonnet.wordtools.KWIC;
import com.sonnets.sonnet.wordtools.NLPTools;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * This service coordinates all the NLP and other tools methods.
 *
 * @author Josh Harkema
 */
@Service
public class ToolsService {
    private static final Logger LOGGER = Logger.getLogger(ToolsService.class);
    private static final NLPTools pipeline = NLPTools.getInstance();
    private final SonnetDetailsService sonnetDetailsService;

    @Autowired
    public ToolsService(SonnetDetailsService sonnetDetailsService) {
        this.sonnetDetailsService = sonnetDetailsService;
    }

    /**
     * @param ids    the sonnets to run kwic on.
     * @param word   the word to look for.
     * @param length the number of words on either side to return.
     * @return a kwic map.
     */
    public List<Map.Entry<String, String>> kwicSonnets(String[] ids, String word, int length) {
        LOGGER.debug("Running sonnet KWIC.");
        return KWIC.searchByWord(sonnetStripper(ids), word, length);
    }

    /**
     * @param ids the sonnets to lemmatize.
     * @return a list of lemmatized words.
     */
    public List<String> lemmatizeSonnets(String[] ids) {
        LOGGER.debug("Running sonnet lemmatizer.");
        String text = sonnetStripper(ids);
        TextDto textDto = new TextDto();
        textDto.setText(text);
        return pipeline.getListOfLemmatizedWords(textDto);
    }

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
     * @param ids the ids to run the freqdist on.
     * @return a sorted freqdist of the top 20 results.
     */
    public Map<String, Integer> frequencyDistribution(String[] ids) {
        TextDto textDto = new TextDto();
        textDto.setText(sonnetStripper(ids));
        List<String> strings = pipeline.getListOfLemmatizedWords(textDto);
        return FrequencyDistribution.getFrequency(strings);
    }

    /**
     * @param ids the ids of the sonnets to strip down to text.
     * @return the sonnets stripped down to their text.
     */
    private String sonnetStripper(String[] ids) {
        StringBuilder sonnetText = new StringBuilder();
        for (String id : ids) {
            for (String text : sonnetDetailsService.getSonnetByID(id).getText()) {
                sonnetText.append(text);
                sonnetText.append(' ');
            }
        }
        return sonnetText.toString();
    }
}
