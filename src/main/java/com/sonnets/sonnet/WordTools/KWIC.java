package com.sonnets.sonnet.WordTools;

import java.util.*;

/**
 * Basic KWIC methods.
 *
 * @author Josh Harkema
 */
public abstract class KWIC {
    public KWIC() {
    }

    /**
     * @param text   the text to search.
     * @param word   the word to search for.
     * @param length the context length (each side)
     * @return a Map of the matches and context. The word is replaced with an '=' sign.
     */
    public static List<Map.Entry<String, String>> searchByWord(final String text, final String word,
                                                               final int length) {
        ArrayList<String> tokens = new ArrayList<>(Arrays.asList(text.replace("\n", " ")
                .split(" ")));

        if (!tokens.isEmpty() && tokens.contains(word)) {
            StringBuilder rightContext = new StringBuilder();
            StringBuilder leftContext = new StringBuilder();

            Map<String, String> pair = new HashMap<>();
            List<Map.Entry<String, String>> contexts = new ArrayList<>();
            final int lastIndex = tokens.size();

            for (int i = 0; i < lastIndex - 1; i++) {
                if (word.toLowerCase().equals(tokens.get(i).toLowerCase())) {
                    for (int j = i + 1; j < lastIndex && j - i <= length; j++) {
                        rightContext.append(tokens.get(j));
                        rightContext.append(' ');
                    }

                    for (int j = i - 1; j > 0 && i - j <= length; j--) {
                        leftContext.insert(0, tokens.get(j) + " ");
                    }

                    pair.put(leftContext.toString(), rightContext.toString());
                    contexts.addAll(pair.entrySet());
                    rightContext.setLength(0);
                    leftContext.setLength(0);
                    pair.clear();
                }
            }

            return contexts;
        }

        return Collections.emptyList();
    }

}
