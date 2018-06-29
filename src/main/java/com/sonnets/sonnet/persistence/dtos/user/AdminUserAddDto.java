package com.sonnets.sonnet.persistence.dtos.user;

import com.sonnets.sonnet.security.password.ValidPassword;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * POJO handles inbound JSON formatted requests to add a user.
 *
 * @author Josh Harkema
 */
public class AdminUserAddDto {
    @NotEmpty
    private String username;
    @Email
    private String email;
    @ValidPassword
    private String password;
    @ValidPassword
    private String password1;
    @NotNull
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

    public boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
