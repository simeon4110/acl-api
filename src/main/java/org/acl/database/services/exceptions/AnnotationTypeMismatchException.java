package org.acl.database.services.exceptions;

public class AnnotationTypeMismatchException extends RuntimeException {

    public AnnotationTypeMismatchException() {
        super();
    }

    public AnnotationTypeMismatchException(String message) {
        super(message);
    }
}
