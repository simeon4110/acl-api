package com.sonnets.sonnet.tools;

/**
 * Tool interface for parsing request params into query strings.
 *
 * @author Josh Harkema
 */
public interface ParseParam {
    String SEPARATOR = "_";

    static String parse(String toParse) {
        return toParse.replace(SEPARATOR, " ").toLowerCase();
    }
}
