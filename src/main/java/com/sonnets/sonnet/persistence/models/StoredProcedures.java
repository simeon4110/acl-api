package com.sonnets.sonnet.persistence.models;

/**
 * Contains constants for stored procedures.
 */
public abstract class StoredProcedures {
    // ## Stored Procedures
    // # Section
    public static final String GET_BOOK_SECTIONS_SIMPLE = "getBookSectionsSimple";
    //
    // # Item
    public static final String GET_ALL_USER_ITEMS = "getAllUserItems";
    public static final String GET_BOOK_SECTIONS_SIMPLE_PROCEDURE = "get_book_sections_simple";
    public static final String GET_ALL_USER_ITEMS_PROCEDURE = "get_all_user_items";
    //
    // # Book
    public static final String GET_ALL_BOOKS_SIMPLE = "getAllBooksSimple";
    public static final String GET_ALL_ITEMS = "getAllItems";
    public static final String GET_ALL_ITEMS_PROCEDURE = "get_all_items";
    public static final String GET_ALL_BOOKS_SIMPLE_PROCEDURE = "get_all_books_simple";
    public static final String GET_ALL_BOOKS_SIMPLE_PDO = "getAllBooksSimplePDO";
    public static final String GET_ALL_BOOKS_SIMPLE_PDO_PROCEDURE = "get_all_books_simple_PDO";
    //
    // # Poem
    public static final String GET_ALL_POEMS_SIMPLE = "getAllPoemsSimple";
    public static final String GET_ALL_POEMS_SIMPLE_PROCEDURE = "get_all_poems_simple";
    public static final String GET_ALL_POEMS_SIMPLE_PDO = "getAllPoemsSimplePDO";
    public static final String GET_ALL_POEMS_SIMPLE_PDO_PROCEDURE = "get_all_poems_simple_PDO";
    public static final String GET_TWO_RANDOM_POEMS = "getTwoRandomPoems";
    public static final String GET_TWO_RANDOM_POEMS_PROCEDURE = "get_two_random_poems";
    //
    // # Section
    public static final String GET_ALL_SECTIONS_SIMPLE = "getAllSectionsSimple";
    public static final String GET_ALL_SECTIONS_SIMPLE_PROCEDURE = "get_all_sections_simple";
    public static final String GET_ALL_SECTIONS_SIMPLE_PDO = "getAllSectionsSimplePDO";
    public static final String GET_ALL_SECTIONS_SIMPLE_PDO_PROCEDURE = "get_all_sections_simple_pdo";
    //
    // # Corpora
    public static final String ADD_CORPORA_ITEM = "addCorporaItem";
    //
    // ## Procedure Params
    // # Corpora Procedure Params
    public static final String CORPORA_ID = "corporaId";
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
    //
    // # General Procedure Params
    public static final String CREATED_BY_PARAM = "createdBy";
    public static final String ITEM_ID = "itemId";
    public static final String ITEM_TYPE = "itemType";
    //
    // # Book Procedure Params
    public static final String BOOK_ID_PARAM = "bookId";
    public static final String USER_NAME_PARAM = "userName";

    private StoredProcedures() {
        // To block implicit public constructor.
    }

}
