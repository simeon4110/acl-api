package com.sonnets.sonnet.services.search;

/**
 * Returns true if the string is neither null nor empty.
 */
@FunctionalInterface
public interface NullFieldParser {
    static boolean isNull(String s) {
        return s != null && !s.isEmpty();
    }

    String operation(String s);
}
