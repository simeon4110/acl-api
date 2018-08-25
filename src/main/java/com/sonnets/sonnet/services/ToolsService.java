package com.sonnets.sonnet.services;

import com.sonnets.sonnet.persistence.dtos.base.TextDto;
import com.sonnets.sonnet.persistence.models.base.Item;
import com.sonnets.sonnet.persistence.models.poetry.Poem;
import com.sonnets.sonnet.persistence.models.prose.Book;
import com.sonnets.sonnet.persistence.models.prose.Section;
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
public class ToolsService implements FrequencyDistribution, KWIC {
    private static final Logger LOGGER = Logger.getLogger(ToolsService.class);
    private static final NLPTools pipeline = NLPTools.getInstance();
    private final ItemService itemService;

    @Autowired
    public ToolsService(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * @param ids    the items to run kwic on.
     * @param word   the word to look for.
     * @param length the number of words on either side to return.
     * @return a kwic map.
     */
    public List<Map.Entry<String, String>> kwic(String[] ids, String word, int length) {
        LOGGER.debug("Running KWIC.");
        return KWIC.searchByWord(itemStripper(ids), word, length);
    }

    /**
     * @param ids the items to lemmatize.
     * @return a list of lemmatized words.
     */
    public List<String> lemmatizeItems(String[] ids) {
        LOGGER.debug("Running item lemmatizer.");
        String text = itemStripper(ids);
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
        textDto.setText(itemStripper(ids));
        List<String> strings = pipeline.getListOfLemmatizedWords(textDto);
        return FrequencyDistribution.getFrequency(strings);
    }

    /**
     * @param ids the ids of the items to strip down to text.
     * @return the items stripped down to their text.
     */
    private String itemStripper(String[] ids) {
        StringBuilder text = new StringBuilder();
        for (String id : ids) {
            Item item = itemService.getItemById(id);
            switch (item.getCategory()) {
                case "POETRY":
                    Poem poem = (Poem) item;
                    for (String s : poem.getText()) {
                        text.append(s);
                        text.append(' ');
                    }
                    break;
                case "PROSE":
                    Book book = (Book) item;
                    for (Section section : book.getSections()) {
                        text.append(section.getText());
                    }
                    break;
                default:
                    break;
            }
        }
        return text.toString();
    }
}
