package org.acl.database.persistence.exceptions;

/**
 * @author Josh Harkema
 */
public class AuthorAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 2220517916976746136L;

    public AuthorAlreadyExistsException(String message) {
        super(message);
    }
}
