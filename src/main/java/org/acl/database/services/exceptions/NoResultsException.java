package org.acl.database.services.exceptions;

public class NoResultsException extends RuntimeException {
    private static final long serialVersionUID = -4475311373989658316L;

    public NoResultsException() {
        super();
    }

    public NoResultsException(String message) {
        super(message);
    }
}
