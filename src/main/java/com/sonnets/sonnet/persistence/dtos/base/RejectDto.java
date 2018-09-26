package com.sonnets.sonnet.persistence.dtos.base;

import javax.validation.constraints.NotEmpty;

/**
 * @author Josh Harkema
 */
public class RejectDto {
    private String id;
    @NotEmpty
    private String rejectMessage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRejectMessage() {
        return rejectMessage;
    }

    public void setRejectMessage(String rejectMessage) {
        this.rejectMessage = rejectMessage;
    }

    @Override
    public String toString() {
        return "RejectDto{" +
                "id=" + id +
                ", rejectMessage='" + rejectMessage + '\'' +
                '}';
    }

}
