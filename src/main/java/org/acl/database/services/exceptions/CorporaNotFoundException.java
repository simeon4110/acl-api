package org.acl.database.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CorporaNotFoundException extends RuntimeException {
    public CorporaNotFoundException() {
        super();
    }

    public CorporaNotFoundException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
