package com.sonnets.sonnet.persistence.repositories;

public class RepositoryException extends RuntimeException {
    private static final long serialVersionUID = 7710432333782525749L;

    public RepositoryException() {
        super();
    }

    public RepositoryException(final String message) {
        super(message);
    }
}
