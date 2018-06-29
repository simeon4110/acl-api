package com.sonnets.sonnet.persistence.dtos.user;

import javax.validation.constraints.NotEmpty;

/**
 * POJO handles inbound JSON formatted requests to delete a user.
 *
 * @author Josh Harkema
 */
public class AdminUserDeleteDto {
    @NotEmpty
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
