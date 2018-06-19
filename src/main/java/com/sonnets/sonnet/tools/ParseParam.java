package com.sonnets.sonnet.tools;

/**
 * Tool class for parsing request params into query strings.
 *
 * @author Josh Harkema
 */
public abstract class ParseParam {
    private static final String SEPARATOR = "_";

    private ParseParam() {
        // empty by design
    }

    public static String parse(String toParse) {
        return toParse.replace(SEPARATOR, " ");
    }

}
