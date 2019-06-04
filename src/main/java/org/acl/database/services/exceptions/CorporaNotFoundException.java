package org.acl.database.services.exceptions;

import java.io.Serializable;

public class CorporaNotFoundException extends RuntimeException implements Serializable {
    public CorporaNotFoundException() {
        super();
    }

    public CorporaNotFoundException(String message) {
        super(message);
    }
}
