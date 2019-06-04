package org.acl.database.services.exceptions;

public class NothingToConfirmException extends RuntimeException {
    private static final long serialVersionUID = -7083631836934363829L;

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
