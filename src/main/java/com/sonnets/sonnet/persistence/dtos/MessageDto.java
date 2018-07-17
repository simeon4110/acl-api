package com.sonnets.sonnet.persistence.dtos;

import javax.validation.constraints.NotEmpty;

/**
 * POJO for handling message form.
 *
 * @author Josh Harkema
 */
public class MessageDto {
    @NotEmpty
    private String to;
    @NotEmpty
    private String message;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                ", to='" + to + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
