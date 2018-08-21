package com.sonnets.sonnet.wordtools;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.StringArrayIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Interface for MalletTools. Adapted from: http://mallet.cs.umass.edu/topics-devel.php
 *
 * @author Josh Harkema
 */
public interface MalletTools {
    Logger LOGGER = Logger.getLogger(MalletTools.class);
    double ALPHA_SUM = 1.0;
    double BETA = 0.01;
    int NUM_THREADS = 2;
    int NUM_ITERATIONS = 2000;
    int TOP_WORDS = 5;

    /**
     * Execute a very simple LDA topic model.
     *
     * @param text           the text to run the model for.
     * @param numberOfTopics the number of topics to run.
     * @return an array of topics where item 0 is the most likely.
     * @throws IOException if the stop words list cannot be found.
     */
    static String topicModel(final String text, final int numberOfTopics) throws IOException {
        ArrayList<Pipe> pipeList = new ArrayList<>();
        String cleanText = text.replace("\n", " ").replaceAll("\\p{Punct}", "");
        String[] strings = cleanText.toLowerCase().split(" "); // Split input into a String[].

        // Pipes added: tokenize, remove stop words, map to features.
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("[\\p{L}\\p{N}_]+")));
        pipeList.add(new TokenSequenceRemoveStopwords(new File("src/main/resources/en.txt"),
                "UTF-8", false, false, false));
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

        // Log some stats I don't understand.
        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
        LabelSequence topics = model.getData().get(0).topicSequence;
        Formatter out = new Formatter(new StringBuilder(), Locale.US);
        for (int position = 0; position < tokens.getLength(); position++) {
            out.format("%s-%d ", alphabet.lookupObject(tokens.getIndexAtPosition(position)), topics.getIndexAtPosition(position));
            LOGGER.debug(out);
        }

        double[] topicDistribution = model.getTopicProbabilities(0);
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();

        // Log the sequence and probabilities after each 10 generations.
        for (int i = 0; i < numberOfTopics; i++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(i).iterator();
            out = new Formatter(new StringBuilder(), Locale.US);
            out.format("%d\t%.3f\t", i, topicDistribution[i]);
            int rank = 0;
            while (iterator.hasNext() && rank < TOP_WORDS) {
                IDSorter idCountPair = iterator.next();
                out.format("%s (%.0f) ", alphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
                rank++;
            }
            LOGGER.debug(out);
        }

        // Create a new instance with high probability of topic 0
        StringBuilder topicZeroText = new StringBuilder();
        Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

        int rank = 0;
        while (iterator.hasNext() && rank < 5) {
            IDSorter idCountPair = iterator.next();
            topicZeroText.append(alphabet.lookupObject(idCountPair.getID())).append(" ");
            rank++;
        }

        // Create a new instance named "test instance" with empty target and source fields.
        InstanceList testing = new InstanceList(instances.getPipe());
        testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));

        TopicInferencer inferencer = model.getInferencer();
        double[] testProbabilities = inferencer.getSampledDistribution(
                testing.get(0), 10, 1, 5
        );
        LOGGER.debug("0\t" + testProbabilities[0]);
        return null;
    }
}
