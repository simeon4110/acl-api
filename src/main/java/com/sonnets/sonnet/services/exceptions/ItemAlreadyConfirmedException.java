package com.sonnets.sonnet.services.exceptions;

import java.io.Serializable;

public class ItemAlreadyConfirmedException extends RuntimeException implements Serializable {
    private static final long serialVersionUID = -1879938924601341871L;

    public ItemAlreadyConfirmedException() {
        super();
    }

    public ItemAlreadyConfirmedException(String message) {
        super(message);
    }
}
