package org.acl.database.services.exceptions;

public class NothingToConfirmException extends RuntimeException {

    public NothingToConfirmException() {
        super();
    }

    public NothingToConfirmException(String message) {
        super(message);
    }

    public NothingToConfirmException(String message, Throwable cause) {
        super(message, cause);
    }

    public NothingToConfirmException(Throwable cause) {
        super(cause);
    }
}
