package com.sonnets.sonnet.wordtools;

import com.sonnets.sonnet.persistence.dtos.base.TextDto;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tools.FormatTools;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Tools singleton for interacting with the NLP.
 *
 * @author Josh Harkema.
 */
@Component
public class NLPTools {
    private static final NLPTools ourInstance = new NLPTools();
    public static NLPTools getInstance() {
        return ourInstance;
    }

    /**
     * This little method is a gosh-darned miracle.
     *
     * @param textDto a textDto containing the text to tag.
     * @return tagged text.
     */
    public static String tagTextSimple(TextDto textDto) {
        String text = textDto.getText();
        Document document = new Document(text);
        return document.json(Sentence::lemmas);
    }

    /**
     * Runs without the need for a DTO.
     *
     * @param string the text to tag.
     * @return tagged text.
     */
    public static String tagTextSimple(String string) {
        Document document = new Document(string);
        return document.json(Sentence::lemmas);
    }

    /**
     * This method is a fast way to lemmatize a large volume of text.
     *
     * @param textDto the text and optional list of custom stop words.
     * @return a cleaned up list of lemmatize words.
     */
    @Async
    public CompletableFuture<List<String>> getListOfLemmatizedWords(TextDto textDto) {
        String text = textDto.getText();
        text = FormatTools.removePunctuation(text);
        Document document = new Document(text);
        List<String> result = new ArrayList<>();

        for (Sentence sentence : document.sentences()) {
            for (int i = 0; i < sentence.length() - 1; i++) {
                if (!FormatTools.containsStopWords(sentence.word(i).toLowerCase())) {
                    result.add(sentence.lemma(i));
                }
            }
        }
        return CompletableFuture.completedFuture(result);
    }

    String getLemmatizedWords(String textToLemmatize) {
        Document document = new Document(textToLemmatize);
        StringBuilder result = new StringBuilder();

        for (Sentence sentence : document.sentences()) {
            for (int i = 0; i < sentence.length() - 1; i++) {
                if (!FormatTools.containsStopWords(sentence.lemma(i).toLowerCase().trim())) {
                    result.append(sentence.lemma(i));
                    result.append(" ");
                }
            }
        }
        return result.toString();
    }
}
