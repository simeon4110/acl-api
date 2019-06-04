package org.acl.database.services.search;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Stores all com.sonnets.sonnet.search related content (i.e. field names.)
 */
@SuppressWarnings("WeakerAccess")
public abstract class SearchConstants {
    private SearchConstants() {
        // Private to block default constructor.
    }

    public static final Path DOCS_PATH = Paths.get("/var/lucene/indexes");

    // Numeric range query settings.
    public static final int INT_DISTANCE = 20;

    // Result constants
    public static final int MAX_RESULT_SIZE = 100;
    public static final int FRAGMENT_SIZE = 75;
    public static final int MAX_FRAGMENTS = 5;

    // General field names.
    public static final String ID = "id";
    public static final String CATEGORY = "category";
    public static final String AUTHOR = "author";
    public static final String AUTHOR_FIRST_NAME = "firstName";
    public static final String AUTHOR_LAST_NAME = "lastName";
    public static final String YEAR = "publicationYear";
    public static final String PERIOD = "period";
    public static final String IS_PUBLIC = "isPublicDomain";
    public static final String TITLE = "title";
    public static final String TOPIC_MODEL = "topic_model";
    public static final String VERSION_TEXT = "version_text";
    public static final String BEST_FRAGMENT = "context";

    // Lucene fuzzy and phrase query constants.
    public static final int PRECISION_STEP = 2;
    public static final int PREFIX_LENGTH = 0; // How many chars are "fixed" to the front.
    public static final int EDIT_DISTANCE = 2; // Levenstein edit distance.
    public static final int SLOP = 2; // Words are separated by no more than 3 words.

    // Item specific field names.
    public static final String POEM_FORM = "poem_form"; // :todo: fix this improper naming convention.
    public static final String TEXT = "text";
    public static final String BOOK_TYPE = "book_type";
    public static final String BOOK_SECTION = "book_section";
    public static final String BOOK_CHARACTER = "book_character";
    public static final String BOOK_CHARACTER_FN = "character_first_name";
    public static final String BOOK_CHARACTER_LN = "character_last_name";
    public static final String BOOK_CHARACTER_SEX = "character_gender";
    public static final String BOOK_CHARACTER_DIALOG = "character_dialog";
    public static final String CHARACTER_DIALOG_BODY = "character_dialog.body";
    public static final String DIALOG_BODY = "dialog_body";
    public static final String NARRATOR = "narrator";
    public static final String PARENT_ID = "parentId";
    public static final String PARENT_TITLE = "parentTitle";
    public static final String CHAPTER_TITLE = "chapterTitle";
    public static final String OTHER_SUB_TYPE = "other_sub_type";
    public static final String OTHER_TEXT = "other_text";
    public static final String SOURCE_TITLE = "source_title";

    // Source field names.
    public static final String EDITION = "edition";
    public static final String PLACE_OF_PUB = "placeOfPublication";
    public static final String PUBLISHER = "publisher";
    public static final String DATE_OF_PUB = "dateOfPublication";
    public static final String JOURNAL_NAME = "journalName";
    public static final String DOI = "DOI";
    public static final String JOURNAL_VOL = "journalVolume";
    public static final String JOURNAL_ISSUE = "journalIssue";
    public static final String LANGUAGE = "language";

    // Poem delimiter
    public static final String LINE_DELIMITER_POEM = " / ";
    /**
     * Used to add a leading period to field names; used in bridges.
     *
     * @param toAdd string to add a leading period to.
     * @return string with leading period added.
     */
    public static String addDot(final String toAdd) {
        return "." + toAdd;
    }
}

