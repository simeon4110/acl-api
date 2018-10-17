package com.sonnets.sonnet.services.exceptions;

public class AnnotationTypeMismatchException extends RuntimeException {
    private static final long serialVersionUID = -4437769766414039095L;

    public AnnotationTypeMismatchException() {
        super();
    }

    public AnnotationTypeMismatchException(String message) {
        super(message);
    }
}
