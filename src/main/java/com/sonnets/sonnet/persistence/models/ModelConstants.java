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

    // Items
    public static final String GET_USER_ITEMS = "getUserItems";
    public static final String GET_USER_ITEMS_PROCEDURE = "get_user_items";
    public static final String GET_ALL_ITEMS = "getAllItems";
    public static final String GET_ALL_ITEMS_PROCEDURE = "get_all_items";

    // Poem
    public static final String GET_ALL_POEMS = "getAllPoemsManual";
    public static final String GET_ALL_POEMS_PROCEDURE = "get_all_poems";
    public static final String GET_RANDOM_POEM = "getRandomPoem";
    public static final String GET_RANDOM_POEM_PROCEDURE = "get_random_poem";
    public static final String GET_POEMS_BY_USER = "getPoemsByUser";
    public static final String GET_POEMS_BY_USER_PROCEDURE = "get_user_poems";

    // Corpora
    public static final String ADD_CORPORA_ITEM = "addCorporaItem";
    public static final String ADD_CORPORA_ITEM_PROCEDURE = "add_corpora_item";
    public static final String GET_CORPORA = "getCorpora";
    public static final String GET_CORPORA_PROCEDURE = "get_corpora";
    public static final String GET_CORPORA_ITEMS = "getCorporaItems";
    public static final String GET_CORPORA_ITEMS_PROCEDURE = "get_corpora_items";
    public static final String GET_CORPORA_ITEMS_SIMPLE = "getCorporaItemsSimple";
    public static final String GET_CORPORA_ITEMS_SIMPLE_PROCEDURE = "get_corpora_items_simple";
    public static final String GET_CORPORA_USER = "getCorporaUser";
    public static final String GET_CORPORA_USER_PROCEDURE = "get_corpora_user";
    public static final String DELETE_CORPORA_ITEM = "deleteCorporaItem";
    public static final String DELETE_CORPORA_ITEM_PROCEDURE = "delete_corpora_item";

    // Corpora Procedure Params
    public static final String CORPORA_ID = "corporaId";
    public static final String ITEM_ID = "itemId";
    public static final String ITEM_TYPE = "itemType";


    // General Procedure Params
    public static final String CREATED_BY = "createdBy";

}
