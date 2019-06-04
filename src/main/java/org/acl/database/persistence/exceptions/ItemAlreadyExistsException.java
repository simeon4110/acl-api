package org.acl.database.persistence.exceptions;

/**
 * @author Josh Harkema
 */
public class ItemAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = -8700821377557407681L;

    public ItemAlreadyExistsException(String message) {
        super(message);
    }
}
