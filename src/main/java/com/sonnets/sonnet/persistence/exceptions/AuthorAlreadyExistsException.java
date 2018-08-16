package com.sonnets.sonnet.persistence.exceptions;

public class AuthorAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 2220517916976746136L;

    public AuthorAlreadyExistsException(String message) {
        super(message);
    }
}
