package com.sonnets.sonnet.persistence.dtos.user;

import javax.validation.constraints.NotEmpty;

/**
 * @author Josh Harkema
 */
@SuppressWarnings("ALL")
public class UserModifyDto {
    @NotEmpty
    private String username;
    private boolean admin;
    private boolean delete;
    private boolean resetPassword;
    private String passwordReset;
    private String passwordReset1;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isResetPassword() {
        return resetPassword;
    }

    public void setResetPassword(boolean resetPassword) {
        this.resetPassword = resetPassword;
    }

    public String getPasswordReset() {
        return passwordReset;
    }

    public void setPasswordReset(String passwordReset) {
        this.passwordReset = passwordReset;
    }

    public String getPasswordReset1() {
        return passwordReset1;
    }

    public void setPasswordReset1(String passwordReset1) {
        this.passwordReset1 = passwordReset1;
    }

    @Override
    public String toString() {
        return "UserModifyDto{" +
                "username='" + username + '\'' +
                ", admin=" + admin +
                ", delete=" + delete +
                ", resetPassword=" + resetPassword +
                ", passwordReset='" + passwordReset + '\'' +
                ", passwordReset1='" + passwordReset1 + '\'' +
                '}';
    }
}
