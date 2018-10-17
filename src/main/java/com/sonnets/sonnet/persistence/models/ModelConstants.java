package com.sonnets.sonnet.persistence.models;

/**
 * Contains constants for stored procedures and item types.
 */
public abstract class ModelConstants {
    private ModelConstants() {
        // To block implicit public constructor.
    }

    public static final String TYPE_DIALOG = "DIAL";
    public static final String TYPE_POEM = "POEM";
    public static final String TYPE_BOOK = "BOOK";
    public static final String TYPE_SECTION = "SECT";
    public static final String TYPE_CHARACTER = "CHAR";

    // Named Queries
    // Section
    public static final String GET_ALL_SECTIONS = "getAllSections";
    public static final String GET_ALL_SECTIONS_PROCEDURE = "get_all_sections";

    // Poem
    public static final String GET_ALL_POEMS = "getAllPoemsManual";
    public static final String GET_ALL_POEMS_PROCEDURE = "get_all_poems";
    public static final String GET_RANDOM_POEM = "getRandomPoem";
    public static final String GET_RANDOM_POEM_PROCEDURE = "get_random_poem";
    public static final String GET_POEMS_BY_USER = "getPoemsByUser";
    public static final String GET_POEMS_BY_USER_PROCEDURE = "get_user_poems";
}
