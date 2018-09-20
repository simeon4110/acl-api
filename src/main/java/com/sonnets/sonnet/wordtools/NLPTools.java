package com.sonnets.sonnet.wordtools;

import com.sonnets.sonnet.persistence.dtos.base.TextDto;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

/**
 * Tools singleton for interacting with the NLP.
 *
 * @author Josh Harkema.
 */
@Component
public class NLPTools {
    private static final List<String> STOP_WORDS = Arrays.asList(
            "been", "those", "how", "nor", "m", "hadn", "herself", "up", "its", "I", "like", "shall", "let",
            "were", "into", "such", "will", "won", "down", "shouldn", "our", "to", "didn", "yourself", "mightn",
            "itself", "once", "them", "both", "needn", "theirs", "under", "wasn't", "haven", "now", "is", "we", "hers",
            "too", "for", "that'll", "very", "weren't", "her", "can", "she's", "ours", "off", "aren't", "own", "your",
            "more", "wouldn't", "but", "did", "any", "wouldn", "you", "again", "it", "who", "am", "not", "further",
            "hadn't", "needn't", "that", "these", "before", "in", "couldn", "doing", "hasn't", "until", "my", "by",
            "ll", "same", "there", "should've", "does", "him", "you'd", "if", "hasn", "was", "she", "they", "me",
            "what", "s", "out", "i", "has", "all", "you'll", "which", "t", "some", "only", "so", "while", "on",
            "mustn't", "through", "after", "himself", "than", "won't", "y", "have", "with", "should", "or", "most",
            "above", "just", "as", "why", "you're", "against", "mustn", "then", "yours", "few", "each", "whom", "be",
            "o", "haven't", "didn't", "ve", "from", "shan", "their", "doesn", "mightn't", "re", "here", "over", "no",
            "themselves", "being", "a", "doesn't", "you've", "about", "couldn't", "having", "during", "had", "weren",
            "do", "shouldn't", "at", "shan't", "an", "myself", "d", "wasn", "don", "of", "isn", "ourselves", "between",
            "yourselves", "don't", "when", "ain", "ma", "are", "he", "the", "because", "his", "below", "isn't", "and",
            "other", "aren", "where", "this", "it's", "'s", "--", "'", "``", "''", "one", "would", "could", "may",
            "every", "make", "upon", "thy", "have", "doth", "ah", "thou"
    );
    private static final StanfordCoreNLP pipeline = new StanfordCoreNLP(setProperties()); // For complex analysis.
    private static final NLPTools ourInstance = new NLPTools();

    public static NLPTools getInstance() {
        return ourInstance;
    }

    private static Properties setProperties() {
        Properties localProperties = new Properties();
        localProperties.setProperty("annotators", "tokenize,ssplit,pos,lemma");
        localProperties.setProperty("parse.model",
                "edu/stanford/nlp/models/pos-tagger/english-left3words/wsj-0-18-left3words-distsim.tagger");
        return localProperties;
    }

    /**
     * Parses text into a CoreDocument and strips all newlines.
     *
     * @param text the string to load and strip.
     * @return the loaded and stripped CoreDocument.
     */
    public static CoreDocument loadDocument(String text) {
        text = text.replace(",", "");
        text = text.replace(";", "");
        text = text.replace(":", "");
        text = text.replace("'s", "");
        text = text.replace("\n", " ");
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);
        return document;
    }

    /**
     * This little method is a gosh-darned miracle.
     *
     * @param textDto a textDto containing the text to tag.
     * @return tagged text.
     */
    public String tagTextSimple(TextDto textDto) {
        String text = textDto.getText();
        Document document = new Document(text);
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
        List<String> localStopWords;
        text = text.replace("-", " ");
        text = text.replaceAll("\\p{Punct}", "");
        text = text.replaceAll("[0-9]", "");
        text = text.replace("\n", " ");

        if (textDto.getCustomStopWords() != null) {
            localStopWords = Arrays.asList(textDto.getCustomStopWords());
        } else {
            localStopWords = STOP_WORDS;
        }

        Document document = new Document(text);
        List<String> result = new ArrayList<>();

        for (Sentence sentence : document.sentences()) {
            for (int i = 0; i < sentence.length() - 1; i++) {
                if (!localStopWords.contains(sentence.word(i).toLowerCase())) {
                    result.add(sentence.lemma(i));
                }
            }
        }
        return CompletableFuture.completedFuture(result);
    }

    public String getLemmatizedWords(String textToLemmatize) {
        textToLemmatize = textToLemmatize.replace("-", " ");
        textToLemmatize = textToLemmatize.replaceAll("\\p{Punct}", "");
        textToLemmatize = textToLemmatize.replaceAll("[0-9]", "");
        textToLemmatize = textToLemmatize.replace("\n", " ");

        Document document = new Document(textToLemmatize);
        StringBuilder result = new StringBuilder();

        for (Sentence sentence : document.sentences()) {
            for (int i = 0; i < sentence.length() - 1; i++) {
                if (!STOP_WORDS.contains(sentence.word(i).toLowerCase()) && sentence.nerTag(i).equals("O")) {
                    result.append(sentence.word(i));
                    result.append(" ");
                }
            }
        }
        return result.toString();
    }
}
