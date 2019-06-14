package org.acl.database.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ItemAlreadyConfirmedException extends RuntimeException {

    public ItemAlreadyConfirmedException() {
        super();
    }

    public ItemAlreadyConfirmedException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
