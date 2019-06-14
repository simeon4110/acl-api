package org.acl.database.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ItemNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -3398812390235707432L;

    public ItemNotFoundException() {
        super();
    }

    public ItemNotFoundException(String message) {
        super(message);
    }
}
