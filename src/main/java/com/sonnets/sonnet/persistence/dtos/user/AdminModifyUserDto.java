package com.sonnets.sonnet.persistence.dtos.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class AdminModifyUserDto {
    @NotEmpty
    private String username;
    @Email
    private String email;
    @NotEmpty
    private boolean isAdmin;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
