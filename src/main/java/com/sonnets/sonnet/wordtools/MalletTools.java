package com.sonnets.sonnet.wordtools;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.iterator.StringArrayIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.IDSorter;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import com.sonnets.sonnet.services.exceptions.TopicModelException;
import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * Singleton for MalletTools. Adapted from: http://mallet.cs.umass.edu/topics-devel.php
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
    private static final int FINAL_ITERATIONS = 10;
    private static final int FINAL_THINNING = 1;
    private static final int FINAL_BURN_IN = 5;

    private static final MalletTools ourInstance = new MalletTools();
    private static final NLPTools nlpTools = NLPTools.getInstance();

    public static MalletTools getInstance() {
        return ourInstance;
    }

    private MalletTools() {
    }

    /**
     * Execute a very simple LDA topic model.
     *
     * @param text           the text to run the model for.
     * @param numberOfTopics the number of topics to run.
     * @return an array of topics where item 0 is the most likely.
     */
    @Async
    public CompletableFuture<Map<Integer, Map<Double, String>>> topicModel(final String text,
                                                                           final int numberOfTopics) {
        // Run the words through the lemmatizer and stop word filter.
        String cleanText = nlpTools.getLemmatizedWords(text);

        ArrayList<Pipe> pipeList = new ArrayList<>();
        String[] strings = cleanText.split(" ");
        strings = Arrays.stream(strings).map(String::toLowerCase).toArray(String[]::new);
        strings = Arrays.stream(strings).filter(x -> x.length() > 1).toArray(String[]::new);

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

        try {
            model.estimate();
        } catch (IOException e) {
            throw new TopicModelException(e);
        }

        // Init a map for output data.
        Map<Integer, Map<Double, String>> resultMap = new HashMap<>();

        // Acts as a lookup for result data.
        Alphabet dataAlphabet = instances.getDataAlphabet();

        // Estimate the topic distribution of the first instance,
        //  given the current Gibbs state.
        double[] topicDistribution = model.getTopicProbabilities(0);

        // Get an array of sorted sets of word ID/count pairs
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();

        // Show top 5 words in topics with proportions for the first document
        for (int topic = 0; topic < numberOfTopics; topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

            StringBuilder outString = new StringBuilder();
            int rank = 0;
            while (iterator.hasNext() && rank < TOP_WORDS) {
                IDSorter idCountPair = iterator.next();
                outString.append(String.format("%s (%.0f) ", dataAlphabet.lookupObject(idCountPair.getID()),
                        idCountPair.getWeight()));
                rank++;
            }

            Map<Double, String> item = new HashMap<>();
            item.put(topicDistribution[topic], outString.toString());
            resultMap.put(topic, item);
        }

        // Create a new instance with high probability of topic 0
        StringBuilder topicZeroText = new StringBuilder();
        Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

        int rank = 0;
        while (iterator.hasNext() && rank < TOP_WORDS) {
            IDSorter idCountPair = iterator.next();
            topicZeroText.append(dataAlphabet.lookupObject(idCountPair.getID()));
            topicZeroText.append(" ");
            rank++;
        }

        // Create a new instance named "test instance" with empty target and source fields.
        InstanceList testing = new InstanceList(instances.getPipe());
        testing.addThruPipe(new Instance(topicZeroText.toString(), null, "test instance", null));

        TopicInferencer inferencer = model.getInferencer();
        double[] testProbabilities = inferencer.getSampledDistribution(
                testing.get(0), FINAL_ITERATIONS, FINAL_THINNING, FINAL_BURN_IN);
        Map<Double, String> zero = new HashMap<>();
        zero.put(testProbabilities[0], topicZeroText.toString());

        resultMap.put(-1, zero);
        LOGGER.debug(resultMap.toString());
        return CompletableFuture.completedFuture(resultMap);
    }
}
