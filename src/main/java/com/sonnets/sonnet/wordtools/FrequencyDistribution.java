package com.sonnets.sonnet.wordtools;

import java.util.*;

/**
 * An auto-lemmatized frequency distribution.
 *
 * @author Josh Harkema
 */
public abstract class FrequencyDistribution {
    private static final int MAX_RETURN = 20; // the total number of results to return
    private static Map<String, Integer> frequency = new HashMap<>();

    private FrequencyDistribution() {
    }

    /**
     * @param toScan the raw text to run a freqdist on.
     * @return a map where key = the word and value = the count.
     */
    public static Map<String, Integer> getFrequency(List<String> toScan) {
        for (String s : toScan) {
            s = s.toLowerCase();
            int wordFrequency = frequencyOf(s, toScan);
            if (wordFrequency != -1) {
                frequency.put(s, wordFrequency);
            }
        }
        return sortByValue(frequency);
    }

    /**
     * Counts the incidence of a given word, ensures word has not already been counted.
     *
     * @param key     the word to count.
     * @param strings the complete list of string to look in.
     * @return the count or -1 if the word already exists in the frequency map.
     */
    private static int frequencyOf(String key, List<String> strings) {
        int count = 0;
        if (!frequency.containsKey(key)) {
            for (String s : strings) {
                if (s.equals(key)) {
                    count++;
                }
            }
            return count;
        }
        return -1;
    }

    /**
     * Sorts a map by value, returns only the top MAX_RETURN results.
     *
     * @param map the map to sort.
     * @param <K> the keys.
     * @param <V> the values.
     * @return a sorted map limited to MAX_RETURN values.
     */
    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        list = list.subList(list.size() - MAX_RETURN, list.size());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
