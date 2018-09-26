package com.sonnets.sonnet.services.exceptions;

public class ItemNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -3398812390235707432L;

    public ItemNotFoundException() {
        super();
    }

    public ItemNotFoundException(String message) {
        super(message);
    }
}
