package com.sonnets.sonnet.persistence.dtos.user;

import com.sonnets.sonnet.security.password.ValidPassword;

import javax.validation.constraints.NotEmpty;

public class AdminPasswordResetDto {
    @NotEmpty
    private String username;
    @ValidPassword
    private String password;
    @ValidPassword
    private String password1;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }
}
