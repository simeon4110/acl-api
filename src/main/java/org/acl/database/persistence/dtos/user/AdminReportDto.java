package org.acl.database.persistence.dtos.user;

import javax.validation.constraints.NotEmpty;

/**
 * POJO for handling JSON request bodies for admin reports.
 *
 * @author Josh Harkema
 */
public class AdminReportDto {
    @NotEmpty
    private String username;
    @NotEmpty
    private String after;
    @NotEmpty
    private String before;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }
}
