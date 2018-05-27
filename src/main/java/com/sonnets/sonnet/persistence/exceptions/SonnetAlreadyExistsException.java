package com.sonnets.sonnet.persistence.exceptions;

/**
 * @author Josh Harkema
 */
public class SonnetAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = -8700821377557407681L;

    public SonnetAlreadyExistsException() {
        super();
    }

    public SonnetAlreadyExistsException(String message) {
        super(message);
    }

    public SonnetAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public SonnetAlreadyExistsException(Throwable cause) {
        super(cause);
    }

    protected SonnetAlreadyExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
