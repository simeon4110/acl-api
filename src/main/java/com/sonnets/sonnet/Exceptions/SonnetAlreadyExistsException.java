package com.sonnets.sonnet.Exceptions;

/**
 * @author Josh Harkema
 */
public class SonnetAlreadyExistsException extends RuntimeException {
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
