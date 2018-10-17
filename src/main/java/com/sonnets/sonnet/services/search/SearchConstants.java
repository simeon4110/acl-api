package com.sonnets.sonnet.services.search;

/**
 * Stores all search related content (i.e. field names.)
 */
public abstract class SearchConstants {
    private SearchConstants() {
        // Private to block default constructor.
    }
    // Numeric range query settings.
    public static final int INT_DISTANCE = 20;

    // General field names.
    public static final String CATEGORY = "category";
    public static final int PRECISION_STEP = 2;

    // Lucene fuzzy and phrase query constants.
    public static final int PREFIX_LENGTH = 0; // How many chars are "fixed" to the front.
    public static final int EDIT_DISTANCE = 2; // Levenstein edit distance.
    public static final int SLOP = 1; // Words are directly adjacent.
    public static final String AUTHOR = "author";
    public static final String AUTHOR_FIRST_NAME = "author.firstName";
    public static final String AUTHOR_LAST_NAME = "author.lastName";
    public static final String YEAR = "publicationYear";
    public static final String TITLE = "title";
    public static final String PERIOD = "period";
    public static final String SOURCE = "source";

    // Item specific field names.
    public static final String POEM_FORM = "poem_form";
    public static final String TEXT = "text";
    public static final String BOOK_TYPE = "book_type";
    public static final String BOOK_CHARACTER_FN = "character_first_name";
    public static final String BOOK_CHARACTER_LN = "character_last_name";
    public static final String BOOK_CHARACTER_SEX = "character_gender";
    public static final String CHARACTER_DIALOG = "character_dialog.body";
    public static final String DIALOG_BODY = "dialog_body";
    public static final String NARRATOR = "narrator";

    // Analyzer Names
    public static final String TEXT_ANALYZER = "textAnalyzer";
}

