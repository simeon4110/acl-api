package com.sonnets.sonnet.wordtools;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.StringArrayIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.*;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Interface for MalletTools. Adapted from: http://mallet.cs.umass.edu/topics-devel.php
 *
 * @author Josh Harkema
 */
public class MalletTools {
    private static final Logger LOGGER = Logger.getLogger(MalletTools.class);
    private static final double ALPHA_SUM = 1.0D;
    private static final double BETA = 0.01D;
    private static final int NUM_THREADS = 2;
    private static final int NUM_ITERATIONS = 1500; // 1500 - 2000 is best.
    private static final int TOP_WORDS = 5;

    /**
     * Execute a very simple LDA topic model.
     *
     * @param text           the text to run the model for.
     * @param numberOfTopics the number of topics to run.
     * @return an array of topics where item 0 is the most likely.
     * @throws IOException if the stop words list cannot be found.
     */
    @Async
    public Map<Integer, Map<Double, String>> topicModel(final String text, final int numberOfTopics)
            throws IOException {
        // Run the words through the lemmatizer and stop word filter.
        NLPTools nlpTools = NLPTools.getInstance();
        String cleanText = nlpTools.getLemmatizedWords(text);

        ArrayList<Pipe> pipeList = new ArrayList<>();
        String[] strings = cleanText.toLowerCase().split(" "); // Split input into a String[].

        // Pipes added: tokenize, remove stop words, map to features.
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("[\\p{L}\\p{N}_]+")));
        pipeList.add(new TokenSequence2FeatureSequence());

        // Add everything to an interface and feed the string reader into it.
        InstanceList instances = new InstanceList(new SerialPipes(pipeList));
        instances.addThruPipe(new StringArrayIterator(strings));
        ParallelTopicModel model = new ParallelTopicModel(numberOfTopics, ALPHA_SUM, BETA);

        // Run the model.
        model.addInstances(instances);
        model.setNumThreads(NUM_THREADS);
        model.setNumIterations(NUM_ITERATIONS);
        model.estimate();

        // Get the total number of words(and how many occurrences of each).
        Alphabet alphabet = instances.getAlphabet();

        // Log some stats I don't fully understand.
        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
        LabelSequence topics = model.getData().get(0).topicSequence;
        Formatter out = new Formatter(new StringBuilder(), Locale.US);
        for (int position = 0; position < tokens.getLength(); position++) {
            out.format("%s-%d ", alphabet.lookupObject(tokens.getIndexAtPosition(position)),
                    topics.getIndexAtPosition(position));
            LOGGER.debug(out);
        }

        double[] topicDistribution = model.getTopicProbabilities(0);
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();
        Map<Integer, Map<Double, String>> resultMap = new HashMap<>();

        Iterator iterator;
        int rank;
        IDSorter idCountPair;
        Formatter outItem;
        // Log the sequence and probabilities after each 10 generations.
        for (int i = 0; i < numberOfTopics; i++) {
            iterator = ((TreeSet) topicSortedWords.get(i)).iterator();
            out = new Formatter(new StringBuilder(), Locale.US);
            outItem = new Formatter(new StringBuilder(), Locale.US);
            out.format("%.3f\t -- ", topicDistribution[i]);
            for (rank = 0; iterator.hasNext() && rank < 5; ++rank) {
                idCountPair = (IDSorter) iterator.next();
                out.format("%s (%.0f) ", alphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
                outItem.format("%s (%.0f) ", alphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
            }
            Map<Double, String> item = new HashMap<>();
            item.put(topicDistribution[i], outItem.toString());
            resultMap.put(i, item);
        }

        // Create a new instance with high probability of topic 0
        StringBuilder topicZeroText = new StringBuilder();
        iterator = ((TreeSet) topicSortedWords.get(0)).iterator();

        for (rank = 0; iterator.hasNext() && rank < TOP_WORDS; ++rank) {
            idCountPair = (IDSorter) iterator.next();
            topicZeroText.append(alphabet.lookupObject(idCountPair.getID()));
        }

        // Create a new instance named "test instance" with empty target and source fields.
        InstanceList testing = new InstanceList(instances.getPipe());
        testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));

        TopicInferencer inferencer = model.getInferencer();
        double[] testProbabilities = inferencer.getSampledDistribution(
                testing.get(0), 10, 1, 5
        );

        // Place the total prob of item 0 in the hash map at key -1 and print the results for debug.
        Map<Double, String> itemZero = new HashMap<>();
        itemZero.put(testProbabilities[0], "FINAL");
        resultMap.put(-1, itemZero);
        return resultMap;
    }
}
