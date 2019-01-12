package com.sonnets.sonnet.persistence.models;

/**
 * Contains constants for stored procedures and item types.
 */
public abstract class ModelConstants {
    private ModelConstants() {
        // To block implicit public constructor.
    }

    // Named Queries
    // Section
    public static final String GET_ALL_SECTIONS = "getAllSections";
    public static final String GET_ALL_SECTIONS_PROCEDURE = "get_all_sections";
    public static final String GET_SECTIONS_BY_USER = "getSectionsByUser";
    public static final String GET_SECTIONS_BY_USER_PROCEDURE = "get_user_sections";
    public static final String GET_BOOK_SECTIONS_SIMPLE = "getBookSectionsSimple";
    public static final String GET_BOOK_SECTIONS_SIMPLE_PROCEDURE = "get_book_sections_simple";

    // Items
    public static final String GET_USER_ITEMS = "getUserItems";
    public static final String GET_USER_ITEMS_PROCEDURE = "get_user_items";
    public static final String GET_ALL_ITEMS = "getAllItems";
    public static final String GET_ALL_ITEMS_PROCEDURE = "get_all_items";

    // Book
    public static final String GET_BOOK_TITLE = "getBookTitle";
    public static final String GET_BOOK_TITLE_PARAM = "get_book_title";
    public static final String GET_BOOKS_SIMPLE = "getBooksSimple";
    public static final String GET_BOOKS_SIMPLE_PARAM = "get_books_simple";
    public static final String GET_BOOK_CHARACTERS = "getBookCharacters";
    public static final String GET_BOOK_CHARACTERS_PARAM = "get_book_characters";

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
    public static final String CREATED_BY_PARAM = "createdBy";
    public static final String USER_NAME_PARAM = "userName";
    public static final String FORM_PARAM = "form";
    public static final String OUTPUT_PARAM = "output";

    // Book Procedure Params
    public static final String BOOK_ID_PARAM = "bookId";
    public static final String BOOK_TITLE_PARAM = "title";

    // Column Definitions
    public static final String BIG_STRING = "NVARCHAR(MAX)";

}
