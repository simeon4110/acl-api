package com.sonnets.sonnet.wordtools;

import com.sonnets.sonnet.persistence.dtos.base.TextDto;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
    private static StanfordCoreNLP pipeline = new StanfordCoreNLP(setProperties()); // For complex analysis.
    private static NLPTools ourInstance = new NLPTools();

    public static NLPTools getInstance() {
        return ourInstance;
    }

    private static Properties setProperties() {
        Properties localProperties = new Properties();
        localProperties.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        localProperties.setProperty("coref.algorithm", "neural");
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
     * THIS IS REALLY SLOW!! DON'T USE IT.
     *
     * @param text           the text to analyze.
     * @param annotationType the type of annotation wanted.
     * @return the annotations.
     */
    public List<List<String>> getAnnotations(String text, AnnotationType annotationType) {
        text = text.replace(",", "");
        text = text.replace(";", "");
        text = text.replace(":", "");
        text = text.replace("'s", "");
        text = text.replace("\n", " ");
        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        List<List<String>> annotations = new ArrayList<>();

        for (CoreMap sentence : sentences) {
            List<String> sentArr = new ArrayList<>();
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                if (annotationType.equals(AnnotationType.PART_OF_SPEECH)) {
                    sentArr.add(token.get(CoreAnnotations.PartOfSpeechAnnotation.class));
                }
                if (annotationType.equals(AnnotationType.LEMMA)) {
                    sentArr.add(token.get(CoreAnnotations.LemmaAnnotation.class));
                }
                if (annotationType.equals(AnnotationType.NAMED_ENTITY)) {
                    sentArr.add(token.get(CoreAnnotations.NamedEntityTagAnnotation.class));
                }
                if (annotationType.equals(AnnotationType.SENTIMENT)) {
                    sentArr.add(token.get(SentimentCoreAnnotations.SentimentClass.class));
                }
            }
            annotations.add(sentArr);
        }
        return annotations;
    }

    /**
     * This method is a fast way to lemmatize a large volume of text.
     *
     * @param textDto the text and optional list of custom stop words.
     * @return a cleaned up list of lemmatize words.
     */
    public List<String> getListOfLemmatizedWords(TextDto textDto) {
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
        return result;
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
                    System.out.println(sentence.word(i));
                    result.append(sentence.word(i));
                    result.append(" ");
                }
            }
        }
        return result.toString();
    }

    /**
     * Automatic NER (Named Entity Recognition) tagging.
     *
     * @param text the String to tag.
     * @return a list of results.
     */
    public List<List<String>> nerTag(String text) {
        CoreDocument document = loadDocument(text);
        List<List<String>> results = new ArrayList<>();
        for (int i = 0; i < document.sentences().size() - 1; i++) {
            results.add(document.sentences().get(i).nerTags());
        }
        return results;
    }

    public enum AnnotationType {
        PART_OF_SPEECH,
        LEMMA,
        NAMED_ENTITY,
        SENTIMENT
    }
}
