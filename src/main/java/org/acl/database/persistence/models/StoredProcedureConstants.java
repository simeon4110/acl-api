package org.acl.database.persistence.models;

/**
 * Contains constants for stored procedures.
 */
public abstract class StoredProcedureConstants {
    //
    // # Poem
    public static final String GET_TWO_RANDOM_POEMS = "getTwoRandomPoems";
    public static final String GET_TWO_RANDOM_POEMS_PROCEDURE = "get_two_random_poems";
    public static final String GET_POEM_TO_CONFIRM = "getPoemToConfirm";
    public static final String GET_POEM_TO_CONFIRM_PROCEDURE = "get_poem_to_confirm";

    public static final String USER_NAME_PARAM = "userName";

    private StoredProcedureConstants() {
        // To block implicit public constructor.
    }
}
