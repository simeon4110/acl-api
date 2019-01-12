package com.sonnets.sonnet.persistence.models;

import java.io.Serializable;

/**
 * Item type constants are stored here.
 *
 * @author Josh Harkema
 */
public abstract class TypeConstants implements Serializable {
    public static final String POEM = "POEM";
    public static final String BOOK = "BOOK";
    public static final String SECTION = "SECT";
    public static final String OTHER = "OTHR";
    public static final String BOOK_CHARACTER = "BKCR";
    public static final String DIALOG = "DIAL";
    private static final long serialVersionUID = 7429821152492460382L;

    private TypeConstants() {
    }
}
