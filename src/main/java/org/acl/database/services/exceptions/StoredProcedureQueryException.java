package org.acl.database.services.exceptions;

public class StoredProcedureQueryException extends RuntimeException {
    private static final long serialVersionUID = 2276831440171971285L;

    public StoredProcedureQueryException() {
        super();
    }

    public StoredProcedureQueryException(String message) {
        super(message);
    }

    public StoredProcedureQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public StoredProcedureQueryException(Throwable cause) {
        super(cause);
    }

    protected StoredProcedureQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}