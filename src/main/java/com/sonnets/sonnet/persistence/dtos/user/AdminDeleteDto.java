package com.sonnets.sonnet.persistence.dtos.user;

import javax.validation.constraints.NotEmpty;

public class AdminDeleteDto {
    @NotEmpty
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
