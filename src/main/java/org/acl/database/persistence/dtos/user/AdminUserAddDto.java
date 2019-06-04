package org.acl.database.persistence.dtos.user;

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
    @NotNull
    private int requiredSonnets;
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

    public int getRequiredSonnets() {
        return requiredSonnets;
    }

    public void setRequiredSonnets(int requiredSonnets) {
        this.requiredSonnets = requiredSonnets;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    @Override
    public String toString() {
        return "AdminUserAddDto{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", requiredSonnets=" + requiredSonnets +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
