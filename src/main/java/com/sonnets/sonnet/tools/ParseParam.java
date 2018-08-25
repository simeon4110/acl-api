package com.sonnets.sonnet.tools;

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
