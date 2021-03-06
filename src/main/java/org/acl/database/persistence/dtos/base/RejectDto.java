package org.acl.database.persistence.dtos.base;

import javax.validation.constraints.NotEmpty;

/**
 * @author Josh Harkema
 */
public class RejectDto {
    private Long id;
    @NotEmpty
    private String rejectMessage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
