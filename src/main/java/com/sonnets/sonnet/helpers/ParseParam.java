package com.sonnets.sonnet.helpers;

/**
 * Tool interface for parsing request params into query strings.
 *
 * @author Josh Harkema
 */
public interface ParseParam {
    static String parse(String toParse) {
        return toParse.replace("_", " ").toLowerCase();
    }
}
