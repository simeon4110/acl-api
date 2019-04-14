package com.sonnets.sonnet.persistence.models;

import java.io.Serializable;

/**
 * Item type constants are stored here.
 *
 * @author Josh Harkema
 */
public abstract class TypeConstants implements Serializable {
    public static final String AUTHOR = "AUTH";
    public static final String NOTE = "NOTE";
    public static final String POEM = "POEM";
    public static final String BOOK = "BOOK";
    public static final String SECTION = "SECT";
    public static final String OTHER = "OTHR";
    private static final long serialVersionUID = 7429821152492460382L;
    public static final String BOOK_CHARACTER = "BKCR";
    public static final String DIALOG = "DIAL";
    public static final String WORD_TRANSLATION = "WTRA";
    public static final String SHORT_STORY = "SHST";

    private TypeConstants() {
    }
}
